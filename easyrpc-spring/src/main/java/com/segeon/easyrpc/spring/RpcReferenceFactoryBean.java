package com.segeon.easyrpc.spring;

import com.segeon.easyrpc.core.domain.entity.ApplicationConfig;
import com.segeon.easyrpc.core.domain.entity.ReferenceConfig;
import com.segeon.easyrpc.spring.utils.ReferenceBeanNameUtil;
import org.springframework.beans.factory.FactoryBean;

public class RpcReferenceFactoryBean<T> extends ReferenceConfig<T> implements FactoryBean<T> {

    public RpcReferenceFactoryBean(String group, String version, String url, Class interfaceType, ApplicationConfig config) {
        super(config);
        setGroup(group);
        setVersion(version);
        setInterfaceType(interfaceType);
        setUrl(url);
    }

    @Override
    public T getObject() throws Exception {
        return refer();
    }

    @Override
    public Class<?> getObjectType() {
        return getInterfaceType();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String beanName() {
        return ReferenceBeanNameUtil.beanName(this);
    }
}
