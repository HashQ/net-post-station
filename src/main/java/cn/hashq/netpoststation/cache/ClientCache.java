package cn.hashq.netpoststation.cache;

import cn.hashq.netpoststation.entity.Client;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端缓存
 *
 * @author HashQ
 * @since 1.0
 */
@Data
@Slf4j
public class ClientCache {

    private ConcurrentHashMap<String, Client> map = new ConcurrentHashMap<>();

    private static ClientCache instance = new ClientCache();

    private ClientCache() {
    }

    public static ClientCache getInstance() {
        return instance;
    }

    public void addClient(Client client) {
        if (!map.containsKey(client.getSecret())) {
            map.put(client.getSecret(), client);
        }
    }

    public Client getClientBySecret(String secret) {
        return map.get(secret);
    }

    public Client getClientByClientId(String clientId) {
        Optional<Client> first = map.values().stream()
                .filter(e -> StrUtil.equals(e.getClientId(), clientId))
                .findFirst();
        if (first.isPresent()) return first.get();
        return null;
    }

}
