package cn.hashq.netpoststation.service.impl;

import cn.hashq.netpoststation.cache.UserLoginCache;
import cn.hashq.netpoststation.constant.AuthConstant;
import cn.hashq.netpoststation.dto.LoginDTO;
import cn.hashq.netpoststation.dto.UserDTO;
import cn.hashq.netpoststation.entity.User;
import cn.hashq.netpoststation.fao.UserFAO;
import cn.hashq.netpoststation.service.UserService;
import cn.hashq.netpoststation.vo.ServerResponse;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public ServerResponse login(LoginDTO loginDTO) {
        User user = UserFAO.getUser();
        if (StrUtil.equals(user.getUsername(), loginDTO.getUsername())
                && StrUtil.equals(user.getPassword(), loginDTO.getPassword())) {
            Algorithm algorithm = Algorithm.HMAC256(user.getPassword());
            long start = System.currentTimeMillis() - 60000;
            Date end = new Date(start + AuthConstant.TOKEN_TIME_OUT * 1000);
            String token = JWT.create()
                    .withSubject(user.getUsername())
                    .withIssuedAt(new Date(start))
                    .withExpiresAt(end)
                    .sign(algorithm);
            UserDTO dto = new UserDTO();
            dto.setUsername(user.getUsername());
            dto.setPassword(user.getPassword());
            dto.setToken(token);
            UserLoginCache.getInstance().addLoginCache(user.getUsername(), JSON.toJSONString(dto));
            return ServerResponse.createSuccessResult(token);
        }
        return ServerResponse.createFailedResult(401, "用户名或密码错误");
    }
}
