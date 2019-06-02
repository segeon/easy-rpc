package com.segeon.easyrpc.core.domain.entity.impl;

import com.segeon.easyrpc.core.domain.entity.*;
import com.segeon.easyrpc.core.domain.exception.RPCClientException;
import com.segeon.easyrpc.core.netty.NettyChannel;
import com.segeon.easyrpc.core.utils.CollectionUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManagerImpl implements ChannelManager {
    private ApplicationConfig applicationConfig;
    private ConcurrentHashMap<Channel, List<ReferenceConfig>> channel2ReferenceMap;
    private ConcurrentHashMap<ReferenceConfig, List<Channel>> reference2ChannelMap;

    public ChannelManagerImpl(ApplicationConfig config) {
        applicationConfig = config;
        channel2ReferenceMap = new ConcurrentHashMap<>();
        reference2ChannelMap = new ConcurrentHashMap<>();
    }

    @Override
    public List<Endpoint> resolveProviders(ReferenceConfig referenceConfig) {
        return applicationConfig.getServiceRegistry().list(referenceConfig);
    }

    @Override
    public void registerChannel(ReferenceConfig referenceConfig, Channel channel) {
        List<ReferenceConfig> referenceConfigs = channel2ReferenceMap.getOrDefault(channel, new ArrayList<>(64));
        referenceConfigs.add(referenceConfig);
        channel2ReferenceMap.put(channel, referenceConfigs);
        List<Channel> channels = reference2ChannelMap.getOrDefault(referenceConfig, new ArrayList<>(64));
        channels.add(channel);
        reference2ChannelMap.put(referenceConfig, channels);
    }

    @Override
    public void registerChannel(ReferenceConfig referenceConfig, List<Channel> channels) {
        for (Channel channel : channels) {
            List<ReferenceConfig> referenceConfigs = channel2ReferenceMap.getOrDefault(channel, new ArrayList<>(64));
            referenceConfigs.add(referenceConfig);
            channel2ReferenceMap.put(channel, referenceConfigs);
        }
        List<Channel> mapped = reference2ChannelMap.getOrDefault(referenceConfig, new ArrayList<>(64));
        mapped.addAll(channels);
        reference2ChannelMap.put(referenceConfig, mapped);
    }

    @Override
    public void removeChannel(Channel channel) {
        List<ReferenceConfig> removedReference = channel2ReferenceMap.remove(channel);
        for (ReferenceConfig config : removedReference) {
            List<Channel> channels = reference2ChannelMap.get(config);
            if (!channels.isEmpty()) {
                channels.remove(channel);
            }
        }
    }

    @Override
    public List<Channel> availableChannels(ReferenceConfig referenceConfig) {
        List<Channel> channels = reference2ChannelMap.get(referenceConfig);
        if (CollectionUtil.isEmpty(channels)) {
            List<Endpoint> endpoints = resolveProviders(referenceConfig);
            if (CollectionUtil.isEmpty(endpoints)) {
                throw new RPCClientException("没有可用的provider!" + referenceConfig);
            }
            for (Endpoint e : endpoints) {
                NettyChannel nettyChannel = new NettyChannel(applicationConfig, e.ip(), e.port());
                nettyChannel.connect();
                registerChannel(referenceConfig, nettyChannel);
            }
        }
        channels = reference2ChannelMap.get(referenceConfig);
        return channels;
    }
}
