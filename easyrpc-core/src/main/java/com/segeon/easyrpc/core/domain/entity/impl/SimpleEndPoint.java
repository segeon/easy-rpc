package com.segeon.easyrpc.core.domain.entity.impl;

import com.segeon.easyrpc.core.domain.entity.Endpoint;
import lombok.ToString;

import java.io.Serializable;

@ToString
public class SimpleEndpoint implements Endpoint, Serializable {
    private String ip;
    private int port;

    public SimpleEndpoint(String ip, int port) {
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
