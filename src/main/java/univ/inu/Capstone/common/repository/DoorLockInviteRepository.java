package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import univ.inu.Capstone.common.entity.DoorLockInvite;

import java.util.Optional;

public interface DoorLockInviteRepository extends JpaRepository<DoorLockInvite, Long> {
    @Query("SELECT e " +
            "FROM DoorLockInvite e " +
            "WHERE e.inviteCode = :inviteCode" +
            "AND e.inpDate > DATE_SUB(NOW(), INTERVAL 1 MINUTE)" +
            "AND e.useYn = 0")
    Optional<DoorLockInvite> findByInviteCode(@Param("inviteCode") String inviteCode);

    @Query("SELECT e " +
            "FROM DoorLockInvite e " +
            "WHERE e.userSeq = :userSeq" +
            "AND e.inpDate > DATE_SUB(NOW(), INTERVAL 1 MINUTE)" +
            "AND e.useYn = 0")
    Optional<DoorLockInvite> findByInviteSeq(@Param("userSeq") Long userSeq);
}
