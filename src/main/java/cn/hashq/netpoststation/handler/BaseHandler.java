package cn.hashq.netpoststation.handler;

import cn.hashq.netpoststation.dto.ProtoMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Objects;

public abstract class BaseHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (Objects.isNull(msg) || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType type = pkg.getType();
        if (!type.equals(getHeadType())) {
            super.channelRead(ctx, msg);
            return;
        }
        process(ctx, pkg);
    }

    public abstract ProtoMsg.HeadType getHeadType();

    public abstract void process(ChannelHandlerContext ctx, ProtoMsg.Message msg);
}
