package univ.inu.Capstone.phone.login.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenDto {
    private String nickname;
    private String accessToken;
    private String refreshToken;
}
