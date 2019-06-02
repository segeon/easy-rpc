package com.segeon.easyrpc.core.netty;

import com.segeon.easyrpc.core.domain.entity.RPCClient;
import com.segeon.easyrpc.core.domain.entity.RPCResponse;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * RPC consumer侧使用的netty codec
 */
@Slf4j
public class ClientHandler extends ChannelDuplexHandler {
    public static final String NAME = "clientHandler";
    private NettyChannel channel;
    private RPCClient client;

    public ClientHandler(NettyChannel nettyChannel, RPCClient client) {
        this.channel = nettyChannel;
        this.client = client;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof RPCResponse)) {
            log.error("无法处理的消息类型: {}", msg.getClass().getSimpleName());
            ctx.close();
            return;
        }
        RPCResponse response = (RPCResponse) msg;
        long requestId = response.getRequestId();
        Future<RPCResponse> responseFuture = client.findFuture(requestId);
        if (null == responseFuture) {
            log.error("没有找到对应的请求id({})，可能客户端已超时！", requestId);
            return;
        }
        CompletableFuture<RPCResponse> future = (CompletableFuture<RPCResponse>) responseFuture;
        future.complete(response);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive: {}", ctx.channel().toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelInactive: {}, reconnecting", ctx.channel().toString());
        this.channel.reconnect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught: {}", ctx.channel(), cause);
    }
}
