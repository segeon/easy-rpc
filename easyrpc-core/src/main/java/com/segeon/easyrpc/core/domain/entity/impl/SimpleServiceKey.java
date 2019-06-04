package com.segeon.easyrpc.core.domain.entity.impl;

import com.segeon.easyrpc.core.domain.entity.ReferenceConfig;
import com.segeon.easyrpc.core.domain.entity.ServiceKey;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = {"interfaceType", "group", "version"})
public class SimpleServiceKey implements ServiceKey {

    private Class interfaceType;
    private String group;
    private String version;

    @Override
    public Class<?> interfaceType() {
        return interfaceType;
    }

    @Override
    public String group() {
        return group;
    }

    @Override
    public String version() {
        return version;
    }

    public static SimpleServiceKey fromReferenceConfig(ReferenceConfig config) {
        return new SimpleServiceKey(config.getInterfaceType(), config.getGroup(), config.getVersion());
    }
}
