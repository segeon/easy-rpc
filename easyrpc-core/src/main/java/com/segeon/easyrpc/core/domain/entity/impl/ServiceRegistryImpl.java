package com.segeon.easyrpc.core.domain.entity.impl;

import com.segeon.easyrpc.core.domain.entity.*;
import com.segeon.easyrpc.core.utils.IpUtil;

import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

public class ServiceRegistryImpl implements ServiceRegistry {

    private ApplicationConfig config;

    public ServiceRegistryImpl(ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public void register(RemotingService remotingService) {

    }

    @Override
    public void unRegister(RemotingService remotingService) {

    }

    @Override
    public void subscribe(ReferenceConfig remotingService) {

    }

    @Override
    public void unsubscribe(ReferenceConfig remotingService) {

    }

    @Override
    public List<Endpoint> list(ReferenceConfig remotingService) {
        try {
            return Arrays.asList(new SimpleEndPoint(IpUtil.getRealIp(), 1121));
        } catch (SocketException e) {
            throw new RuntimeException(e);
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
