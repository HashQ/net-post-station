package cn.hashq.netpoststation.handler;

import cn.hashq.netpoststation.cache.ClientCache;
import cn.hashq.netpoststation.concurrent.CallbackTask;
import cn.hashq.netpoststation.concurrent.CallbackTaskSchedule;
import cn.hashq.netpoststation.dto.ProtoMsg;
import cn.hashq.netpoststation.entity.Client;
import cn.hashq.netpoststation.session.ServerSession;
import cn.hashq.netpoststation.session.SessionMap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 验证客户端用Handler
 *
 * @author HashQ
 * @since 1.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class AuthHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (Objects.isNull(msg) || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType type = pkg.getType();
        if (!type.equals(ProtoMsg.HeadType.AUTH)) {
            super.channelRead(ctx, msg);
            return;
        }
        ServerSession session = new ServerSession(ctx.channel());
        CallbackTaskSchedule.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                // TODO:处理验证逻辑
                String secret = pkg.getAuth().getSecret();
                Client client = ClientCache.getInstance().getClientBySecret(secret);
                Optional<ServerSession> serverSession = SessionMap.inst().getSessionByClientId(client.getClientId());
                if (serverSession.isPresent()) {
                }
                return null;
            }

            @Override
            public void onBack(Boolean r) {
                if (r) {
                    // TODO:验证成功
                }
            }

            @Override
            public void onException(Throwable t) {
                // TODO:验证失败
            }
        });

    }
}
