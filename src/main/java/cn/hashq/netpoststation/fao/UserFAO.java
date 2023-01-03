package cn.hashq.netpoststation.fao;

import cn.hashq.netpoststation.entity.User;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;

import java.io.File;

public class UserFAO {

    public static final String USER_PATH = FileUtil.getUserHomePath() + File.separator + ".net-post-station" + File.separator + "user.json";

    public synchronized static User getUser() {
        if (!FileUtil.exist(USER_PATH)) {
            FileUtil.touch(USER_PATH);
            User defaultUser = new User();
            defaultUser.setUsername("admin");
            defaultUser.setPassword("admin");
            FileUtil.writeUtf8String(JSON.toJSONString(defaultUser), USER_PATH);
            return defaultUser;
        }
        String clientJsonStr = FileUtil.readString(USER_PATH, "utf-8");
        User user = JSON.parseObject(clientJsonStr, User.class);
        return user;
    }
}
