package com.swin.client;


import io.netty.channel.Channel;

abstract class SwinContext {
    protected String clientId;
    protected Channel channel;
    protected long timeout;

    public SwinContext(String clientId, Channel channel,long timeout) {
        this.clientId = clientId;
        this.channel = channel;
        this.timeout = timeout;
    }

}
