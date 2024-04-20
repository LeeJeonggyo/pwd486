package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.inu.Capstone.common.entity.DoorLock;

import java.util.Optional;

public interface DoorLockRepository extends JpaRepository<DoorLock, Long> {
    Optional<DoorLock> findBySerialNo(String serialNo);
    Optional<DoorLock> findByBtSerialNo(String btSerialNo);

}
