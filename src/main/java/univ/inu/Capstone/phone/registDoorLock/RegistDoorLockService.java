package univ.inu.Capstone.phone.registDoorLock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.dto.doorlock.DoorLockRequestDto;
import univ.inu.Capstone.common.dto.doorlock.DoorLockResponseDto;
import univ.inu.Capstone.common.dto.registDoorlock.RegistDLRequestDto;
import univ.inu.Capstone.common.dto.registDoorlock.RegistDLResponseDto;
import univ.inu.Capstone.common.entity.DoorLock;
import univ.inu.Capstone.common.entity.RegistDoorLock;
import univ.inu.Capstone.common.entity.User;
import univ.inu.Capstone.common.repository.DoorLockRepository;
import univ.inu.Capstone.common.repository.RegistDoorLockRepository;
import univ.inu.Capstone.common.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistDoorLockService {

    private final DoorLockRepository doorLockRepository;
    private final RegistDoorLockRepository registDoorLockRepository;

    /**
     * 도어락 기기 정보 등록
     * @param dto DoorLockRequestDto.DoorLockBasic
     * @return DoorLockResponseDto.DoorLockResult
     */
    public DoorLockResponseDto.SaveMachine saveMachine(DoorLockRequestDto.DoorLockBasic dto){
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
    public RegistDLResponseDto.RegistDL registDL(RegistDLRequestDto.RegistDL dto, Long userSeq){
        // 1. 넘어온 도어락 구분자값으로 등록된 도어락 정보가 있는지 확인
        Optional<DoorLock> doorLock = doorLockRepository.findById(dto.getDoorLockSeq());
        if (doorLock.isEmpty()) throw new RuntimeException();

        // 2. 넘어온 도어락 구분자값으로 등록된 사용자가 있는지 확인
        Optional<List<RegistDoorLock>> data = registDoorLockRepository.findByDoorLockSeq(dto.getDoorLockSeq());

        // 3. 사용자가 없을 경우, OWNER 권한 / 있을 경우, MEMBER 권한
        RegistDoorLock registDoorLock = RegistDoorLock.builder()
                .userSeq(userSeq)
                .rdlName(dto.getRdlName())
                .rdlAuth(data.isEmpty() ? 1 : 2)
                .rdlApprove(data.isEmpty() ? 0 : 1)
                .doorLock(doorLock.get())
                .build();

        // 4. 데이터 저장 & return
        registDoorLockRepository.save(registDoorLock);
        return RegistDLResponseDto.RegistDL.builder()
                .serialNo(registDoorLock.getDoorLock().getSerialNo())
                .rdlName(registDoorLock.getRdlName())
                .build();
    }
}
