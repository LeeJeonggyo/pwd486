package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.inu.Capstone.common.entity.RegistDoorLock;

import java.util.List;
import java.util.Optional;

public interface RegistDoorLockRepository extends JpaRepository<RegistDoorLock, Long> {
    Optional<RegistDoorLock> findByUser_UserSeqAndDoorLock_DoorLockSeq(Long userSeq, Long doorLockSeq);
    List<RegistDoorLock> findByDoorLock_DoorLockSeq(Long doorLockSeq);
}
