package univ.inu.Capstone.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ActivateNfcLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activateLogSeq;   // 구분자
    private int useYn;         // 사용 여부(0:미사용 / 1:사용)

    @Column(updatable = false)
    private LocalDateTime deactivateTime; // 활성화 종료 시간
    @Column(updatable = false)
    private LocalDateTime inpDate; // 생성일
    private LocalDateTime modDate; // 수정일

    @ManyToOne
    @JoinColumn(name="doorLockSeq")
    private DoorLock doorLock;          // 도어락 구분자

    @ManyToOne
    @JoinColumn(name="userSeq")
    private User user;          // 사용자 구분자


    /* =======================================================
     * 생성일, 수정일 셋팅
     * ======================================================= */
    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        this.inpDate = now;
        this.deactivateTime = now.plusMinutes(1L);
    }

    @PreUpdate
    public void PreUpdate(){
        this.modDate = LocalDateTime.now();
    }



    // 사용여부 업데이트
    public void updateUseYn(){
        this.useYn = 1;
    }
}
