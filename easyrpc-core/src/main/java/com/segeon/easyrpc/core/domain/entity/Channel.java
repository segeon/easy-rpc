package com.segeon.easyrpc.core.domain.entity;

import java.util.concurrent.Future;

/**
 * Consumer到Provider的连接
 */
public interface Channel<REQ, RES> extends Endpoint{

    void connect();

    void reconnect();

    Future<RES> send(REQ request);

    void disConnect();

    void close();
}
