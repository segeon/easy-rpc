package com.segeon.easyrpc.core.domain.invocation.client;

import com.segeon.easyrpc.core.domain.entity.RPCClient;
import com.segeon.easyrpc.core.domain.entity.RPCRequest;
import com.segeon.easyrpc.core.domain.entity.RPCResponse;
import com.segeon.easyrpc.core.domain.entity.ReferenceConfig;
import com.segeon.easyrpc.core.domain.exception.RPCServerException;
import com.segeon.easyrpc.core.domain.value.Consts;
import com.segeon.easyrpc.core.utils.IdGenerator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.segeon.easyrpc.core.utils.StringUtils.matchAny;

@Slf4j
public class ReferenceInvocationHandler implements InvocationHandler {
    private static final String[] FORBIDDEN_METHODS = {"equals", "clone", "hashcode", "finalize"};
    private RPCClient rpcClient;
    private ReferenceConfig referenceConfig;

    public ReferenceInvocationHandler(RPCClient rpcClient, ReferenceConfig referenceConfig) {
        this.rpcClient = rpcClient;
        this.referenceConfig = referenceConfig;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("toString") && (args == null || args.length == 0)) {
            return proxyName(proxy, referenceConfig.getInterfaceType());
        }
        log.debug("invoke {}#{}", referenceConfig.getInterfaceType().getSimpleName(), method.getName());
        if (method.getDeclaringClass().equals(Object.class) && matchAny(method.getName(), FORBIDDEN_METHODS)) {
            throw new UnsupportedOperationException("禁止在远程调用代理对象上调用Object类的下列方法:" + String.join(",", FORBIDDEN_METHODS));
        }

        RPCRequest rpcRequest = new RPCRequest();
        rpcRequest.setRequestId(IdGenerator.getId());
        rpcRequest.setType(Consts.PACKET_TYPE_REQUEST);
        rpcRequest.setInterfaceName(referenceConfig.getInterfaceType().getCanonicalName());
        rpcRequest.setGroup(referenceConfig.getGroup());
        rpcRequest.setVersion(referenceConfig.getVersion());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setArgs(args);
        rpcRequest.setArgTypes(method.getParameterTypes());
        RPCResponse rpcResponse = rpcClient.call(rpcRequest, referenceConfig);
        if (rpcResponse.isSuccess()) {
            return rpcResponse.getResult();
        } else {
            throw new RPCServerException("服务提供方异常！", rpcResponse.getError());
        }
    }

    private String proxyName(Object proxy, Class<?> interfaceType) {
        StringBuilder builder = new StringBuilder(128);
        builder.append(interfaceType.getSimpleName()).append("$Proxy").append(System.identityHashCode(proxy));
        return builder.toString();
    }
}
