package com.segeon.easyrpc.core.domain.entity;

import java.util.concurrent.Future;

/**
 * Consumer可使用的原始Client
 */
public interface RPCClient {

    RPCResponse call(RPCRequest request, ReferenceConfig referenceConfig);

    RPCResponse call(RPCRequest request, long timeoutMills, ReferenceConfig referenceConfig);

    Future<RPCResponse> execute(RPCRequest request, ReferenceConfig referenceConfig);

    Future<RPCResponse> findFuture(long reqId);

    void registerFuture(long reqId, Future<RPCResponse> future);

    void removeFuture(long reqId);
}
