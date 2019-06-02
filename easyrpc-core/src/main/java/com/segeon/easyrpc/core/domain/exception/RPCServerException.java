package com.segeon.easyrpc.core.domain.exception;

public class RPCServerException extends RuntimeException {
    public RPCServerException() {
    }

    public RPCServerException(String message) {
        super(message);
    }

    public RPCServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RPCServerException(Throwable cause) {
        super(cause);
    }

    public RPCServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
