package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.inu.Capstone.common.entity.KeyBio;

import java.util.Optional;

public interface KeyBioRepository extends JpaRepository<KeyBio, Long> {

    Optional<KeyBio> findByKeyBioDataAndDoorLock_DoorLockSeq(String keyBioData, Long doorLockSeq);
}
