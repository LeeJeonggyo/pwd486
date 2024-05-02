package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.inu.Capstone.common.entity.OpenLog;

import java.util.List;
import java.util.Optional;

public interface OpenLogRepository extends JpaRepository<OpenLog, Long> {
    List<OpenLog> findByDoorLock_DoorLockSeq(Long doorLockSeq);
    Optional<OpenLog> findTopByOpenYnAndDoorLock_DoorLockSeqOrderByOpenLogSeqDesc(int openYn, Long doorLockSeq);
}
