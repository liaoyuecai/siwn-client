package com.swin.client.factory;

import com.swin.bean.ConnectAck;
import com.swin.bean.MapData;
import com.swin.bean.Message;
import com.swin.bean.MsgAnswer;
import com.swin.constant.MessageIdentify;
import com.swin.exception.ConnectionException;
import com.swin.manager.ConditionLock;
import com.swin.utils.CoderUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;

public class MessageHandler extends ChannelInboundHandlerAdapter {

    private String clientName;

    private Integer feature;

    public MessageHandler(String clientName, Integer feature) {
        this.clientName = clientName;
        this.feature = feature;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Message message = new Message();
        switch (feature) {
            case 0:
                message.setIdentify(MessageIdentify.REGISTER_MAP);
                break;
            case 1:
                message.setIdentify(MessageIdentify.REGISTER_QUEUE);
                break;
        }
        message.setClientId(clientName);
        ctx.channel().writeAndFlush(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Integer identify = message.getIdentify();
        switch (identify) {
//            case MessageIdentify.CONNECT_OK:
//                ConditionLock.getInstance().release(clientName + "_start", new ConnectAck(true, null, ctx.channel()));
//                break;
            case MessageIdentify.CONNECT_EXCEPTION:
                ctx.channel().close();
                throw new ConnectionException("The client "+clientName+" is close, reason: " +message.getClientId());
            case MessageIdentify.GET_TREE_MAP_DATA_OK:
                this.mapMsgAck(message.getUuid(), (MapData) message.getData(), true);
                break;
            case MessageIdentify.PUT_TREE_MAP_DATA_OK:
                this.mapMsgAck(message.getUuid(), (MapData) message.getData(), true);
                break;
        }
    }

    private void mapMsgAck(String id, MapData data, boolean result) throws UnsupportedEncodingException {
        MsgAnswer answer = null;
        if (data.getValue() != null) {
            if (result) {
                answer = new MsgAnswer(result, null, data.getValue());
            } else {
                answer = new MsgAnswer(result, new String(data.getValue(), CoderUtils.STR_CODE), null);
            }
        } else {
            answer = new MsgAnswer(result, null, null);
        }
        ConditionLock.getInstance().release(id, answer);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
