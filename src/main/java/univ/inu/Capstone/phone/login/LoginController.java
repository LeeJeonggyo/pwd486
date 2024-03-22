package univ.inu.Capstone.phone.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import univ.inu.Capstone.phone.login.dto.LoginDto;
import univ.inu.Capstone.phone.login.dto.TokenDto;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/pw486/user")
public class LoginController {

    private final LoginService loginService;

    /**
     * 카카오 로그인 (최초) /
     * 파라미터 = kakaoId, nickname, email 넘어옴. /
     * return = accessToken, refreshToken /
     */
    @PostMapping("/firstLogin")
    public ResponseEntity<TokenDto> firstLogin(@RequestBody LoginDto dto) {
        TokenDto result = loginService.firstLogin(dto);
        return ResponseEntity.ok().body(result);
    }

    /**
     * 지문 로그인 - accessToken 으로 로그인 (only accessToken) /
     * 파라미터 = accessToken /
     * return = nickname /
     */
    @PostMapping("/accessLogin")
    public ResponseEntity<String> accessLogin(Authentication authentication) {
        return ResponseEntity.ok().body(authentication.getName());
    }

    /**
     * 지문 로그인 - accessToken 재발급 (only refreshToken) /
     * 파라미터 = refreshToken /
     * return = ok /
     */
    @PostMapping("/refreshLogin")
    public ResponseEntity<TokenDto> refreshLogin(HttpServletRequest request) {
        TokenDto result = loginService.refreshLogin(request);
        return ResponseEntity.ok().body(result);
    }
}
