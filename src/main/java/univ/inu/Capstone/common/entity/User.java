package univ.inu.Capstone.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userSeq;   // 구분자

    private String kakaoId;     // 카카오 로그인시 발급되는 id
    private String nickname;    // 카카오 로그인 이름
    private String email;       // 카카오 로그인 이메일

    private String refreshToken;       // refreshToken

    /* =======================================================
     * update
     * ======================================================= */
    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
}
