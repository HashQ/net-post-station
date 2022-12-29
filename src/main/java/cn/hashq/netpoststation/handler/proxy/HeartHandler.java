package cn.hashq.netpoststation.handler.proxy;

import cn.hashq.netpoststation.dto.ProtoMsg;
import cn.hashq.netpoststation.session.ServerSession;
import cn.hashq.netpoststation.util.ThreadUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 处理心跳Handler
 *
 * @author HashQ
 * @since 1.0
 */
@Slf4j
public class HeartHandler extends IdleStateHandler {

    private static final int READ_IDLE_GAP = 150;

    public HeartHandler() {
        super(READ_IDLE_GAP, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (Objects.isNull(msg) || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType type = pkg.getType();
        if (type.equals(ProtoMsg.HeadType.HEART_BEAT)) {
            ThreadUtil.getIOIntenseTargetThreadPool().submit(() -> {
                if (ctx.channel().isActive()) {
                    log.info("{}", pkg.getHeart().getBody());
                    ctx.writeAndFlush(pkg);
                }
            });
        }
        super.channelRead(ctx, msg);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        log.info("{}秒内没读到数据，关闭连接", READ_IDLE_GAP);
        ServerSession.closeSession(ctx);
    }
}
