package com.swin.client;

import com.alibaba.fastjson.JSON;
import com.swin.bean.MapData;
import com.swin.bean.Message;
import com.swin.bean.MsgAnswer;
import com.swin.exception.ParamException;
import com.swin.exception.UnknownException;
import com.swin.utils.CoderUtils;
import io.netty.channel.Channel;

public class SwinMapContext extends SwinContext {
    public SwinMapContext(String clientName, Channel channel, long timeout) {
        super(clientName, channel, timeout);
    }

    public boolean putMessage(String tree, String key, Object value) throws Exception {
        if (tree != null && key != null && value != null) {
            Message message = new Message();
            message.setClientId("map_" + clientId);
            message.setIdentify(MessageIdentify.PUT_TREE_MAP_DATA);
            MapData data = new MapData();
            data.setTree(tree);
            data.setKey(key);
            data.setValue(JSON.toJSONString(value).getBytes(CoderUtils.STR_CODE));
            message.setData(data);
            channel.writeAndFlush(message);
            MsgAnswer answer = (MsgAnswer) ConditionLock.await(message.getUuid(), timeout);
            if (answer.getMessage() != null) {
                throw new UnknownException(answer.getMessage());
            } else {
                return answer.isSuccess();
            }
        } else {
            throw new ParamException("Some param is null");
        }
    }

    public Object getMessage(String tree, String key) throws Exception {
        if (tree != null && key != null) {
            Message message = new Message();
            message.setClientId("map_" + clientId);
            message.setIdentify(MessageIdentify.GET_TREE_MAP_DATA);
            MapData data = new MapData();
            data.setTree("map_" + tree);
            data.setKey(key);
            message.setData(data);
            channel.writeAndFlush(message);
            MsgAnswer answer = (MsgAnswer) ConditionLock.await(message.getUuid(), timeout);
            if (answer.getMessage() != null) {
                throw new UnknownException(answer.getMessage());
            } else {
                if (answer.getValue() != null) {
                    return JSON.parse(new String(answer.getValue(), CoderUtils.STR_CODE));
                } else {
                    return null;
                }
            }
        } else {
            throw new ParamException("Some param is null");
        }
    }

}
