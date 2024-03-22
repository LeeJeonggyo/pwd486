package univ.inu.Capstone.common.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class BaseEntity {

    @Column(updatable = false)
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

}
