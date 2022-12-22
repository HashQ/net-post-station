package cn.hashq.netpoststation.handler;

import cn.hashq.netpoststation.dto.ProtoMsg;
import cn.hashq.netpoststation.session.ServerSession;
import cn.hashq.netpoststation.session.SessionMap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 转发数据用Handler
 * <p>
 * 转发客户端响应的数据包
 * </p>
 *
 * @author HashQ
 * @since 1.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ClientDataRedirectHandler extends BaseHandler {

    @Override
    public ProtoMsg.HeadType getHeadType() {
        return ProtoMsg.HeadType.CLIENT_DATA_REDIRECT;
    }

    @Override
    public void process(ChannelHandlerContext ctx, ProtoMsg.Message msg) {
        ProtoMsg.DataPackage dataPackage = msg.getDataPackage();
        byte[] bytes = dataPackage.getBytes().toByteArray();
        String sessionId = msg.getSessionId();
        ServerSession session = SessionMap.inst().getSession(sessionId);
        session.writeAndFlush(bytes);
    }

}
