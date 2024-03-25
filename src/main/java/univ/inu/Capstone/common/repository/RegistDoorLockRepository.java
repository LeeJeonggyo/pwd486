package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.inu.Capstone.common.entity.RegistDoorLock;

import java.util.List;
import java.util.Optional;

public interface RegistDoorLockRepository extends JpaRepository<RegistDoorLock, Long> {
    Optional<List<RegistDoorLock>> findByDoorLockSeq(Long doorLockSeq);
}
