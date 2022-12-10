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
import org.springframework.stereotype.Component;

@Component
public class NettyUtil {

    public ChannelFuture openServerPort(int port, ChannelInitializer<SocketChannel> channels, GenericFutureListener listener) {
        EventLoopGroup bg = new NioEventLoopGroup(1);
        EventLoopGroup wg = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bg, wg);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.localAddress(port);
        bootstrap.childHandler(channels);
        ChannelFuture future = bootstrap.bind();
        future.addListener(listener);
        return future;
    }
}
