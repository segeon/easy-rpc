package com.segeon.easyrpc.core.domain.entity;

import java.util.List;

public interface EndpointChangeListener {

    void notify(ServiceKey key, List<Endpoint> newEndpoints);
}
