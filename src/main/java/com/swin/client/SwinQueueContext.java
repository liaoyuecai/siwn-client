package com.swin.client;


import io.netty.channel.Channel;

public class SwinQueueContext extends SwinContext {
    public SwinQueueContext(String clientName, Channel channel, long timeout) {
        super(clientName, channel, timeout);
    }
}
