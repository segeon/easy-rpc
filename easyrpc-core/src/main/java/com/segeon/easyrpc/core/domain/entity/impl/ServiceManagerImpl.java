package com.segeon.easyrpc.core.domain.entity.impl;

import com.segeon.easyrpc.core.domain.entity.*;
import com.segeon.easyrpc.core.netty.NettyRPCServer;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceManagerImpl implements ServiceManager {
    private ApplicationConfig applicationConfig;
    private String localIp;
    private ConcurrentHashMap<String, RemotingService> services;
    private RPCServer server;

    public ServiceManagerImpl(ApplicationConfig config) {
        this.applicationConfig = config;
        this.services = new ConcurrentHashMap<>();
        this.localIp = config.getLocalIp();
        this.server = new NettyRPCServer(applicationConfig);
        this.server.initialize();
    }

    @Override
    public String ip() {
        return this.localIp;
    }

    @Override
    public int port() {
        return applicationConfig.getPort();
    }

    @Override
    public void register(RemotingService remotingService) {
        services.put(remotingService.getKey(), remotingService);
        applicationConfig.getServiceRegistry().register(remotingService);
    }

    @Override
    public void unregister(RemotingService remotingService) {
        applicationConfig.getServiceRegistry().unRegister(remotingService);
        //TODO: 实现优雅关机
        services.remove(remotingService.getKey());
    }

    @Override
    public RemotingService lookup(String interfaceName, String group, String version) {
        return services.get(ServiceKey.genKey(interfaceName, group, version));
    }
}
