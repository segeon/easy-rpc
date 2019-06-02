package com.segeon.easyrpc.core.domain.entity;

import com.google.common.base.Preconditions;
import com.segeon.easyrpc.core.domain.entity.impl.RemotingServiceImpl;
import lombok.Getter;
import lombok.Setter;


public class ServiceConfig {
    private ApplicationConfig config;
    @Getter
    @Setter
    private Class<?> interfaceType;
    @Getter
    @Setter
    private Object refImpl;
    @Getter
    @Setter
    private String group = "";
    @Getter
    @Setter
    private String version = "";

    public ServiceConfig(ApplicationConfig config) {
        this.config = config;
    }

    public void export() {
        Preconditions.checkNotNull(interfaceType);
        Preconditions.checkNotNull(refImpl);
        Preconditions.checkNotNull(group);
        Preconditions.checkNotNull(version);
        config.check();
        ServiceManager serviceManager = config.getServiceManager();
        RemotingServiceImpl remotingService = new RemotingServiceImpl(interfaceType, refImpl, group, version);
        synchronized (config) {
            while (!config.isInited()) {
                try {
                    config.wait(10);
                } catch (Exception e) {
                }
            }
        }
        serviceManager.register(remotingService);
    }
}
