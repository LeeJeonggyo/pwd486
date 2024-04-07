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
     * 카카오 로그인 (최초)
     * @param dto LoginDto
     * @return ResponseEntity<TokenDto.responseDto>
     */
    @PostMapping("/firstLogin")
    public ResponseEntity<TokenDto.responseDto> firstLogin(@RequestBody LoginDto dto) {
        return ResponseEntity.ok().body(loginService.firstLogin(dto));
    }

    /**
     * 지문 로그인 - accessToken 으로 로그인 (only accessToken)
     * @param authentication Authentication
     * @return ResponseEntity<TokenDto.accessLogin>
     */
    @PostMapping("/accessLogin")
    public ResponseEntity<TokenDto.accessLogin> accessLogin(Authentication authentication) {
        return ResponseEntity.ok().body(TokenDto.accessLogin.builder()
                .result("SUCCESS")
                .nickname(authentication.getName())
                .build());
    }

    /**
     * accessToken 재발급 (only refreshToken)
     * @param request HttpServletRequest
     * @return ResponseEntity<TokenDto.responseDto>
     */
    @PostMapping("/refreshLogin")
    public ResponseEntity<TokenDto.responseDto> refreshLogin(HttpServletRequest request) {
        return ResponseEntity.ok().body(loginService.refreshLogin(request));
    }
}
