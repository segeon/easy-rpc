package com.segeon.easyrpc.core.netty;

import com.segeon.easyrpc.core.domain.entity.ApplicationConfig;
import com.segeon.easyrpc.core.domain.entity.RPCRequest;
import com.segeon.easyrpc.core.domain.entity.RPCResponse;
import com.segeon.easyrpc.core.domain.invocation.server.InvocationExecutor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {
    public static final String NAME = "serverHandler";

    private ApplicationConfig applicationConfig;
    private InvocationExecutor executor;

    public ServerHandler(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
        this.executor = new InvocationExecutor(applicationConfig);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RPCRequest) {
            log.debug("receive request: {}", msg);
            executor.execute(new RPCContext((RPCRequest)msg, null, ctx));
        } else {
            ctx.write(RPCResponse.genInvalidRequestResponse("不合法的请求类型！"));
            log.error("不合法的请求类型:{}", msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelActive: {}", ctx.channel().toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("", cause);
    }
}
