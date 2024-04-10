package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.inu.Capstone.common.entity.KeyCard;

import java.util.List;
import java.util.Optional;

public interface KeyCardRepository extends JpaRepository<KeyCard, Long> {
    Optional<KeyCard> findByKeyCardDataAndDoorLock_DoorLockSeq(String keyCardData, Long doorLockSeq);
    List<KeyCard> findByDoorLock_DoorLockSeq(Long doorLockSeq);
}
