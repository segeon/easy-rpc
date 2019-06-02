package com.segeon.easyrpc.core.domain.exception;

public class RPCClientException extends RuntimeException {
    public RPCClientException() {
    }

    public RPCClientException(String message) {
        super(message);
    }

    public RPCClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public RPCClientException(Throwable cause) {
        super(cause);
    }

    public RPCClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
