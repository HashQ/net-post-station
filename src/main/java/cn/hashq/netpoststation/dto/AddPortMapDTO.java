package cn.hashq.netpoststation.dto;

import lombok.Data;

/**
 * 增加端口映射用DTO
 *
 * @author HashQ
 * @since 1.0
 */
@Data
public class AddPortMapDTO {


    /**
     * 客户端id
     */
    private int clientId;

    /**
     * 服务器端口
     */
    private int serverPort;

    /**
     * 客户端端口
     */
    private int clientPort;
}
