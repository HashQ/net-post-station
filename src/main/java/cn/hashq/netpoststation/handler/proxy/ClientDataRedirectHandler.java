package cn.hashq.netpoststation.handler.proxy;

import cn.hashq.netpoststation.cache.ChannelCache;
import cn.hashq.netpoststation.dto.ProtoMsg;
import cn.hashq.netpoststation.handler.BaseHandler;
import io.netty.channel.Channel;
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
        String sessionId = msg.getSessionId();
        byte[] bytes = dataPackage.getBytes().toByteArray();
        Channel channel = ChannelCache.getInstance().getChannel(sessionId);
        channel.writeAndFlush(bytes);
    }

}
