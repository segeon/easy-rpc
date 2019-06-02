package com.segeon.easyrpc.core.netty;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.segeon.easyrpc.core.domain.entity.ApplicationConfig;
import com.segeon.easyrpc.core.domain.entity.RPCServer;
import com.segeon.easyrpc.core.netty.codec.FrameDecoder;
import com.segeon.easyrpc.core.netty.codec.FrameEncoder;
import com.segeon.easyrpc.core.netty.codec.RequestDecoder;
import com.segeon.easyrpc.core.netty.codec.ResponseEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;

@Slf4j
public class NettyRPCServer implements Closeable, RPCServer {
    private ApplicationConfig config;
    private int threads = Runtime.getRuntime().availableProcessors() * 2;
    private volatile boolean inited = false;
    private volatile boolean shuttingDown = false;
    private ServerBootstrap serverBootstrap;
    private NioEventLoopGroup masterLoop;
    private NioEventLoopGroup workerLoop;
    private Channel serverChannel;

    public NettyRPCServer(ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public synchronized void initialize() {
        if (inited) {
            return;
        }
        masterLoop = new NioEventLoopGroup(1, new ThreadFactoryBuilder().setDaemon(false).setNameFormat("easyrpc-nettyserver-master").build());
        workerLoop = new NioEventLoopGroup(threads, new ThreadFactoryBuilder().setDaemon(false).setNameFormat("easyrpc-nettyserver-worker").build());
        serverBootstrap = new ServerBootstrap().group(masterLoop, workerLoop).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //下面encoder和handler的顺序很重要，不能修改
                        ch.pipeline().addLast(FrameEncoder.NAME, new FrameEncoder())
                                .addLast(ResponseEncoder.NAME, new ResponseEncoder())
                                .addLast(FrameDecoder.NAME, new FrameDecoder())
                                .addLast(RequestDecoder.NAME, new RequestDecoder())
                                .addLast(ServerHandler.NAME, new ServerHandler(config));
                    }
                });
        serverChannel = serverBootstrap.bind(config.getPort()).syncUninterruptibly().channel();
        inited = true;
        log.info("bind to {} successfully!", config.getPort());
    }

    @Override
    public synchronized void close() throws IOException {
        if (!inited) {
            return;
        }
        shuttingDown = true;
        if (serverChannel != null) {
            serverChannel.closeFuture().syncUninterruptibly();
        }
        if (masterLoop != null) {
            masterLoop.shutdownGracefully();
        }
        if (workerLoop != null) {
            workerLoop.shutdownGracefully();
        }
        inited = false;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    @Override
    public synchronized boolean isInited() {
        return inited;
    }

    @Override
    public synchronized boolean isShuttingDown() {
        return shuttingDown;
    }
}
