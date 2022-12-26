package cn.hashq.netpoststation.runner;

import cn.hashq.netpoststation.handler.AuthHandler;
import cn.hashq.netpoststation.handler.ExceptionHandler;
import cn.hashq.netpoststation.handler.ProtobufDecoder;
import cn.hashq.netpoststation.handler.ProtobufEncoder;
import cn.hashq.netpoststation.util.NettyUtil;
import cn.hutool.core.collection.CollUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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
public class ProxyServerRunner implements ApplicationRunner {

    @Resource
    private NettyUtil nettyUtil;

    @Resource
    private AuthHandler authHandler;

    @Resource
    private ExceptionHandler exceptionHandler;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("打开代理端口");
        List<String> proxyPorts = args.getOptionValues("proxyPort");
        Integer proxyPort = null;
        if (CollUtil.isNotEmpty(proxyPorts)) {
            proxyPort = Integer.valueOf(proxyPorts.get(0));
        }
        startServer(proxyPort);
    }

    private void startServer(Integer proxyProt) {
        int defaultPort = 8090;
        if (Objects.nonNull(proxyProt)) {
            defaultPort = proxyProt;
        }
        run(defaultPort);
    }

    private void run(int port) {
        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast("decoder", new ProtobufDecoder());
                socketChannel.pipeline().addLast("encoder", new ProtobufEncoder());
                socketChannel.pipeline().addLast("auth", authHandler);
                socketChannel.pipeline().addLast(exceptionHandler);
            }
        };
        ChannelFutureListener channelFutureListener = new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    log.info("代理端口{}打开成功", port);
                } else {
                    log.info("端口启动失败,原因:{}", channelFuture.cause().getCause().getMessage());
                    System.exit(0);
                }
            }
        };
        ChannelFuture future = nettyUtil.openServerPort(port, channelInitializer, channelFutureListener);
        if (Objects.isNull(future)) {
            System.exit(0);
            return;
        }
        ChannelFuture closeFuture = future.channel().closeFuture();

    }

}
