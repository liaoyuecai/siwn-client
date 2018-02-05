package com.swin.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

class TcpClientStarter {
    private static final Logger logger = LoggerFactory.getLogger(TcpClientStarter.class);

    private ThreadPoolTaskExecutor executor;

    private String host;

    private Integer port;

    private Bootstrap client;

    private ChannelFuture channelFuture;

    private Channel channel;

    private boolean runStatus;

    TcpClientStarter(ThreadPoolTaskExecutor executor, String host, Integer port) {
        this.executor = executor;
        this.host = host;
        this.port = port;
    }

    private final EventLoopGroup group = new NioEventLoopGroup((Runtime.getRuntime().availableProcessors() / 3));

    void startClient(String clientName, final ChannelInitializer channelInitializer) {
        executor.submit(new Callable<Object>() {
            public Object call() throws Exception {
                try {
                    client = new Bootstrap();
                    client.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                            .option(ChannelOption.SO_KEEPALIVE, true)
                            .option(ChannelOption.SO_SNDBUF, 128 * 1024)
                            .option(ChannelOption.SO_SNDBUF, 128 * 1024)
                            .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                            .handler(new LoggingHandler(LogLevel.INFO))
                            .handler(channelInitializer);
                    channelFuture = client.connect(host, port).sync();
                    channel = channelFuture.channel();
                    runStatus = true;
                    ConditionLock.release(clientName + "_start",channel);
                    channelFuture.channel().closeFuture().sync();
                } catch (Exception e) {
                    logger.error("Client server exception", e);
                    group.shutdownGracefully().sync();
                } finally {
                    if (runStatus) {
                        reConnect();
                    }
                }
                return null;
            }
        });

    }


    private void reConnect() throws InterruptedException {
        try {
            channelFuture = client.connect(host, port).sync();
            channel = channelFuture.channel();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("Client connect break", e);
            TimeUnit.MILLISECONDS.sleep(1000);
            reConnect();
        } finally {

        }
    }

}