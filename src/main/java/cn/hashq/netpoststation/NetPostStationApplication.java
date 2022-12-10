package cn.hashq.netpoststation;

import cn.hashq.netpoststation.config.CommonConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.Resource;

@Slf4j
@SpringBootApplication
public class NetPostStationApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(NetPostStationApplication.class, args);

    }

}
