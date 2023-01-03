package cn.hashq.netpoststation.config.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private UserDetails userDetails;

    private DecodedJWT decodedJWT;

    public JwtAuthenticationToken(DecodedJWT jwt) {
        super(Collections.emptyList());
        this.decodedJWT = jwt;
    }

    public JwtAuthenticationToken(UserDetails userDetails, DecodedJWT jwt, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userDetails = userDetails;
        this.decodedJWT = jwt;
    }

    @Override
    public Object getCredentials() {
        return userDetails;
    }

    @Override
    public Object getPrincipal() {
        return decodedJWT.getSubject();
    }


    public void setDecodedJWT(DecodedJWT decodedJWT) {
        this.decodedJWT = decodedJWT;
    }


    public DecodedJWT getDecodedJWT() {
        return decodedJWT;
    }
}
