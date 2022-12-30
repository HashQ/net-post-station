package cn.hashq.netpoststation.cache;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelCache {

    private ConcurrentHashMap<String, Channel> map = new ConcurrentHashMap<>();

    private static ChannelCache instance = new ChannelCache();

    private ChannelCache() {
    }

    public static ChannelCache getInstance() {
        return instance;
    }

    public void addChannel(Channel channel) {
        map.put(channel.id().asLongText(), channel);
    }

    public Channel getChannel(String key) {
        return map.get(key);
    }

    public void removeChannel(String key) {
        map.remove(key);
    }
}
