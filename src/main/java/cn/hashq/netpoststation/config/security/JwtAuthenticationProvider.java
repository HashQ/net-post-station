package cn.hashq.netpoststation.config.security;

import cn.hashq.netpoststation.cache.UserLoginCache;
import cn.hashq.netpoststation.dto.UserDTO;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.NonceExpiredException;

import java.util.Calendar;
import java.util.Objects;

public class JwtAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        DecodedJWT jwt = ((JwtAuthenticationToken) authentication).getDecodedJWT();
        if (jwt.getExpiresAt().before(Calendar.getInstance().getTime())) {
            throw new NonceExpiredException("认证过期");
        }
        String sid = jwt.getSubject();
        // 获取令牌字符串,用于验证是否重复登录
        String presentToken = jwt.getToken();

        String loginCache = UserLoginCache.getInstance().getLoginCache(sid);
        if (StrUtil.isBlank(loginCache)) {
            throw new NonceExpiredException("还没有登录,请登录系统");
        }
        UserDTO userDTO = JSON.parseObject(loginCache, UserDTO.class);
        if (Objects.isNull(userDTO)) {
            throw new NonceExpiredException("认证有误,请重新登录");
        }
        if (Objects.isNull(presentToken) || !presentToken.equals(userDTO.getToken())) {
            throw new NonceExpiredException("您已在其他地方登录!");
        }

        try {
            // 密码密文作为salt
            String encryptSalt = userDTO.getPassword();
            Algorithm algorithm = Algorithm.HMAC256(encryptSalt);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withSubject(sid)
                    .build();
            verifier.verify(presentToken);
        } catch (Exception e) {
            throw new BadCredentialsException("认证有误:令牌校验失败,请重新登录", e);
        }
        UserDetails userDetails = User.builder()
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .authorities("user-info")
                .build();
        JwtAuthenticationToken passedToken = (JwtAuthenticationToken) authentication;
        passedToken.setAuthenticated(true);
        passedToken.setDetails(userDetails);
        return passedToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(JwtAuthenticationToken.class);
    }
}
