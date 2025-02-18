package com.segeon.easyrpc.core.domain.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegistryConfig {
    /**
     * etcd
     */
    private String schema;
    private String ip;
    private int port;
}
