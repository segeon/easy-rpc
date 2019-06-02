package com.segeon.easyrpc.core.domain.entity.impl;

import com.segeon.easyrpc.core.domain.entity.*;
import com.segeon.easyrpc.core.domain.exception.RPCServerException;
import com.segeon.easyrpc.core.netty.NettyRPCServer;
import com.segeon.easyrpc.core.utils.IpUtil;

import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceManagerImpl implements ServiceManager {
    private ApplicationConfig applicationConfig;
    private String localIp;
    private ConcurrentHashMap<String, RemotingService> services;
    private RPCServer server;

    public ServiceManagerImpl(ApplicationConfig config) {
        this.applicationConfig = config;
        this.services = new ConcurrentHashMap<>();
        try {
            this.localIp = IpUtil.getRealIp();
        } catch (SocketException e) {
            throw new RPCServerException("获取本机ip异常！");
        }
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
        services.put(RemotingService.genServiceKey(remotingService), remotingService);
        applicationConfig.getServiceRegistry().register(remotingService);
    }

    @Override
    public void unregister(RemotingService remotingService) {
        applicationConfig.getServiceRegistry().unRegister(remotingService);
        //TODO: 实现优雅关机
        services.remove(RemotingService.genServiceKey(remotingService));
    }

    @Override
    public RemotingService lookup(String interfaceName, String group, String version) {
        return services.get(RemotingService.genServiceKey(interfaceName, group, version));
    }
}
