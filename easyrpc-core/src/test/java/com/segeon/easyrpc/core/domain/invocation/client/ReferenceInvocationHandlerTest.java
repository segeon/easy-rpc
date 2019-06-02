package com.segeon.easyrpc.core.domain.invocation.client;

import com.segeon.easyrpc.core.domain.entity.RPCRequest;
import com.segeon.easyrpc.core.domain.entity.ReferenceConfig;
import com.segeon.easyrpc.core.netty.NettyRPCClient;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class ReferenceInvocationHandlerTest {

    @org.junit.Test
    public void invoke() throws NoSuchMethodException {
        Method equals = NettyRPCClient.class.getMethod("equals", Object.class);
        Method toString = NettyRPCClient.class.getMethod("toString");
        assertEquals(Object.class, equals.getDeclaringClass());
        assertEquals(Object.class, toString.getDeclaringClass());
        Method execute = NettyRPCClient.class.getMethod("execute", RPCRequest.class, ReferenceConfig.class);
        assertEquals(NettyRPCClient.class, execute.getDeclaringClass());
    }
}