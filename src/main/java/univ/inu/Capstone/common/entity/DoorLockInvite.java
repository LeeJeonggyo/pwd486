package univ.inu.Capstone.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DoorLockInvite extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inviteSeq; // 구분자

    private String inviteCode;  // 초대코드
    private int rdlAuth;        // 권한 (2:MEMBER / 3:GUEST)
    private int useYn;          // 사용여부 (0: 미사용 / 1: 사용)

    @ManyToOne
    @JoinColumn(name="doorLockSeq")
    private DoorLock doorLock;  // 도어락 테이블 조인

    @ManyToOne
    @JoinColumn(name="userSeq")
    private User user;  // 등록 사용자


    public void usingCode(){
        this.useYn = 1;
    }
}
