package univ.inu.Capstone.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import univ.inu.Capstone.phone.settings.dto.SettingsRequestDto;

import javax.persistence.*;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DoorLockSecret extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dlSecretSeq;               // 구분자
    private String dlSecretNo = "0000";     // 비밀번호

    @OneToOne
    @JoinColumn(name="doorLockSeq")
    private DoorLock doorLock;  // 도어락 테이블 조인

    @ManyToOne
    @JoinColumn(name="userSeq")
    private User user;  // 수정 사용자


    /* ============================================================
     * update
     * ============================================================ */
    public void changePw(String newSecretNo, Long userSeq){
        this.dlSecretNo = newSecretNo;
        this.user = User.builder().userSeq(userSeq).build();
    }
}
