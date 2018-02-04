package com.swin.open;


import io.netty.channel.Channel;

public abstract class SwinContext {
    protected String clientName;
    protected Channel channel;
    protected long timeout;

    public SwinContext(String clientName, Channel channel,long timeout) {
        this.clientName = clientName;
        this.channel = channel;
        this.timeout = timeout;
    }

}
