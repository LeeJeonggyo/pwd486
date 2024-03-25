package univ.inu.Capstone.common.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import univ.inu.Capstone.phone.login.LoginService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginService loginService;
    @Value("${jwt.secret.ACCESS_TOKEN_KEY}")
    private String accessTokenKey;
    @Value("${jwt.secret.REFRESH_TOKEN_KEY}")
    private String refreshTokenKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws  Exception{
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/pw486/user/firstLogin"
                        , "/pw486/user/refreshLogin"
                        , "/api/pw486/regist/machine").permitAll()
                .antMatchers("/api/pw486/**").authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt 사용하는 경우 사용
                .and()
                .addFilterBefore(new JwtFilter(loginService, accessTokenKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
