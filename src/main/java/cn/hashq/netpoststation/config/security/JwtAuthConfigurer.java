package cn.hashq.netpoststation.config.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;

public class JwtAuthConfigurer<T extends JwtAuthConfigurer<T, B>, B extends HttpSecurityBuilder<B>> extends AbstractHttpConfigurer<T, B> {

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    public JwtAuthConfigurer() {
        this.jwtAuthenticationFilter = new JwtAuthenticationFilter();
    }

    @Override
    public void configure(B http) throws Exception {
        jwtAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        jwtAuthenticationFilter.setFailureHandler(new AuthFailureHandler());
        JwtAuthenticationFilter filter = postProcess(jwtAuthenticationFilter);
        http.addFilterBefore(filter, LogoutFilter.class);
    }

    public JwtAuthConfigurer<T, B> permissiveRequestUrls(String... urls) {
        jwtAuthenticationFilter.setPermissiveUrl(urls);
        return this;
    }

    public JwtAuthConfigurer<T, B> tokenValidSuccessHandler(AuthenticationSuccessHandler successHandler) {
        jwtAuthenticationFilter.setSuccessHandler(successHandler);
        return this;
    }


}
