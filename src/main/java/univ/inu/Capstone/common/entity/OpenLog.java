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
    private Long openMethod;    // 해제 방법 구분자(1: NFC / 2: 비밀번호 / 3: 카드키 / 4: 지문)

    @ManyToOne
    @JoinColumn(name="userSeq")
    private User user;          // 사용자 구분자

    @ManyToOne
    @JoinColumn(name="doorLockSeq")
    private DoorLock doorLock;          // 도어락 구분자
}
