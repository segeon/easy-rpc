package com.segeon.easyrpc.core.domain.entity;

import com.google.common.base.Preconditions;
import com.segeon.easyrpc.core.domain.entity.impl.*;
import com.segeon.easyrpc.core.domain.exception.RPCServerException;
import com.segeon.easyrpc.core.netty.NettyRPCClient;
import com.segeon.easyrpc.core.utils.IpUtil;
import com.segeon.easyrpc.core.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.net.SocketException;


public class ApplicationConfig {
    private volatile boolean inited = false;

    @Getter
    @Setter
    private String name;
    @Getter
    private String localIp;
    @Getter
    @Setter
    private int port;
    @Getter
    @Setter
    private RegistryConfig registryConfig;
    private ServiceManager serviceManager;
    private ChannelManager channelManager;
    private ServiceRegistry serviceRegistry;
    private LoadBalancePolicy loadBalancePolicy;
    private RPCClient rpcClient;

    public boolean isInited() {
        return inited;
    }

    public synchronized void init() {
        if (!inited) {
            Preconditions.checkState(StringUtils.hasText(name), "应用名必须不为空");
            Preconditions.checkState(port > 0, "端口号必须大于0");
            Preconditions.checkNotNull(registryConfig);
            Preconditions.checkState(StringUtils.hasText(registryConfig.getIp()), "注册中心ip不能为空");
            Preconditions.checkState(registryConfig.getPort() > 0, "注册中心端口号必须大于0");
            try {
                this.localIp = IpUtil.getRealIp();
            } catch (SocketException e) {
                throw new RPCServerException("获取本机ip异常！");
            }
            loadBalancePolicy = new RandomLoadBalancePolicy();
            serviceRegistry = new ServiceRegistryResolver();
            serviceRegistry.init(this);
            serviceManager = new ServiceManagerImpl(this);
            channelManager = new ChannelManagerImpl(this);
            rpcClient = new NettyRPCClient(this);
            inited = true;
            notifyAll();
        }
    }

    public ServiceManager getServiceManager() {
        check();
        return serviceManager;
    }

    public ChannelManager getChannelManager() {
        check();
        return channelManager;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public LoadBalancePolicy getLoadBalancePolicy() {
        return loadBalancePolicy;
    }

    public RPCClient getRpcClient() {
        return rpcClient;
    }

    public void check() {
        if (!inited) {
            throw new IllegalStateException("ApplicationConfig尚未初始化！");
        }
    }
}
