package cn.hashq.netpoststation.handler;

import cn.hashq.netpoststation.cache.ClientCache;
import cn.hashq.netpoststation.cache.PortMapCache;
import cn.hashq.netpoststation.dto.ProtoMsg;
import cn.hashq.netpoststation.entity.Client;
import cn.hashq.netpoststation.entity.PortMap;
import cn.hashq.netpoststation.session.ServerSession;
import cn.hashq.netpoststation.session.SessionMap;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

/**
 * 映射端口数据处理Handler
 *
 * @author HashQ
 * @since 1.0
 */
@Slf4j
public class PortMapDataHandler extends ChannelInboundHandlerAdapter {

    private int port;

    public PortMapDataHandler(int port) {
        this.port = port;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ServerSession session = new ServerSession(ctx.channel());
        session.setServerPort(port);
        session.reverseBind();
        SessionMap.inst().addSession(session);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        PortMap portMap = PortMapCache.getInstance().getPortMapByServerPort(port);
        if (Objects.isNull(portMap)) {
            log.info("不存在{}端口映射", port);
            ServerSession.closeSession(ctx);
        }
        String clientId = portMap.getClientId();
        Client client = ClientCache.getInstance().getClientByClientId(clientId);
        if (Objects.isNull(client)) {
            log.info("{}端口对应客户端不存在", port);
            ServerSession.closeSession(ctx);
        }
        Optional<ServerSession> proxySession = SessionMap.inst().getSessionByClientId(clientId);
        if (proxySession.isPresent()) {
            log.info("{}客户端未上线", client.getClientName());
            ServerSession.closeSession(ctx);
        }
        byte[] bytes = (byte[]) msg;
        ProtoMsg.DataPackage dataPackage = ProtoMsg.DataPackage.newBuilder()
                .setPort(port)
                .setBytes(ByteString.copyFrom(bytes))
                .build();
        ProtoMsg.Message message = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.SERVER_DATA_REDIRECT)
                .setDataPackage(dataPackage)
                .build();
        proxySession.get().writeAndFlush(message);
    }
}
