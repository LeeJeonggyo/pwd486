package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.inu.Capstone.common.entity.KeyBio;

import java.util.List;
import java.util.Optional;

public interface KeyBioRepository extends JpaRepository<KeyBio, Long> {

    Optional<KeyBio> findByKeyBioDataAndDoorLock_DoorLockSeq(int keyBioData, Long doorLockSeq);
    List<KeyBio> findByDoorLock_DoorLockSeq(Long doorLockSeq);
}
