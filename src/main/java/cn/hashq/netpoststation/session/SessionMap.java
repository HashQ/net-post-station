package cn.hashq.netpoststation.session;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Slf4j
public final class SessionMap {

    private SessionMap() {
    }

    private static SessionMap singleInstance = new SessionMap();

    private ConcurrentHashMap<String, ServerSession> map = new ConcurrentHashMap<>();

    public static SessionMap inst() {
        return singleInstance;
    }

    public void addSession(ServerSession session) {
        map.put(session.getClientId(), session);
    }

    public ServerSession getSession(String clientId) {
        return map.get(clientId);
    }

    public void removeSession(String sessionId) {
        if (!map.containsKey(sessionId)) return;
        map.remove(sessionId);
    }

}
