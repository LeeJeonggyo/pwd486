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
public class RegistDoorLock extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rdlSeq;    // 구분자
    private String rdlName; // 이름

    @Column(columnDefinition = "VARCHAR(11) BINARY NOT NULL")
    private String nfcData; // NFC 데이터
    private int rdlAuth;    // 권한 (1:OWNER / 2:MEMBER / 3:GUEST)
    private int rdlApprove; // 승인여부 (0: 미승인 / 1: 승인) - OWNER 권한은 무조건 1

    @ManyToOne
    @JoinColumn(name="userSeq")
    private User user;       // 사용자 구분자

    @ManyToOne
    @JoinColumn(name="doorLockSeq")
    private DoorLock doorLock;   // 도어락 구분자

    // 승인여부 변경 (0: 미승인 / 1: 승인)
    public void permit(){
        this.rdlApprove = 1;
    }

    // 권한 변경
    public void changeAuth(int rdlAuth){
        this.rdlAuth = rdlAuth;
    }


}
