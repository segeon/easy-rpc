package com.segeon.easyrpc.spring.processor;

import com.segeon.easyrpc.core.domain.entity.ApplicationConfig;
import com.segeon.easyrpc.core.domain.entity.ServiceConfig;
import com.segeon.easyrpc.spring.annotation.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;

import java.lang.reflect.AnnotatedElement;

public class RpcServiceBeanPostProcessor implements BeanPostProcessor {
    @Autowired
    private ApplicationConfig applicationConfig;

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        AnnotationAttributes attributes = findRpcServiceAnnotation(bean.getClass());
        if (null == attributes) {
            return bean;
        }
        ServiceConfig config = new ServiceConfig(applicationConfig);
        config.setRefImpl(bean);
        config.setGroup(attributes.getString("group"));
        config.setVersion(attributes.getString("version"));
        config.setInterfaceType(attributes.getClass("interfaceType"));
        config.export();
        return bean;
    }

    private AnnotationAttributes findRpcServiceAnnotation(AnnotatedElement ao) {
        if (ao.getAnnotations().length > 0) {
            AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(ao, RpcService.class);
            if (attributes != null) {
                return attributes;
            }
        }
        return null;
    }
}
