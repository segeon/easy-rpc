package com.segeon.easyrpc.core.domain.exception;

public class ServerTooBusyException extends RPCServerException {
    public ServerTooBusyException() {
    }

    public ServerTooBusyException(String message) {
        super(message);
    }

    public ServerTooBusyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerTooBusyException(Throwable cause) {
        super(cause);
    }

    public ServerTooBusyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
