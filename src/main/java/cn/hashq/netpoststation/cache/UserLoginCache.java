package cn.hashq.netpoststation.cache;

import java.util.concurrent.ConcurrentHashMap;

public class UserLoginCache {

    private ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    private static UserLoginCache instance = new UserLoginCache();

    private UserLoginCache() {
    }

    public static UserLoginCache getInstance() {
        return instance;
    }

    public void addLoginCache(String key, String value) {
        map.put(key, value);
    }

    public String getLoginCache(String key) {
        return map.get(key);
    }

}
