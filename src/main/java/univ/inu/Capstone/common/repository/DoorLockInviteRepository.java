package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.inu.Capstone.common.entity.DoorLockInvite;

import java.util.Optional;

public interface DoorLockInviteRepository extends JpaRepository<DoorLockInvite, Long> {
    Optional<DoorLockInvite> findByInviteCode(String inviteCode);
}
