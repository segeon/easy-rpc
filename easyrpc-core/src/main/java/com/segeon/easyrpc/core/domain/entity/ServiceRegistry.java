package com.segeon.easyrpc.core.domain.entity;

import java.io.Closeable;
import java.util.List;

/**
 * 负责服务注册与订阅
 */
public interface ServiceRegistry extends Endpoint, Closeable {

    void init(ApplicationConfig applicationConfig);

    String registryType();

    void register(RemotingService remotingService);

    void unRegister(RemotingService remotingService);

    void subscribe(ReferenceConfig remotingService, EndpointChangeListener listener);

    void unsubscribe(ReferenceConfig remotingService);

    List<Endpoint> list(ReferenceConfig remotingService);
}
