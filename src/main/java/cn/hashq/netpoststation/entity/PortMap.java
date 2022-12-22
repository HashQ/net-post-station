package cn.hashq.netpoststation.entity;

import lombok.Data;

@Data
public class PortMap {

    private String id;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 名称
     */
    private String name;

    /**
     * 服务端端口
     */
    private int serverPort;

    /**
     * 客户端端口
     */
    private int clientPort;

    /**
     * 状态 0:关闭;1:开放
     */
    private int status;
}
