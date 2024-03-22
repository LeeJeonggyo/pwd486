package univ.inu.Capstone.common.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import univ.inu.Capstone.common.utils.JwtUtil;
import univ.inu.Capstone.login.LoginService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final LoginService loginService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 특정 url 필터링 제외
        if ("/pw486/user/firstLogin".equals(request.getRequestURI())
                || "/pw486/user/refreshLogin".equals(request.getRequestURI())) {
            log.info("필터링에서 제외합니다.");
            filterChain.doFilter(request, response);
            return;
        }


        // 토큰 전달 여부 확인
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer")){
            log.error("잘못된 authorization 입니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 꺼내기
        String token = authorization.split(" ")[1];

        // 토큰 Expired 여부 확인
        if(JwtUtil.isExpired(token, secretKey)){
            log.error("Token이 만료되었습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // 1. 토큰에서 userSeq 꺼내기
        Long userSeq = JwtUtil.getUserSeq(token, secretKey);
        log.info("userSeq : {}", userSeq);

        // 2. 권한 부여하기
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userSeq, null, List.of(new SimpleGrantedAuthority("USER")));

        // 3. Detail 넣어주기
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
