package cn.hashq.netpoststation.handler;

import cn.hashq.netpoststation.cache.ClientCache;
import cn.hashq.netpoststation.concurrent.CallbackTask;
import cn.hashq.netpoststation.concurrent.CallbackTaskSchedule;
import cn.hashq.netpoststation.constant.ProtoConstant;
import cn.hashq.netpoststation.dto.ProtoMsg;
import cn.hashq.netpoststation.entity.Client;
import cn.hashq.netpoststation.session.ServerSession;
import cn.hashq.netpoststation.session.SessionMap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    @Resource
    private DataRedirectHandler dataRedirectHandler;

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
                // 判断客户端是否存在
                String secret = pkg.getAuth().getSecret();
                Client client = ClientCache.getInstance().getClientBySecret(secret);
                long seq = pkg.getSequence();
                if (Objects.isNull(client)) {
                    session.writeAndFlush(buildAuthResponse(seq, session, ProtoConstant.ResultCode.AUTH_FAILED));
                    return false;
                }
                // 判断客户端是否已连接，如果已经连接，则断开上一个连接
                Optional<ServerSession> serverSession = SessionMap.inst().getSessionByClientId(client.getClientId());
                if (serverSession.isPresent()) {
                    serverSession.get().close();
                    SessionMap.inst().removeSession(serverSession.get().getSessionId());
                }
                session.reverseBind();
                session.writeAndFlush(buildAuthResponse(seq, session, ProtoConstant.ResultCode.SUCCESS));
                return true;
            }

            @Override
            public void onBack(Boolean r) {
                if (r) {
                    ctx.pipeline().addAfter("auth", "redirect", dataRedirectHandler);
                    ctx.pipeline().addAfter("auth", "heartBeat", new HeartHandler());
                    ctx.pipeline().remove("auth");
                } else {
                    ServerSession.closeSession(ctx);
                }
            }

            @Override
            public void onException(Throwable t) {
                ServerSession.closeSession(ctx);
            }
        });
    }

    public ProtoMsg.Message buildAuthResponse(long seq, ServerSession session, ProtoConstant.ResultCode code) {
        ProtoMsg.Message.Builder builder = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.AUTH_RESPONSE)
                .setSequence(seq)
                .setSessionId(session.getSessionId());
        ProtoMsg.AuthResponse.Builder response = ProtoMsg.AuthResponse.newBuilder()
                .setCode(code.getCode());
        builder.setResponse(response.build());
        return builder.build();
    }
}
