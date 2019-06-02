package com.segeon.easyrpc.core.domain.entity.impl;

import com.segeon.easyrpc.core.domain.entity.Channel;
import com.segeon.easyrpc.core.domain.entity.LoadBalancePolicy;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLoadBalancePolicy implements LoadBalancePolicy {

    @Override
    public Channel choose(List<Channel> channelList) {
        int i = ThreadLocalRandom.current().nextInt(channelList.size());
        return channelList.get(i);
    }
}
