package cn.hashq.netpoststation.service;

import cn.hashq.netpoststation.dto.AddClientDTO;
import cn.hashq.netpoststation.entity.Client;
import cn.hashq.netpoststation.vo.ServerResponse;

/**
 * 客户端管理服务类
 *
 * @author HashQ
 * @since 1.0
 */
public interface ClientManagerService {

    ServerResponse addClient(AddClientDTO clientDTO);

    ServerResponse listClient();
}
