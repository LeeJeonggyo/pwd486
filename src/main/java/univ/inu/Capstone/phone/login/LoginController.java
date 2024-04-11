package univ.inu.Capstone.phone.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import univ.inu.Capstone.common.dto.apiResponse.ApiResponse;
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
     * @return ResponseEntity<?>
     */
    @PostMapping("/firstLogin")
    public ResponseEntity<?> firstLogin(@RequestBody LoginDto dto) {
        return ResponseEntity.ok().body(loginService.firstLogin(dto));
    }

    /**
     * 지문 로그인 - accessToken 으로 로그인 (only accessToken)
     * @param authentication Authentication
     * @return ResponseEntity<?>
     */
    @PostMapping("/accessLogin")
    public ResponseEntity<?> accessLogin(Authentication authentication) {
        return ResponseEntity.ok().body(
                ApiResponse.SUCCESS("SUCCESS : 로그인 완료",
                    TokenDto.accessLogin.builder()
                        .nickname(authentication.getName())
                        .build()));
    }

    /**
     * accessToken 재발급 (only refreshToken)
     * @param request HttpServletRequest
     * @return ResponseEntity<?>
     */
    @PostMapping("/refreshLogin")
    public ResponseEntity<?> refreshLogin(HttpServletRequest request) {
        return ResponseEntity.ok().body(loginService.refreshLogin(request));
    }
}
