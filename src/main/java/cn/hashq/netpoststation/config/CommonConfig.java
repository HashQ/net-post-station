package cn.hashq.netpoststation.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 公共配置类
 */
@Data
//@Configuration
public class CommonConfig {

    /**
     * 配置下发端口
     */
    @Value("${common.port}")
    private int port;

    /**
     * 管理页用户名
     */
    @Value("${common.username}")
    private String username;

    /**
     * 管理页密码
     */
    @Value("${common.password}")
    private String password;
}
