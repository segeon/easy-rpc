package com.segeon.easyrpc.core.domain.entity.impl;

import com.segeon.easyrpc.core.domain.entity.*;
import com.segeon.easyrpc.core.domain.exception.RPCClientException;
import com.segeon.easyrpc.core.netty.NettyChannel;
import com.segeon.easyrpc.core.utils.CollectionUtil;
import com.segeon.easyrpc.core.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ChannelManagerImpl implements ChannelManager, EndpointChangeListener {
    private ApplicationConfig applicationConfig;
    private ConcurrentHashMap<Channel, List<ReferenceConfig>> channel2ReferenceMap;
    private ConcurrentHashMap<ReferenceConfig, ReferenceChannelTuple> reference2ChannelMap;
    private ConcurrentHashMap<ReferenceConfig, Boolean> watchedReference;

    public ChannelManagerImpl(ApplicationConfig config) {
        applicationConfig = config;
        channel2ReferenceMap = new ConcurrentHashMap<>();
        reference2ChannelMap = new ConcurrentHashMap<>();
        watchedReference = new ConcurrentHashMap<>();
    }

    @Override
    public List<Endpoint> resolveProviders(ReferenceConfig referenceConfig) {
        return applicationConfig.getServiceRegistry().list(referenceConfig);
    }

    @Override
    public void registerAndConnectChannel(ReferenceConfig referenceConfig, Channel channel) {
        try {
            ReferenceChannelTuple tuple = reference2ChannelMap.get(referenceConfig);
            if (tuple != null) {
                if (!tuple.getChannels().contains(channel)) {
                    channel.connect();
                    List<ReferenceConfig> referenceConfigs = channel2ReferenceMap.getOrDefault(channel, new ArrayList<>(16));
                    referenceConfigs.add(referenceConfig);
                    channel2ReferenceMap.put(channel, referenceConfigs);
                    tuple.getChannels().add(channel);
                    reference2ChannelMap.put(referenceConfig, tuple);
                }
            } else {
                channel.connect();
                List<ReferenceConfig> referenceConfigs = channel2ReferenceMap.getOrDefault(channel, new ArrayList<>(16));
                referenceConfigs.add(referenceConfig);
                channel2ReferenceMap.put(channel, referenceConfigs);

                ReferenceChannelTuple channelTuple = new ReferenceChannelTuple();
                channelTuple.setReferenceConfig(referenceConfig);
                ArrayList<Channel> channels = new ArrayList<>(16);
                channels.add(channel);
                channelTuple.setChannels(channels);
                reference2ChannelMap.put(referenceConfig, channelTuple);
            }
        } catch (Exception e) {
            log.error("注册{}的channel({})异常!", referenceConfig, channel, e);
        }
    }

    @Override
    public void registerAndConnectChannel(ReferenceConfig referenceConfig, List<Channel> channels) {
        for (Channel channel : channels) {
            registerAndConnectChannel(referenceConfig, channel);
        }
    }

    @Override
    public void removeChannel(Channel channel) {
        List<ReferenceConfig> removedReference = channel2ReferenceMap.remove(channel);
        for (ReferenceConfig config : removedReference) {
            ReferenceChannelTuple tuple = reference2ChannelMap.get(config);
            if (tuple != null && !tuple.getChannels().isEmpty()) {
                tuple.getChannels().remove(channel);
            }
        }
    }

    @Override
    public List<Channel> availableChannels(ReferenceConfig referenceConfig) {
        ReferenceChannelTuple tuple = reference2ChannelMap.get(referenceConfig);
        if (null == tuple || CollectionUtil.isEmpty(tuple.getChannels())) {
            List<Endpoint> endpoints = resolveProviders(referenceConfig);
            if (CollectionUtil.isEmpty(endpoints)) {
                throw new RPCClientException("没有可用的provider!" + referenceConfig);
            }
            if (shouldWatch(referenceConfig)) {
                synchronized (watchedReference) {
                    if (!watchedReference.containsKey(referenceConfig)) {
                        applicationConfig.getServiceRegistry().subscribe(referenceConfig, this);
                        watchedReference.put(referenceConfig, true);
                    }
                }
            }
            for (Endpoint e : endpoints) {
                NettyChannel nettyChannel = new NettyChannel(applicationConfig, e.ip(), e.port());
                registerAndConnectChannel(referenceConfig, nettyChannel);
            }
        }
        tuple = reference2ChannelMap.get(referenceConfig);
        if (null == tuple || CollectionUtil.isEmpty(tuple.getChannels())) {
            throw new RPCClientException("没有可用的Channel!" + referenceConfig);
        }
        return tuple.getChannels();
    }

    @Override
    public void notify(ServiceKey key, List<Endpoint> newEndpoints, List<Endpoint> deletedEndpoints) {
        ReferenceConfig referenceConfig = new ReferenceConfig(applicationConfig);
        referenceConfig.setInterfaceType(key.interfaceType());
        referenceConfig.setGroup(key.group());
        referenceConfig.setVersion(key.version());
        ReferenceChannelTuple tuple = reference2ChannelMap.get(referenceConfig);
        List<Channel> channels = newEndpoints.stream().map(e -> new NettyChannel(applicationConfig, e.ip(), e.port())).collect(Collectors.toList());
        registerAndConnectChannel(tuple.getReferenceConfig(), channels);
        for (Endpoint deletedEndpoint : deletedEndpoints) {
            removeChannel(new NettyChannel(applicationConfig, deletedEndpoint.ip(), deletedEndpoint.port()));
        }
    }

    private boolean shouldWatch(ReferenceConfig referenceConfig) {
        return !StringUtils.hasText(referenceConfig.getUrl());
    }

    @Getter
    @Setter
    private static class ReferenceChannelTuple {
        private ReferenceConfig referenceConfig;
        private List<Channel> channels;
    }
}
