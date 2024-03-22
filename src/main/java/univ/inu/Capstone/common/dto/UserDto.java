package univ.inu.Capstone.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class UserDto {
    private Long userSeq;   // 구분자
    private String kakaoId;     // 카카오 로그인시 발급되는 id
    private String nickname;    // 카카오 로그인 이름
    private String email;       // 카카오 로그인 이메일
    private LocalDateTime inpDate; // 생성일
    private LocalDateTime modDate; // 수정일
}
