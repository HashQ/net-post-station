package cn.hashq.netpoststation.cache;

import cn.hashq.netpoststation.entity.PortMap;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

@Data
public class PortMapCache {

    private ConcurrentHashMap<Integer, PortMap> map = new ConcurrentHashMap<>();

    private static PortMapCache instance = new PortMapCache();

    private PortMapCache() {
    }

    public static PortMapCache getInstance() {
        return instance;
    }

    public void addClient(PortMap portMap) {
        if (!map.containsKey(portMap.getServerPort())) {
            map.put(portMap.getServerPort(), portMap);
        }
    }

    public PortMap getPortMapByServerPort(int serverPort) {
        return map.get(serverPort);
    }

}
