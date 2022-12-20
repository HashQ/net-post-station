package cn.hashq.netpoststation.server;

import cn.hashq.netpoststation.handler.AuthHandler;
import cn.hashq.netpoststation.handler.ProtobufDecoder;
import cn.hashq.netpoststation.util.NettyUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 代理服务器
 * <p>
 * 核心服务，必须启动，具备以下功能
 * <li>转发代理数据</li>
 * <li>动态下发配置</li>
 * </p>
 *
 * @author HashQ
 * @since 1.0
 */
@Slf4j
@Component
public class ProxyServer {

    @Resource
    private NettyUtil nettyUtil;

    @Resource
    private AuthHandler authHandler;

    public void run(int port) {
        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addFirst("decoder", new ProtobufDecoder());
                socketChannel.pipeline().addLast("auth", authHandler);
            }
        };
        ChannelFutureListener channelFutureListener = new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isDone()) {
                    log.info("代理端口{}启动成功", port);
                }
            }
        };
        ChannelFuture future = nettyUtil.openServerPort(port, channelInitializer, channelFutureListener);
        ChannelFuture closeFuture = future.channel().closeFuture();
        try {
            closeFuture.channel().close().sync();
        } catch (InterruptedException e) {
            log.error("关闭代理端口{}失败，原因:{}", port, e.getMessage());
        }
    }

}
