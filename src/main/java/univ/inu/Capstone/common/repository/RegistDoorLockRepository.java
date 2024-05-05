package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import univ.inu.Capstone.common.entity.RegistDoorLock;

import java.util.List;
import java.util.Optional;

public interface RegistDoorLockRepository extends JpaRepository<RegistDoorLock, Long> {
    List<RegistDoorLock> findByUser_UserSeq(Long userSeq);
    Optional<RegistDoorLock> findByUser_UserSeqAndDoorLock_DoorLockSeq(Long userSeq, Long doorLockSeq);
    List<RegistDoorLock> findByDoorLock_DoorLockSeq(Long doorLockSeq);
    List<RegistDoorLock> findByRdlAuthAndDoorLock_DoorLockSeq(int rdlAuth, Long doorLockSeq);
    Optional<RegistDoorLock> findByRdlAuthAndDoorLock_DoorLockSeqAndUser_UserSeq(int rdlAuth, Long doorLockSeq, Long userSeq);
    Optional<RegistDoorLock> findByNfcDataAndDoorLock_DoorLockSeq(String nfcData, Long doorLockSeq);
    Optional<RegistDoorLock> findByRdlApproveAndNfcDataAndDoorLock_DoorLockSeq(int rdlApprove, String nfcData, Long doorLockSeq);

    @Query(value = "SELECT * " +
            "FROM regist_door_lock " +
            "WHERE door_lock_seq = :doorLockSeq " +
            "AND rdl_auth != 1 " +
            "ORDER BY rdl_approve, rdl_auth, rdl_seq ASC", nativeQuery = true)
    List<RegistDoorLock> findNoOwnerListByDoorLockSeqOrder(@Param("doorLockSeq") Long doorLockSeq);

    @Query(value = "SELECT * " +
            "FROM regist_door_lock " +
            "WHERE door_lock_seq = :doorLockSeq " +
            "AND rdl_approve = :rdlApprove " +
            "AND rdl_auth != 1 " +
            "ORDER BY rdl_approve, rdl_auth, rdl_seq ASC", nativeQuery = true)
    List<RegistDoorLock> findNoOwnerListByRdlApproveAndDoorLock_DoorLockSeq(@Param("rdlApprove") int rdlApprove, @Param("doorLockSeq") Long doorLockSeq);
}
