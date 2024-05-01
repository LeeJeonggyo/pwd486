package univ.inu.Capstone.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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
    @Column(columnDefinition = "VARCHAR(255) BINARY")
    private String refreshToken;    // refreshToken
    @Column(columnDefinition = "VARCHAR(255) BINARY")
    private String fcmToken;        // fcmToken : 파이어베이스에 저장한 해당 디바이스의 FCM 토큰 값

    @OneToMany(mappedBy = "user")
    private List<RegistDoorLock> registDoorLock;

    @OneToMany(mappedBy = "user")
    private List<DoorLockSecret> doorLockSecret;

    @OneToMany(mappedBy = "user")
    private List<DoorLockInvite> doorLockInvite;

    /* =======================================================
     * update
     * ======================================================= */
    // jwt 리프레시 토큰 update
    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    // fcmToken update
    public void updateFcmToken(String fcmToken){
        this.fcmToken = fcmToken;
    }
}
