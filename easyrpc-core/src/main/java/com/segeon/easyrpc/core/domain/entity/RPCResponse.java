package com.segeon.easyrpc.core.domain.entity;

import com.segeon.easyrpc.core.domain.exception.InvalidRequestException;
import com.segeon.easyrpc.core.domain.value.Consts;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RPCResponse extends RPCHeader {
    private Throwable error;
    private String message;
    private boolean success;
    private Object result;

    public static RPCResponse genInvalidRequestResponse(String msg) {
        RPCResponse rpcResponse = new RPCResponse();
        rpcResponse.setRequestId(-1);
        rpcResponse.setSuccess(false);
        rpcResponse.setType((byte)(Consts.PACKET_TYPE_RESPONSE & 0xFF));
        rpcResponse.setMessage(msg);
        rpcResponse.setError(new InvalidRequestException());
        return rpcResponse;
    }

    public static RPCResponse genInvalidRequestResponse(String msg, Throwable error) {
        RPCResponse rpcResponse = new RPCResponse();
        rpcResponse.setRequestId(-1);
        rpcResponse.setSuccess(false);
        rpcResponse.setType((byte)(Consts.PACKET_TYPE_RESPONSE & 0xFF));
        rpcResponse.setMessage(msg);
        rpcResponse.setError(error);
        return rpcResponse;
    }
}
