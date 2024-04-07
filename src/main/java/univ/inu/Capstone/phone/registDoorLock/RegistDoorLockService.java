package univ.inu.Capstone.phone.registDoorLock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.dto.doorlock.DoorLockRequestDto;
import univ.inu.Capstone.common.dto.doorlock.DoorLockResponseDto;
import univ.inu.Capstone.common.dto.registDoorlock.RegistDLRequestDto;
import univ.inu.Capstone.common.dto.registDoorlock.RegistDLResponseDto;
import univ.inu.Capstone.common.entity.*;
import univ.inu.Capstone.common.repository.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistDoorLockService {

    private final UserRepository userRepository;
    private final DoorLockRepository doorLockRepository;
    private final RegistDoorLockRepository registDoorLockRepository;
    private final DoorLockSecretRespository doorLockSecretRespository;
    private final DoorLockInviteRepository doorLockInviteRepository;

    /**
     * 도어락 기기 정보 등록
     * @param dto DoorLockRequestDto.DoorLockBasic
     * @return DoorLockResponseDto.DoorLockResult
     */
    public DoorLockResponseDto.SaveMachine registMachine(DoorLockRequestDto.DoorLockBasic dto){
        String result = "SUCCESS";
        Optional<DoorLock> data = doorLockRepository.findBySerialNo(dto.getSerialNo());

        if (data.isPresent()) result = "FAIL";
        else {
            DoorLock doorLock = doorLockRepository.save(dto.toEntity());

            // 비밀번호 low 생성
            DoorLockSecret doorLockSecret = DoorLockSecret.builder()
                    .dlSecretNo("0000")
                    .doorLock(doorLock)
                    .build();
            doorLockSecretRespository.save(doorLockSecret);
        }

        return DoorLockResponseDto.SaveMachine.builder()
                .result(result)
                .build();
    }

    /**
     * 시리얼 넘버를 통한 도어락 검색
     * @param serialNo String
     * @return DoorLockResponseDto.SearchSerialNo
     */
    public DoorLockResponseDto.SearchSerialNo searchSerialNo(String serialNo){
        // 시리얼 넘버로 등록된 도어락 정보 확인
        Optional<DoorLock> data = doorLockRepository.findBySerialNo(serialNo);
        return DoorLockResponseDto.SearchSerialNo.builder()
                .doorLockSeq(data.map(DoorLock::getDoorLockSeq).orElse(null))
                .result(data.isPresent() ? "SUCCESS":"FAIL")
                .build();
    }

    /**
     * 사용자 도어락 NFC 등록 (owner)
     * @param dto RegistDLRequestDto.RegistDL
     * @param userSeq Long
     * @return RegistDLResponseDto.RegistDL
     */
    @Transactional
    public RegistDLResponseDto.RegistDL registDL(RegistDLRequestDto.RegistDL dto, Long userSeq){
        // 1. 넘어온 userSeq 데이터가 있는지 확인
        Optional<User> user = userRepository.findById(userSeq);
        if (user.isEmpty()) throw new RuntimeException("알 수 없는 사용자입니다.");

        // 2. 넘어온 도어락 구분자값으로 등록된 도어락 정보가 있는지 확인
        Optional<DoorLock> doorLock = doorLockRepository.findById(dto.getDoorLockSeq());
        if (doorLock.isEmpty()) throw new RuntimeException("알 수 없는 도어락입니다.");

        // 3. 넘어온 도어락 구분자값으로 등록된 사용자가 있는지 확인 (사용자가 없어야함.)
        List<RegistDoorLock> data = registDoorLockRepository.findByDoorLock_DoorLockSeq(dto.getDoorLockSeq());

        // 4. 사용자가 있을 경우, OWNER 로 등록 불가
        if (!data.isEmpty()) throw new RuntimeException("OWNER 로 이미 등록된 사용자가 있습니다.");

        // 5. 사용자가 없을 경우, OWNER 권한
        RegistDoorLock registDoorLock = RegistDoorLock.builder()
                .user(user.get())
                .doorLock(doorLock.get())
                .rdlName(dto.getRdlName())
                .rdlAuth(1)
                .rdlApprove(0)
                .build();
        registDoorLockRepository.save(registDoorLock);

        return RegistDLResponseDto.RegistDL.builder()
                .serialNo(registDoorLock.getDoorLock().getSerialNo())
                .rdlName(registDoorLock.getRdlName())
                .build();
    }

    /**
     * owner 권한 이외, NFC 등록 API
     * @param dto RegistDLRequestDto.registNfcOther
     * @param userSeq Long
     * @return RegistDLResponseDto.registNfcOther
     */
    public RegistDLResponseDto.registNfcOther registNfcOther(RegistDLRequestDto.registNfcOther dto, Long userSeq){
        // 1. 넘어온 userSeq 데이터가 있는지 확인
        Optional<User> user = userRepository.findById(userSeq);
        if (user.isEmpty()) throw new RuntimeException("알 수 없는 사용자입니다.");

        // 2. 넘어온 도어락 구분자값으로 등록된 도어락 정보가 있는지 확인
        Optional<DoorLock> doorLock = doorLockRepository.findById(dto.getDoorLockSeq());
        if (doorLock.isEmpty()) throw new RuntimeException("알 수 없는 도어락입니다.");

        // 3. 초대 코드 조회를 통해 부여할 권한 확인
        Optional<DoorLockInvite> inviteCode = doorLockInviteRepository.findById(dto.getInviteSeq());
        if (inviteCode.isEmpty()) throw new RuntimeException("잘못된 초대코드 입니다.");

        // 4. 넘어온 도어락 구분자값으로 등록된 사용자가 있는지 확인(사용자가 없다는 건 OWNER 권한자가 없다는 의미가 됨.)
        List<RegistDoorLock> data = registDoorLockRepository.findByDoorLock_DoorLockSeq(dto.getDoorLockSeq());
        if (data.isEmpty()) throw new RuntimeException("OWNER 로 등록된 사용자가 없습니다.");

        // 5. 넘어온 도어락 구분자 및 사용자 구분자 코드로 등록된 데이터가 있는지 확인
        if(registDoorLockRepository.findByUser_UserSeqAndDoorLock_DoorLockSeq(userSeq, dto.getDoorLockSeq()).isPresent())
            throw new RuntimeException("이미 등록된 사용자입니다.");

        // 6. 초대 코드에 맞는 권한으로 NFC 데이터 저장
        RegistDoorLock registDoorLock = RegistDoorLock.builder()
                .user(user.get())
                .doorLock(doorLock.get())
                .rdlName(dto.getRdlName())
                .rdlAuth(inviteCode.get().getRdlAuth())
                .rdlApprove(1)
                .build();
        registDoorLockRepository.save(registDoorLock);

        return RegistDLResponseDto.registNfcOther.builder()
                .serialNo(registDoorLock.getDoorLock().getSerialNo())
                .rdlName(registDoorLock.getRdlName())
                .build();
    }
}
