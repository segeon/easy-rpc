package com.segeon.easyrpc.core.domain.invocation.server;

import com.segeon.easyrpc.core.domain.entity.*;
import com.segeon.easyrpc.core.domain.exception.ServerTooBusyException;
import com.segeon.easyrpc.core.netty.RPCContext;
import com.segeon.easyrpc.core.utils.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class InvocationRunnable implements Runnable {

    private RPCContext context;
    private ServiceManager serviceManager;

    public InvocationRunnable(RPCContext context, ApplicationConfig config) {
        this.context = context;
        this.serviceManager = config.getServiceManager();
    }

    @Override
    public void run() {
        initResponse();
        invoke();
        sendResponse();
    }

    protected void initResponse() {
        if (null == context.getResponse()) {
            context.setResponse(new RPCResponse());
        }
        context.getResponse().setRequestId(context.getRequest().getRequestId());
    }

    protected void setResult(Object result) {
        context.getResponse().setResult(result);
        context.getResponse().setSuccess(true);
    }

    protected void setError(Throwable throwable) {
        context.getResponse().setError(throwable);
        context.getResponse().setSuccess(false);
    }

    protected void invoke() {
        try {
            RPCRequest request = context.getRequest();
            RemotingService remotingService = serviceManager.lookup(request.getInterfaceName(), request.getGroup(), request.getVersion());
            Object target = remotingService.target();
            Method method = target.getClass().getMethod(request.getMethodName(), request.getArgTypes());
            setResult(method.invoke(target, request.getArgs()));
        } catch (Throwable e) {
            Throwable throwable = ExceptionUtil.unwrapThrowable(e);
            log.error("执行本地方法异常！{}", context, throwable);
            setError(throwable);
        }
    }

    protected void sendResponse() {
        context.getCtx().writeAndFlush(context.getResponse());
    }

    public void rejectExecution() {
        setError(new ServerTooBusyException());
        context.getCtx().writeAndFlush(context.getResponse());
    }
}
