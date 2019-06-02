package com.segeon.easyrpc.core.domain.entity;

import java.io.IOException;

public interface RPCServer {

    void initialize();

    void close() throws IOException;

    boolean isInited();

    boolean isShuttingDown();
}
