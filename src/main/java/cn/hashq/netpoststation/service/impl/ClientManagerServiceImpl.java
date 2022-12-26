package cn.hashq.netpoststation.service.impl;

import cn.hashq.netpoststation.cache.ClientCache;
import cn.hashq.netpoststation.dto.AddClientDTO;
import cn.hashq.netpoststation.entity.Client;
import cn.hashq.netpoststation.service.ClientManagerService;
import cn.hashq.netpoststation.vo.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Slf4j
@Service
public class ClientManagerServiceImpl implements ClientManagerService {

    @Override
    public ServerResponse addClient(AddClientDTO clientDTO) {
        return null;
    }

    @Override
    public ServerResponse listClient() {
        Collection<Client> clients = ClientCache.getInstance().getMap().values();
        return ServerResponse.createSuccessResult(clients);
    }
}
