package com.segeon.easyrpc.core.netty;

import com.segeon.easyrpc.core.domain.entity.*;
import com.segeon.easyrpc.core.domain.exception.RPCClientException;
import com.segeon.easyrpc.core.domain.value.Consts;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class NettyRPCClient implements RPCClient {

    private ApplicationConfig applicationConfig;

    private ConcurrentMap<Long, Future<RPCResponse>> request2FutureMap;

    public NettyRPCClient(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
        this.request2FutureMap = new ConcurrentHashMap<>();
    }

    @Override
    public RPCResponse call(RPCRequest request, ReferenceConfig referenceConfig) {
        return call(request, Consts.DEFAULT_RPC_TIMEOUT_MILLIS, referenceConfig);
    }

    @Override
    public RPCResponse call(RPCRequest request, long timeoutMills, ReferenceConfig referenceConfig) {
        Future<RPCResponse> future = execute(request, referenceConfig);
        try {
            return (RPCResponse) future.get(timeoutMills, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error("执行RPC请求异常: {}", request.toString(), e);
            request2FutureMap.remove(request.getRequestId());
            throw new RPCClientException(e);
        } catch (ExecutionException e) {
            log.error("执行RPC请求异常: {}", request.toString(), e);
            request2FutureMap.remove(request.getRequestId());
            throw new RPCClientException(e);
        } catch (TimeoutException e) {
            log.error("执行RPC请求超时: {}, 超时时间: {}ms", request.toString(), timeoutMills, e);
            request2FutureMap.remove(request.getRequestId());
            throw new RPCClientException(e);
        }
    }

    @Override
    public Future<RPCResponse> execute(RPCRequest request, ReferenceConfig referenceConfig) {
        ChannelManager channelManager = applicationConfig.getChannelManager();
        List<Channel> channels = channelManager.availableChannels(referenceConfig);
        Channel channel = applicationConfig.getLoadBalancePolicy().choose(channels);
        return channel.send(request);
    }

    @Override
    public Future<RPCResponse> findFuture(long reqId) {
        return request2FutureMap.get(reqId);
    }

    @Override
    public void registerFuture(long reqId, Future<RPCResponse> future) {
        request2FutureMap.put(reqId, future);
    }

    @Override
    public void removeFuture(long reqId) {
        request2FutureMap.remove(reqId);
    }
}
