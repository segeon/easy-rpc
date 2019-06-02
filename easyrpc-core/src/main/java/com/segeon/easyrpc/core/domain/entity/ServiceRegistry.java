package com.segeon.easyrpc.core.domain.entity;

import java.util.List;

/**
 * 注册中心
 */
public interface ServiceRegistry extends Endpoint{

    void register(RemotingService remotingService);

    void unRegister(RemotingService remotingService);

    void subscribe(ReferenceConfig remotingService);

    void unsubscribe(ReferenceConfig remotingService);

    List<Endpoint> list(ReferenceConfig remotingService);
}
