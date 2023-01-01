package cn.hashq.netpoststation.handler.map;

import cn.hashq.netpoststation.cache.ChannelCache;
import cn.hashq.netpoststation.cache.ClientCache;
import cn.hashq.netpoststation.cache.PortMapCache;
import cn.hashq.netpoststation.dto.ProtoMsg;
import cn.hashq.netpoststation.entity.Client;
import cn.hashq.netpoststation.entity.PortMap;
import cn.hashq.netpoststation.session.ServerSession;
import cn.hashq.netpoststation.session.SessionMap;
import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 映射端口数据处理Handler
 *
 * @author HashQ
 * @since 1.0
 */
@Slf4j
public class PortMapDataHandler extends ChannelInboundHandlerAdapter {

    private int port;

    private String clientId;

    public PortMapDataHandler(int port) {
        this.port = port;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive()) {
            ChannelCache.getInstance().addChannel(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ProtoMsg.Message msg = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.PORT_CONNECT_BREAK)
                .setSessionId(ctx.channel().id().asLongText())
                .build();
        ServerSession session = SessionMap.inst().getSession(clientId);
        session.writeAndFlush(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        PortMap portMap = PortMapCache.getInstance().getPortMapByServerPort(port);
        String channelId = ctx.channel().id().asLongText();
        if (Objects.isNull(portMap)) {
            log.info("不存在{}端口映射", port);
            ChannelCache.getInstance().removeChannel(channelId);
        }
        String clientId = portMap.getClientId();
        Client client = ClientCache.getInstance().getClientByClientId(clientId);
        if (Objects.isNull(client)) {
            log.info("{}端口对应客户端不存在", port);
            ChannelCache.getInstance().removeChannel(channelId);
        }
        ServerSession session = SessionMap.inst().getSession(clientId);
        this.clientId = session.getClientId();
        if (!Objects.isNull(session)) {
            log.info("{}客户端未上线", client.getClientName());
            ChannelCache.getInstance().removeChannel(channelId);
        }
        ByteBuf buf = (ByteBuf) msg;
        int length = buf.readableBytes();
        if (length == 0) {
            super.channelRead(ctx, msg);
            return;
        }
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        ProtoMsg.DataPackage dataPackage = ProtoMsg.DataPackage.newBuilder()
                .setServerPort(port)
                .setClientPort(portMap.getClientPort())
                .setBytes(ByteString.copyFrom(bytes))
                .build();
        ProtoMsg.Message message = ProtoMsg.Message.newBuilder()
                .setSessionId(channelId)
                .setType(ProtoMsg.HeadType.SERVER_DATA_REDIRECT)
                .setDataPackage(dataPackage)
                .build();
        session.writeAndFlush(message);
    }
}
