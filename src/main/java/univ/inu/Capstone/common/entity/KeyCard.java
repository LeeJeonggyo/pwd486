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
public class KeyCard extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keyCardSeq;            // 구분자
    private String keyCardName;         // 카드키 별명
    private String keyCardData;         // 등록된 카드키 번호

    @ManyToOne
    @JoinColumn(name="doorLockSeq")
    private DoorLock doorLock;  // 도어락 테이블 조인
}
