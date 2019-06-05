package com.segeon.easyrpc.core.netty;

import com.segeon.easyrpc.core.domain.entity.*;
import com.segeon.easyrpc.core.domain.exception.ClientChannelException;
import com.segeon.easyrpc.core.netty.codec.FrameDecoder;
import com.segeon.easyrpc.core.netty.codec.FrameEncoder;
import com.segeon.easyrpc.core.netty.codec.RequestEncoder;
import com.segeon.easyrpc.core.netty.codec.ResponseDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Slf4j
@EqualsAndHashCode(of = {"ip", "port"})
public class NettyChannel implements com.segeon.easyrpc.core.domain.entity.Channel<RPCRequest, RPCResponse> {
    private ApplicationConfig config;
    private RPCClient client;
    private ChannelManager channelManager;
    private String ip;
    private int port;
    private volatile io.netty.channel.Channel nettyChannel;
    private EventLoopGroup group;
    private Bootstrap bootstrap;

    public NettyChannel(ApplicationConfig config, String ip, int port) {
        this.config = config;
        this.client = config.getRpcClient();
        this.channelManager = config.getChannelManager();
        this.ip = ip;
        this.port = port;
    }

    @Override
    public synchronized void connect() {
        group = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(FrameDecoder.NAME, new FrameDecoder())
                                    .addLast(ResponseDecoder.NAME, new ResponseDecoder())
                                    .addLast(FrameEncoder.NAME, new FrameEncoder())
                                    .addLast(RequestEncoder.NAME, new RequestEncoder())
                                    .addLast(ClientHandler.NAME, new ClientHandler(NettyChannel.this, client));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
            nettyChannel = channelFuture.channel();
        } catch (InterruptedException e) {
            String message = String.format("连接%s:%s异常", ip, port);
            log.error(message, e);
            throw new ClientChannelException(message, e);
        }
    }

    @Override
    public synchronized void reconnect() {
        disConnect();
        ChannelFuture channelFuture = bootstrap.connect(ip, port).syncUninterruptibly();
        nettyChannel = channelFuture.channel();
    }

    @Override
    public synchronized Future<RPCResponse> send(RPCRequest request) {
        CompletableFuture<RPCResponse> future = new CompletableFuture<>();
        client.registerFuture(request.getRequestId(), future);
        nettyChannel.writeAndFlush(request);
        return future;
    }

    @Override
    public synchronized void disConnect() {
        if (nettyChannel != null) {
            nettyChannel.disconnect().syncUninterruptibly();
            nettyChannel = null;
        }
    }

    @Override
    public synchronized void close() {
        if (nettyChannel != null) {
            nettyChannel.close().syncUninterruptibly();
            channelManager.removeChannel(this);
            nettyChannel = null;
        }
    }

    @Override
    public String ip() {
        return this.ip;
    }

    @Override
    public int port() {
        return this.port;
    }
}
