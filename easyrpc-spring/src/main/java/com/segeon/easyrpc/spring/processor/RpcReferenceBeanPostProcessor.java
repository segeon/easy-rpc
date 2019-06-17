package com.segeon.easyrpc.spring.processor;

import com.segeon.easyrpc.core.domain.entity.ApplicationConfig;
import com.segeon.easyrpc.spring.RpcReferenceFactoryBean;
import com.segeon.easyrpc.spring.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RpcReferenceBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements BeanFactoryAware {

    private ListableBeanFactory beanFactory;
    @Autowired
    private ApplicationConfig applicationConfig;
    private final Map<String, InjectionMetadata> injectionMetadataCache =
            new ConcurrentHashMap<String, InjectionMetadata>(256);

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        }
        catch (BeanCreationException ex) {
            throw ex;
        }
        catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of RpcReference dependencies failed", ex);
        }
        return pvs;
    }

    private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
        // Fall back to class name as cache key, for backwards compatibility with custom callers.
        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
        // Quick check on the concurrent map first, with minimal locking.
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    try {
                        metadata = buildAutowiringMetadata(clazz);
                        this.injectionMetadataCache.put(cacheKey, metadata);
                    }
                    catch (NoClassDefFoundError err) {
                        throw new IllegalStateException("Failed to introspect bean class [" + clazz.getName() +
                                "] for autowiring metadata: could not find class that it depends on", err);
                    }
                }
            }
        }
        return metadata;
    }

    private AnnotationAttributes findRpcReferenceAnnotation(AccessibleObject ao) {
        if (ao.getAnnotations().length > 0) {
            AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(ao, RpcReference.class);
            if (attributes != null) {
                return attributes;
            }
        }
        return null;
    }

    private InjectionMetadata buildAutowiringMetadata(final Class<?> clazz) {
        LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
        Class<?> targetClass = clazz;

        do {
            final LinkedList<InjectionMetadata.InjectedElement> currElements =
                    new LinkedList<InjectionMetadata.InjectedElement>();

            ReflectionUtils.doWithLocalFields(targetClass, new ReflectionUtils.FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    AnnotationAttributes ann = findRpcReferenceAnnotation(field);
                    if (ann != null) {
                        if (Modifier.isStatic(field.getModifiers())) {
                            if (log.isWarnEnabled()) {
                                log.warn("RpcReference annotation is not supported on static fields: " + field);
                            }
                            return;
                        }
                        if (!Modifier.isInterface(field.getType().getModifiers())) {
                            if (log.isWarnEnabled()) {
                                log.warn("RpcReference annotation is not supported on non Interface fields: " + field);
                            }
                            return;
                        }

                        currElements.add(new RpcReferenceInjectElement(field, null, ann, field.getType()));
                    }
                }
            });

            ReflectionUtils.doWithLocalMethods(targetClass, new ReflectionUtils.MethodCallback() {
                @Override
                public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                    Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
                    if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                        return;
                    }
                    AnnotationAttributes ann = findRpcReferenceAnnotation(bridgedMethod);
                    if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                        if (Modifier.isStatic(method.getModifiers())) {
                            if (log.isWarnEnabled()) {
                                log.warn("RpcReference annotation is not supported on static methods: " + method);
                            }
                            return;
                        }
                        if (method.getParameterTypes().length != 1) {
                            if (log.isWarnEnabled()) {
                                log.warn("RpcReference annotation should only be used on methods with one parameter: " +
                                        method);
                            }
                        }
                        if (!Modifier.isInterface(method.getParameterTypes()[0].getModifiers())) {
                            if (log.isWarnEnabled()) {
                                log.warn("RpcReference annotation should only be used on methods with interface parameter: " +
                                        method);
                            }
                        }
                        PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                        currElements.add(new RpcReferenceInjectElement(method, pd, ann, method.getParameterTypes()[0]));
                    }
                }
            });

            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);
        return new InjectionMetadata(clazz, elements);
    }

    private class RpcReferenceInjectElement extends InjectionMetadata.InjectedElement {
        private AnnotationAttributes ann;
        private Class interfaceType;

        public RpcReferenceInjectElement(Member member, PropertyDescriptor pd, AnnotationAttributes attributes, Class type) {
            super(member, pd);
            this.ann = attributes;
            this.interfaceType = type;
        }

        @Override
        protected Object getResourceToInject(Object target, String requestingBeanName) {
            String group = ann.getString("group");
            if (group != null) {
                group = group.trim();
            }
            String version = ann.getString("version");
            if (version != null) {
                version = version.trim();
            }
            String url = ann.getString("url");
            if (url != null) {
                url = url.trim();
            }
            RpcReferenceFactoryBean<Object> referenceFactoryBean = new RpcReferenceFactoryBean<>(group, version, url, interfaceType, RpcReferenceBeanPostProcessor.this.applicationConfig);
            String beanName = referenceFactoryBean.beanName();
            boolean hasBean = beanFactory.containsBean(beanName);
            Object bean;
            if (!hasBean) {
                ((SingletonBeanRegistry) RpcReferenceBeanPostProcessor.this.beanFactory).registerSingleton(beanName, referenceFactoryBean);
            }
            bean = beanFactory.getBean(beanName);
            return bean;
        }
    }
}
