package cn.hashq.netpoststation.entity;

import lombok.Data;

/**
 * 客户端实体
 *
 * @author HashQ
 * @since 1.0
 */
@Data
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
