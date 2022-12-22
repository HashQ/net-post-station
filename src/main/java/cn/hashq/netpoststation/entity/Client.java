package cn.hashq.netpoststation.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Client {

    /**
     * 客户端Id
     */
    private String clientId;


    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 密钥
     */
    private String secret;

    /**
     * 客户端IP
     */
    private String clientIP;

    /**
     * 状态 0:关闭;1:开放
     */
    private int status;

}
