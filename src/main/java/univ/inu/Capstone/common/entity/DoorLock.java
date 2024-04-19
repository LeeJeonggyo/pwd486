package univ.inu.Capstone.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DoorLock extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doorLockSeq;   // 구분자
    private String serialNo;    // 시리얼넘버
    private int failCntSecretNo;    // 비밀번호 틀린 횟수 (최대 5회)
    private int failCntTag;         // rfid & nfc 태그 틀린 횟수 (최대 5회)
    private int failCntBio;         // 지문 틀린 횟수 (최대 5회)

    @OneToMany(mappedBy = "doorLock")
    private List<RegistDoorLock> registDoorLock;

    @OneToOne(mappedBy = "doorLock")
    private DoorLockSecret doorLockSecret;

    @OneToMany(mappedBy = "doorLock")
    private List<OpenLog> openLog;

    @OneToMany(mappedBy = "doorLock")
    private List<DoorLockInvite> doorLockInvite;

    @OneToMany(mappedBy = "doorLock")
    private List<KeyCard> keyCard;

    @OneToMany(mappedBy = "doorLock")
    private List<KeyBio> keyBio;

    /* =================================================================
     * update
     * ================================================================= */
    // 비밀번호 해제 결과에 따른 카운트 값 초기화
    public void openSecretNo(int success){
        if (success == 1) this.failCntSecretNo = 0;
        else this.failCntSecretNo += 1;
    }

    // 태그 해제 결과에 따른 카운트 값 초기화
    public void openTag(int success){
        if (success == 1) this.failCntTag = 0;
        else this.failCntTag += 1;
    }
}

