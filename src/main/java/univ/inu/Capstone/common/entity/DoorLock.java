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
public class DoorLock extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doorLockSeq;   // 구분자
    private String serialNo;    // 시리얼넘버

    @OneToMany(mappedBy = "doorLock")
    private RegistDoorLock registDoorLock;
}

