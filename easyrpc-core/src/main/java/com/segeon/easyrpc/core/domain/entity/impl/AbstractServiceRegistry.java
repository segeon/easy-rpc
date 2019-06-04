package com.segeon.easyrpc.core.domain.entity.impl;

import com.segeon.easyrpc.core.domain.entity.*;
import com.segeon.easyrpc.core.utils.StringUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractServiceRegistry implements ServiceRegistry {

    protected ApplicationConfig config;
    protected ConcurrentMap<ServiceKey, EndpointChangeListener> listenerMap;

    public AbstractServiceRegistry() {
        this.listenerMap = new ConcurrentHashMap<>();
    }

    @Override
    public void init(ApplicationConfig applicationConfig) {
        this.config = applicationConfig;
        doInit();
    }

    protected abstract void doInit();


    @Override
    public void subscribe(ReferenceConfig remotingService, EndpointChangeListener listener) {
        if (StringUtils.hasText(remotingService.getUrl())) {
            return;
        }
        ServiceKey serviceKey = SimpleServiceKey.fromReferenceConfig(remotingService);
        listenerMap.put(serviceKey, listener);
        doSubscribe(serviceKey);
    }

    abstract protected void doSubscribe(ServiceKey remotingService);

    @Override
    public void unsubscribe(ReferenceConfig remotingService) {
        ServiceKey serviceKey = SimpleServiceKey.fromReferenceConfig(remotingService);
        doUnsubscribe(serviceKey);
        listenerMap.remove(serviceKey);
    }

    abstract protected void doUnsubscribe(ServiceKey config);

    protected void onEndpointsChange(ServiceKey serviceKey, List<Endpoint> endpoints) {
        EndpointChangeListener listener = listenerMap.get(serviceKey);
        if (listener != null) {
            listener.notify(serviceKey, endpoints);
        }
    }

    @Override
    public String ip() {
        return config.getRegistryConfig().getIp();
    }

    @Override
    public int port() {
        return config.getRegistryConfig().getPort();
    }
}
