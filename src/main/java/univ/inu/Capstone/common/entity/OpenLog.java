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
public class OpenLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long openLogSeq;    // 구분자
    private int openYn;         // 해제 여부(0:fail / 1:success)
    private Long openMethod;    // 해제 방법 구분자(1: 비밀번호 / 2: TAG(NFC or RFID) / 3: 지문 / 4:태그리스)
    private String nickname;    // 해제 당사자 이름


    @ManyToOne
    @JoinColumn(name="doorLockSeq")
    private DoorLock doorLock;          // 도어락 구분자
}
