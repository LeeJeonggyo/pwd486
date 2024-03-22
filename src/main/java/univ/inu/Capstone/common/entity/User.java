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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userSeq;   // 구분자

    private String kakaoId;     // 카카오 로그인시 발급되는 id
    private String nickname;    // 카카오 로그인 이름
    private String email;       // 카카오 로그인 이메일

    private String refreshToken;       // refreshToken

    private LocalDateTime inpDate; // 생성일
    private LocalDateTime modDate; // 수정일

    /* =======================================================
     * 생성일, 수정일 셋팅
     * ======================================================= */
    @PrePersist
    public void prePersist(){
        this.inpDate = LocalDateTime.now();
    }

    @PreUpdate
    public void PreUpdate(){
        this.modDate = LocalDateTime.now();
    }

    /* =======================================================
     * update
     * ======================================================= */
    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
}
