package com.swin.bean;

import io.netty.channel.Channel;
import lombok.Data;

import java.io.Serializable;
@Data
public class ConnectAck implements Serializable {
    private boolean success;
    private String message;
    private Channel channel;

    public ConnectAck(boolean success, String message, Channel channel) {
        this.success = success;
        this.message = message;
        this.channel = channel;
    }
}
