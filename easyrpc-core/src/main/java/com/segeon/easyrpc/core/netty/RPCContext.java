package com.segeon.easyrpc.core.netty;

import com.segeon.easyrpc.core.domain.entity.RPCRequest;
import com.segeon.easyrpc.core.domain.entity.RPCResponse;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RPCContext {
    private RPCRequest request;
    private RPCResponse response;
    private ChannelHandlerContext ctx;
}
