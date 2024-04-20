package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import univ.inu.Capstone.common.entity.RegistDoorLock;

import java.util.List;
import java.util.Optional;

public interface RegistDoorLockRepository extends JpaRepository<RegistDoorLock, Long> {
    Optional<RegistDoorLock> findByUser_UserSeqAndDoorLock_DoorLockSeq(Long userSeq, Long doorLockSeq);
    List<RegistDoorLock> findByDoorLock_DoorLockSeq(Long doorLockSeq);
    List<RegistDoorLock> findByRdlAuthAndDoorLock_DoorLockSeq(int rdlAuth, Long doorLockSeq);
    Optional<RegistDoorLock> findByRdlAuthAndDoorLock_DoorLockSeqAndUser_UserSeq(int rdlAuth, Long doorLockSeq, Long userSeq);
    Optional<RegistDoorLock> findByNfcDataAndDoorLock_DoorLockSeq(String nfcData, Long doorLockSeq);
    Optional<RegistDoorLock> findByRdlApproveAndNfcDataAndDoorLock_DoorLockSeq(int rdlApprove, String nfcData, Long doorLockSeq);
}
