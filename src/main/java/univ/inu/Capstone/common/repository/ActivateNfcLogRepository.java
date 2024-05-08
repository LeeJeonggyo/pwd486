package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import univ.inu.Capstone.common.entity.ActivateNfcLog;

import java.util.Optional;

public interface ActivateNfcLogRepository extends JpaRepository<ActivateNfcLog, Long> {

    @Query(value = "SELECT * " +
            "FROM activate_nfc_log anl " +
            "WHERE door_lock_seq = :doorLockSeq " +
            "AND use_yn = 0 " +
            "AND inp_date <= now() " +
            "AND deactivate_time >= now()", nativeQuery = true)
    Optional<ActivateNfcLog> findActivateLog(@Param("doorLockSeq") Long doorLockSeq);
}
