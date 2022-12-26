package cn.hashq.netpoststation.util;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.BindException;

@Slf4j
@Component
public class NettyUtil {
    private EventLoopGroup wg = new NioEventLoopGroup();

    public ChannelFuture openServerPort(int port, ChannelInitializer<SocketChannel> channels, GenericFutureListener listener) {
        try {
            EventLoopGroup bg = new NioEventLoopGroup(1);
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bg, wg);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.localAddress(port);
            bootstrap.childHandler(channels);
            ChannelFuture future  = bootstrap.bind().sync();
            future.addListener(listener);
            return future;
        } catch (InterruptedException e) {
            log.error("打开端口{}失败,原因:{}", port, e.getMessage());
        } catch (Exception e) {
            if (e instanceof BindException) {
                log.error("{}端口已被占用", port);
            }
        }
        return null;
    }
}
