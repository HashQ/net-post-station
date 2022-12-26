package cn.hashq.netpoststation.runner;

import cn.hashq.netpoststation.cache.ClientCache;
import cn.hashq.netpoststation.cache.PortMapCache;
import cn.hashq.netpoststation.entity.Client;
import cn.hashq.netpoststation.entity.PortMap;
import cn.hashq.netpoststation.fao.ClientFAO;
import cn.hashq.netpoststation.fao.PortMapFAO;
import cn.hashq.netpoststation.handler.map.PortMapDataHandler;
import cn.hashq.netpoststation.util.NettyUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PortMapRunner implements CommandLineRunner {


    @Resource
    private NettyUtil nettyUtil;

    @Override
    public void run(String... args) throws Exception {
        loadClientToMemory();
        loadPortMapToMemory();
        ClientCache.getInstance().getMap()
                .values().stream()
                .forEach(e -> {
                    if (e.getStatus() == 1) {
                        startPortMap(e.getClientId());
                    }
                });
    }

    private void startPortMap(String clientId) {
        List<PortMap> portMaps = PortMapCache.getInstance()
                .getMap().values()
                .stream()
                .filter(e -> StrUtil.equals(e.getClientId(), clientId) && e.getStatus() == 1)
                .collect(Collectors.toList());
        portMaps.stream().forEach(e -> {
            startPortMap0(e);
        });
    }

    private void startPortMap0(PortMap portMap) {
        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new PortMapDataHandler(portMap.getServerPort()));
            }
        };
        GenericFutureListener listener = new GenericFutureListener() {
            @Override
            public void operationComplete(Future future) throws Exception {
                if (future.isSuccess()) {
                    log.info("映射端口{}打开成功", portMap.getServerPort());
                } else {
                    log.info("映射端口{}打开失败,原因:{}", portMap.getServerPort(), future.cause().getMessage());
                }
            }
        };
        nettyUtil.openServerPort(portMap.getServerPort(), channelInitializer, listener);

    }

    private void loadPortMapToMemory() {
        List<PortMap> portMaps = PortMapFAO.listPortMap();
        portMaps.stream().forEach(e -> {
            PortMapCache.getInstance().addPortMap(e);
        });
    }

    private void loadClientToMemory() {
        List<Client> clients = ClientFAO.listClient();
        clients.stream().forEach(e -> {
            ClientCache.getInstance().addClient(e);
        });
    }

}
