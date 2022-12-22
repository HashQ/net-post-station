package cn.hashq.netpoststation.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 转发数据用Handler
 * <p>
 * 转发客户端响应的数据包
 * </p>
 * @author HashQ
 * @since 1.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ClientDataRedirectHandler extends ChannelInboundHandlerAdapter {
}
