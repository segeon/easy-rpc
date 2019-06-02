package com.segeon.easyrpc.core.domain.entity;

/**
 * 代表一个节点
 */
public interface Endpoint {

    String ip();

    int port();

    default String stringIdentity() {
        return String.format("%s:%d", ip(), port());
    }
}
