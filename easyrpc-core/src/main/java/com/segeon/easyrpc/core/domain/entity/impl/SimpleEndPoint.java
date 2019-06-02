package com.segeon.easyrpc.core.domain.entity.impl;

import com.segeon.easyrpc.core.domain.entity.Endpoint;

public class SimpleEndPoint implements Endpoint {
    private String ip;
    private int port;

    public SimpleEndPoint(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String ip() {
        return ip;
    }

    @Override
    public int port() {
        return port;
    }
}
