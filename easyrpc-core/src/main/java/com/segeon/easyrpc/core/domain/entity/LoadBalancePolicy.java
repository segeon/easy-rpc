package com.segeon.easyrpc.core.domain.entity;

import java.util.List;
import java.util.Set;


public interface LoadBalancePolicy {

    Channel choose(List<Channel> channelList);
}
