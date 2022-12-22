package cn.hashq.netpoststation.handler;

import cn.hashq.netpoststation.cache.PortMapCache;
import cn.hashq.netpoststation.dto.ProtoMsg;
import cn.hashq.netpoststation.entity.PortMap;
import cn.hashq.netpoststation.session.ServerSession;
import cn.hashq.netpoststation.session.SessionMap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;


/**
 * 转发数据用Handler
 * <p>
 * 转发映射端口收到的请求数据包
 * </p>
 *
 * @author HashQ
 * @since 1.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ServerDataRedirectHandler extends BaseHandler {
    @Override
    public ProtoMsg.HeadType getHeadType() {
        return ProtoMsg.HeadType.SERVER_DATA_REDIRECT;
    }

    @Override
    public void process(ChannelHandlerContext ctx, ProtoMsg.Message msg) {
        ProtoMsg.DataPackage dataPackage = msg.getDataPackage();
        int port = dataPackage.getPort();
        PortMap portMap = PortMapCache.getInstance().getPortMapByServerPort(port);
        String clientId = portMap.getClientId();
        Optional<ServerSession> session = SessionMap.inst().getSessionByClientId(clientId);
        if (!session.isPresent()) {
            log.info("{}客户端未连接", clientId);
            return;
        }
        session.get().writeAndFlush(msg);
    }
}
