package com.swin.client;

import com.swin.exception.ConditionTaskException;
import com.swin.exception.ConditionTimeoutException;
import com.swin.exception.IdentificationException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

public class SwinClient {

    public ThreadPoolTaskExecutor executor;

    public enum SwinFeature {
        MAP, QUEUE
    }

    private String clientName;
    private Integer feature;
    private long timeout;

    public SwinClient(String clientName, SwinFeature feature, long timeout) {
        this.clientName = clientName;
        this.timeout = timeout;
        switch (feature) {
            case MAP:
                this.feature = 0;
                break;
            case QUEUE:
                this.feature = 1;
                break;
        }
        this.executor = new ThreadPoolTaskExecutor();
        this.executor.setCorePoolSize(5);
        this.executor.setMaxPoolSize(200);
        this.executor.setQueueCapacity(5);
        this.executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        this.executor.setKeepAliveSeconds(60);
        this.executor.initialize();
    }

    public SwinClient(String clientName, SwinFeature feature, long timeout, Integer executorMax) {
        this.clientName = clientName;
        this.timeout = timeout;
        switch (feature) {
            case MAP:
                this.feature = 0;
                break;
            case QUEUE:
                this.feature = 1;
                break;
        }
        this.executor = new ThreadPoolTaskExecutor();
        this.executor.setCorePoolSize(5);
        this.executor.setMaxPoolSize(executorMax);
        this.executor.setQueueCapacity(5);
        this.executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        this.executor.setKeepAliveSeconds(60);
        this.executor.initialize();
    }

    public SwinContext context(String host, Integer port) throws Exception {
        TcpClientStarter starter = new TcpClientStarter(executor, host, port);
        ChannelInitializer channelInitializer = new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel channel)
                    throws Exception {
                channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(16 * 1024, 0, 4, 0, 4))
                        .addLast(new MessageDecoder())
                        .addLast("idleStateHandler", new IdleStateHandler(300 * 1000, 300 * 1000, 300 * 1000))
                        .addLast(new MessageEncoder())
                        .addLast(new MessageHandler(clientName, feature));
            }
        };
        starter.startClient(clientName, channelInitializer);
        Channel channel = null;
        try {
            channel = (Channel) ConditionLock.await(clientName + "_start", 30000);
        } catch (ConditionTaskException e) {
            throw new IdentificationException("Maybe you have the client of the same name");
        } catch (ConditionTimeoutException e) {
            throw new IdentificationException("Connect timeout");
        } catch (Exception e) {
            throw e;
        }
        switch (feature) {
            case 0:
                return new SwinMapContext(clientName, channel, timeout);
            case 1:
                return new SwinQueueContext(clientName, channel, timeout);
            default:
                return null;
        }
    }


}
