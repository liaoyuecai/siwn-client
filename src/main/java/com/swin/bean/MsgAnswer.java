package com.swin.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class MsgAnswer implements Serializable {

    private boolean success;

    private String message;

    private byte[] value;

    public MsgAnswer(boolean success, String message, byte[] value) {
        this.success = success;
        this.message = message;
        this.value = value;
    }
}
