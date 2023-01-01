package cn.hashq.netpoststation.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 会话类
 * <p>
 * 将channel与客户端标识进行双向绑定
 * </p>
 *
 * @author HashQ
 * @since 1.0
 */
@Data
@Slf4j
public class ServerSession {

    public static final AttributeKey<String> KEY_USER_ID = AttributeKey.valueOf("key_user_id");

    public static final AttributeKey<ServerSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

    /**
     * 通道
     */
    private Channel channel;

    /**
     * 客户端Id
     */
    private String clientId;


    /**
     * 验证状态
     */
    private boolean isAuth = false;


    public ServerSession(Channel channel) {
        // 正向绑定
        this.channel = channel;
    }

    public ServerSession reverseBind() {
        // 反向绑定
        channel.attr(ServerSession.SESSION_KEY).set(this);
        SessionMap.inst().addSession(this);
        isAuth = true;
        return this;
    }

    public synchronized void writeAndFlush(Object pkg) {
        channel.writeAndFlush(pkg);
    }

    public static void closeSession(ChannelHandlerContext ctx) {
        ServerSession session = ctx.channel().attr(ServerSession.SESSION_KEY).get();
        if (Objects.nonNull(session) && session.isAuth()) {
            session.close();
            SessionMap.inst().removeSession(session.getClientId());
        }
    }

    public synchronized void close() {
        ChannelFuture future = channel.close();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    log.error("关闭通道异常");
                }
            }
        });
    }

}
