package univ.inu.Capstone.phone.registDoorLock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.dto.doorlock.DoorLockRequestDto;
import univ.inu.Capstone.common.dto.doorlock.DoorLockResponseDto;
import univ.inu.Capstone.common.dto.registDoorlock.RegistDLRequestDto;
import univ.inu.Capstone.common.dto.registDoorlock.RegistDLResponseDto;
import univ.inu.Capstone.common.entity.DoorLock;
import univ.inu.Capstone.common.entity.DoorLockSecret;
import univ.inu.Capstone.common.entity.RegistDoorLock;
import univ.inu.Capstone.common.entity.User;
import univ.inu.Capstone.common.repository.DoorLockRepository;
import univ.inu.Capstone.common.repository.DoorLockSecretRespository;
import univ.inu.Capstone.common.repository.RegistDoorLockRepository;
import univ.inu.Capstone.common.repository.UserRepository;

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

    /**
     * 도어락 기기 정보 등록
     * @param dto DoorLockRequestDto.DoorLockBasic
     * @return DoorLockResponseDto.DoorLockResult
     */
    public DoorLockResponseDto.SaveMachine registMachine(DoorLockRequestDto.DoorLockBasic dto){
        String result = "SUCCESS";
        Optional<DoorLock> data = doorLockRepository.findBySerialNo(dto.getSerialNo());

        if (data.isPresent()) result = "FAIL";
        else doorLockRepository.save(dto.toEntity());

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
     * 사용자 도어락 키(핸드폰) 등록
     * @param dto RegistDLRequestDto.RegistDL
     * @param userSeq Long
     * @return RegistDLResponseDto.RegistDL
     */
    @Transactional
    public RegistDLResponseDto.RegistDL registDL(RegistDLRequestDto.RegistDL dto, Long userSeq){
        // 1. 넘어온 userSeq 데이터가 있는지 확인
        Optional<User> user = userRepository.findById(userSeq);
        if (user.isEmpty()) throw new RuntimeException();

        // 2. 넘어온 도어락 구분자값으로 등록된 도어락 정보가 있는지 확인
        Optional<DoorLock> doorLock = doorLockRepository.findById(dto.getDoorLockSeq());
        if (doorLock.isEmpty()) throw new RuntimeException();

        // 3. 넘어온 도어락 구분자값으로 등록된 사용자가 있는지 확인
        List<RegistDoorLock> data = registDoorLockRepository.findByDoorLock_DoorLockSeq(dto.getDoorLockSeq());

        // 4. 넘어온 도어락 구분자 및 사용자 구분자 코드로 등록된 데이터가 있는지 확인
        if(registDoorLockRepository.findByUser_UserSeqAndDoorLock_DoorLockSeq(userSeq, dto.getDoorLockSeq()).isPresent())
            throw new RuntimeException();

        // 5. 사용자가 없을 경우, OWNER 권한 / 있을 경우, MEMBER 권한 으로 NFC 데이터 저장
        RegistDoorLock registDoorLock = RegistDoorLock.builder()
                .user(user.get())
                .doorLock(doorLock.get())
                .rdlName(dto.getRdlName())
                .rdlAuth(data.isEmpty() ? 1 : 2)
                .rdlApprove(data.isEmpty() ? 0 : 1)
                .build();
        registDoorLockRepository.save(registDoorLock);

        // 6. owner 권한으로 등록 한 경우, 비밀번호 로우 생성
        if(registDoorLock.getRdlAuth() == 1){
            DoorLockSecret doorLockSecret = DoorLockSecret.builder()
                    .dlSecretNo("0000")
                    .user(user.get())
                    .doorLock(doorLock.get())
                    .build();
            doorLockSecretRespository.save(doorLockSecret);
        }

        return RegistDLResponseDto.RegistDL.builder()
                .serialNo(registDoorLock.getDoorLock().getSerialNo())
                .rdlName(registDoorLock.getRdlName())
                .build();
    }
}
