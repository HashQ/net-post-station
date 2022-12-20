package cn.hashq.netpoststation;

import cn.hashq.netpoststation.server.ProxyServer;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
public class NetPostStationApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(NetPostStationApplication.class, args);
        Map<String, String> params = parseArgs(args);
        startServer(context, params);
    }

    private static void startServer(ApplicationContext context, Map<String, String> params) {
        int port = 8090;
        if (params.containsKey("p")) {
            port = Integer.parseInt(params.get("p"));
        }
        ProxyServer server = context.getBean(ProxyServer.class);
        server.run(port);
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> params = Maps.newHashMap();
        String commandStr = Arrays.stream(args).collect(Collectors.joining(" "));
        String[] realParams = commandStr.split("-");
        for (String realParam : realParams) {
            if (StrUtil.isNotBlank(realParam)) {
                String[] s = realParam.split(" ");
                params.put(s[0], s[1]);
            }
        }
        return params;
    }

}
