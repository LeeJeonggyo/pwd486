package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.inu.Capstone.common.entity.KeyCard;

import java.util.Optional;

public interface KeyCardRepository extends JpaRepository<KeyCard, Long> {
    Optional<KeyCard> findByKeyCardDataAndDoorLock_DoorLockSeq(String keyCardData, Long doorLockSeq);
}
