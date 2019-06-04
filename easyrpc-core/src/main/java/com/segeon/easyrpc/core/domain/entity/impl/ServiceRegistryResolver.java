package com.segeon.easyrpc.core.domain.entity.impl;

import com.google.common.base.Preconditions;
import com.segeon.easyrpc.core.domain.entity.*;
import com.segeon.easyrpc.core.domain.exception.RegistrationException;

import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;

public class ServiceRegistryResolver implements ServiceRegistry {
    private ApplicationConfig config;
    private String registryType;
    private ServiceRegistry target;

    @Override
    public void init(ApplicationConfig applicationConfig) {
        this.config = applicationConfig;
        this.registryType = config.getRegistryConfig().getSchema();
        Preconditions.checkNotNull(this.registryType);
        ServiceLoader<ServiceRegistry> loader = ServiceLoader.load(ServiceRegistry.class);
        for (ServiceRegistry registry : loader) {
            if (this.registryType.equals(registry.registryType())) {
                target = registry;
                target.init(config);
                break;
            }
        }
        if (null == target) {
            throw new RegistrationException("没有找到" + registryType + "类型的注册中心实现");
        }
    }

    @Override
    public String registryType() {
        return config.getRegistryConfig().getSchema();
    }

    @Override
    public void register(RemotingService remotingService) {
        target.register(remotingService);
    }

    @Override
    public void unRegister(RemotingService remotingService) {
        target.unRegister(remotingService);
    }

    @Override
    public void subscribe(ReferenceConfig remotingService, EndpointChangeListener listener) {
        target.subscribe(remotingService, listener);
    }

    @Override
    public void unsubscribe(ReferenceConfig remotingService) {
        target.unsubscribe(remotingService);
    }

    @Override
    public List<Endpoint> list(ReferenceConfig remotingService) {
        return target.list(remotingService);
    }

    @Override
    public String ip() {
        return null;
    }

    @Override
    public int port() {
        return 0;
    }

    @Override
    public void close() throws IOException {
        target.close();
    }
}
