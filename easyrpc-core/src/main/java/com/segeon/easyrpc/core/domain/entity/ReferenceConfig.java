package com.segeon.easyrpc.core.domain.entity;

import com.google.common.base.Preconditions;
import com.segeon.easyrpc.core.domain.invocation.client.ReferenceInvocationHandler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Proxy;

@ToString
@EqualsAndHashCode(of = {"url", "interfaceType", "group", "version"})
public class ReferenceConfig<T> {
    @ToString.Exclude
    private ApplicationConfig config;
    @Getter
    @Setter
    private String url;
    @Getter
    @Setter
    private Class<T> interfaceType;
    @Getter
    @Setter
    private String group = "";
    @Getter
    @Setter
    private String version = "";

    public ReferenceConfig(ApplicationConfig config) {
        this.config = config;
    }

    public T refer() {
        Preconditions.checkNotNull(interfaceType);
        Preconditions.checkNotNull(group);
        Preconditions.checkNotNull(version);
        config.check();
        T o = (T)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{interfaceType}, new ReferenceInvocationHandler(config.getRpcClient(), this));
        return o;
    }
}
