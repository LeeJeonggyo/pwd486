package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import univ.inu.Capstone.common.entity.DoorLockInvite;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DoorLockInviteRepository extends JpaRepository<DoorLockInvite, Long> {
    @Query("SELECT e " +
            "FROM DoorLockInvite e " +
            "WHERE e.inviteCode = :inviteCode " +
            "AND e.inpDate > :expireTime " +
            "AND e.useYn = 0")
    Optional<DoorLockInvite> findByInviteCode(@Param("inviteCode") String inviteCode, @Param("expireTime") LocalDateTime expireTime);

    @Query("SELECT e " +
            "FROM DoorLockInvite e " +
            "WHERE e.inviteSeq = :inviteSeq " +
            "AND e.inpDate > :expireTime " +
            "AND e.useYn = 0")
    Optional<DoorLockInvite> findByInviteSeq(@Param("inviteSeq") Long inviteSeq, @Param("expireTime") LocalDateTime expireTime);
}
