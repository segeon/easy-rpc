package com.segeon.easyrpc.core.domain.exception;

public class ClientChannelException extends RPCClientException {
    public ClientChannelException() {
        super();
    }

    public ClientChannelException(String message) {
        super(message);
    }

    public ClientChannelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientChannelException(Throwable cause) {
        super(cause);
    }

    public ClientChannelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
