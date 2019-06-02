package com.segeon.easyrpc.core.domain.entity.impl;

import com.segeon.easyrpc.core.domain.entity.RemotingService;

public class RemotingServiceImpl implements RemotingService {
    private Class<?> interfaceType;
    private Object target;
    private String group;
    private String version;

    public RemotingServiceImpl(Class<?> interfaceType, Object target, String group, String version) {
        this.interfaceType = interfaceType;
        this.target = target;
        this.group = group;
        this.version = version;
    }

    @Override
    public Class<?> interfaceType() {
        return this.interfaceType;
    }

    @Override
    public Object target() {
        return target;
    }

    @Override
    public String group() {
        return group;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public String toString() {
        return "RemotingServiceImpl{" +
                "interfaceType=" + interfaceType +
                ", target=" + target +
                ", group='" + group + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
