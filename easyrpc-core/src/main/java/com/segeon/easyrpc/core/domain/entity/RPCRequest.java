package com.segeon.easyrpc.core.domain.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class RPCRequest extends RPCHeader {
    private String interfaceName;
    private String group;
    private String version;
    private String methodName;
    private Class[] argTypes;
    private Object[] args;
}
