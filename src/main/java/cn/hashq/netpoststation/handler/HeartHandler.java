package cn.hashq.netpoststation.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 处理心跳Handler
 *
 * @author HashQ
 * @since 1.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class HeartHandler extends ChannelInboundHandlerAdapter {

}
