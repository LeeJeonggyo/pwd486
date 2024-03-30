package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.inu.Capstone.common.entity.DoorLockSecret;

import java.util.Optional;

public interface DoorLockSecretRespository extends JpaRepository<DoorLockSecret, Long> {
    Optional<DoorLockSecret> findByDoorLock_DoorLockSeqAndDlSecretNo(Long doorLockSeq, String dlSecretNo);
}
