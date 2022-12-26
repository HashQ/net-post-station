package cn.hashq.netpoststation.fao;

import cn.hashq.netpoststation.entity.Client;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Client文件访问对象
 *
 * @author HashQ
 * @since 1.0
 */
public class ClientFAO {

    public static final String CLIENT_PATH = FileUtil.getUserHomePath() + File.separator + ".net-post-station" + File.separator + "client.json";

    public synchronized static List<Client> listClient() {
        if (!FileUtil.exist(CLIENT_PATH)) {
            FileUtil.touch(CLIENT_PATH);
            return Lists.newArrayList();
        }
        String clientJsonStr = FileUtil.readString(CLIENT_PATH, "utf-8");
        List<Client> clients = JSON.parseArray(clientJsonStr, Client.class);
        return clients;
    }

    public synchronized static void addClient(Client client) {
        List<Client> clients = listClient();
        clients.add(client);
        FileUtil.del(CLIENT_PATH);
        FileUtil.writeUtf8String(JSON.toJSONString(clients), CLIENT_PATH);
    }

    public synchronized static void delClient(String clientId) {
        List<Client> clients = listClient();
        Iterator<Client> iterator = clients.iterator();
        while (iterator.hasNext()) {
            Client next = iterator.next();
            if (StrUtil.equals(next.getClientId(), clientId)) {
                iterator.remove();
            }
        }
        FileUtil.del(CLIENT_PATH);
        FileUtil.writeUtf8String(JSON.toJSONString(clients), CLIENT_PATH);
    }

    public synchronized static void updateClient(Client client) {
        List<Client> clients = listClient();
        Iterator<Client> iterator = clients.iterator();
        while (iterator.hasNext()) {
            if (StrUtil.equals(client.getClientId(), iterator.next().getClientId())) {
                iterator.remove();
            }
        }
        clients.add(client);
        FileUtil.del(CLIENT_PATH);
        FileUtil.writeUtf8String(JSON.toJSONString(clients), CLIENT_PATH);
    }
}
