package cn.hashq.netpoststation.service;

import cn.hashq.netpoststation.dto.AddPortMapDTO;
import cn.hashq.netpoststation.vo.ServerResponse;

/**
 * 端口映射服务类
 *
 * @author HashQ
 * @since 1.0
 */
public interface PortMapManagerService {

    ServerResponse addPortMap(AddPortMapDTO portMapDTO);

}
