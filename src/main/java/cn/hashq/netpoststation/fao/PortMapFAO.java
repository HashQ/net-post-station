package cn.hashq.netpoststation.fao;

import cn.hashq.netpoststation.entity.PortMap;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * PortMap文件访问对象
 *
 * @author HashQ
 * @since 1.0
 */
public class PortMapFAO {

    public static final String PORT_MAP_PATH = FileUtil.getUserHomePath() + File.separator + ".net-post-station" + File.separator + "PortMap.json";

    public synchronized static List<PortMap> listPortMap() {
        if (!FileUtil.exist(PORT_MAP_PATH)) {
            FileUtil.touch(PORT_MAP_PATH);
            return Lists.newArrayList();
        }
        String clientJsonStr = FileUtil.readString(PORT_MAP_PATH, "utf-8");
        List<PortMap> clients = JSON.parseArray(clientJsonStr, PortMap.class);
        return clients;
    }

    public synchronized static void addPortMap(PortMap portMap) {
        List<PortMap> clients = listPortMap();
        clients.add(portMap);
        FileUtil.del(PORT_MAP_PATH);
        FileUtil.writeUtf8String(JSON.toJSONString(clients), PORT_MAP_PATH);
    }

    public synchronized static void delPortMap(String id) {
        List<PortMap> clients = listPortMap();
        Iterator<PortMap> iterator = clients.iterator();
        while (iterator.hasNext()) {
            PortMap next = iterator.next();
            if (StrUtil.equals(next.getClientId(), id)) {
                iterator.remove();
            }
        }
        FileUtil.del(PORT_MAP_PATH);
        FileUtil.writeUtf8String(JSON.toJSONString(clients), PORT_MAP_PATH);
    }

    public synchronized static void updatePortMap(PortMap client) {
        List<PortMap> clients = listPortMap();
        Iterator<PortMap> iterator = clients.iterator();
        while (iterator.hasNext()) {
            if (StrUtil.equals(client.getClientId(), iterator.next().getClientId())) {
                iterator.remove();
            }
        }
        clients.add(client);
        FileUtil.del(PORT_MAP_PATH);
        FileUtil.writeUtf8String(JSON.toJSONString(clients), PORT_MAP_PATH);
    }
}
