package univ.inu.Capstone.phone.login.dto;

import lombok.Getter;

@Getter
public class LoginDto {
    private String kakaoId;     // 카카오 로그인시 발급되는 id
    private String nickname;    // 카카오 로그인 이름
    private String email;       // 카카오 로그인 이메일
}
