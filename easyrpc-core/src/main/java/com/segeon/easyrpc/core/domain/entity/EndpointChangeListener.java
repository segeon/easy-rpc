package com.segeon.easyrpc.core.domain.entity;

import java.util.List;

public interface EndpointChangeListener {

    /**
     * @param key
     * @param newEndpoints 新增的节点
     * @param deletedEndpoints 删除的节点
     */
    void notify(ServiceKey key, List<Endpoint> newEndpoints, List<Endpoint> deletedEndpoints);
}
