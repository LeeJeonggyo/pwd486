package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import univ.inu.Capstone.common.entity.OpenLog;

import java.util.List;
import java.util.Optional;

public interface OpenLogRepository extends JpaRepository<OpenLog, Long> {
    List<OpenLog> findByDoorLock_DoorLockSeqOrderByOpenLogSeqDesc(Long doorLockSeq);
    Optional<OpenLog> findTopByOpenYnAndDoorLock_DoorLockSeqOrderByOpenLogSeqDesc(int openYn, Long doorLockSeq);



    @Query(value = "SELECT * " +
            "FROM open_log ol " +
            "WHERE ol.open_method = 4 " +
            "AND ol.open_yn = 1 " +
            "AND ol.door_lock_seq = :doorLockSeq " +
            "AND ol.inp_date > DATE_SUB(NOW(), INTERVAL 1 MINUTE) " +
            "order by ol.open_log_seq desc " +
            "limit 1 ", nativeQuery = true)
    Optional<OpenLog> findRecentTaglessLog(@Param("doorLockSeq") Long doorLockSeq);
}
