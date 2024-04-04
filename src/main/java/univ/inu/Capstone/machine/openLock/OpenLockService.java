package univ.inu.Capstone.machine.openLock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.entity.DoorLock;
import univ.inu.Capstone.common.entity.OpenLog;
import univ.inu.Capstone.common.entity.RegistDoorLock;
import univ.inu.Capstone.common.repository.DoorLockRepository;
import univ.inu.Capstone.common.repository.OpenLogRepository;
import univ.inu.Capstone.common.repository.RegistDoorLockRepository;
import univ.inu.Capstone.machine.openLock.dto.OpenLockRequestDto;
import univ.inu.Capstone.machine.openLock.dto.OpenLockResponseDto;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OpenLockService {

    private final DoorLockRepository doorLockRepository;
    private final RegistDoorLockRepository registDoorLockRepository;
    private final OpenLogRepository openLogRepository;

    public OpenLockResponseDto.openLockByNfc openLockByNfc(OpenLockRequestDto.openLockByNfc dto) {
        // 1. 도어락 시리얼 넘버로 해당 도어락 존재 확인
        Optional<DoorLock> doorLock = doorLockRepository.findBySerialNo(dto.getSerialNo());
        if (doorLock.isEmpty()) return new OpenLockResponseDto.openLockByNfc(400, "존재하지 않는 도어락입니다.");

        // 2. 해당 NFC 정보 조회
        Optional<RegistDoorLock> nfcData = registDoorLockRepository.findById(dto.getRdlSeq());

        // 3. 로그 데이터 생성 및 return
        if(nfcData.isEmpty()){
            openLogRepository.save(
                    OpenLog.builder()
                            .openMethod(0L)
                            .nickname("알수 없음")
                            .doorLock(doorLock.get())
                            .build());
            return new OpenLockResponseDto.openLockByNfc(400, "접근 권한이 없습니다.");
        } else {
            openLogRepository.save(
                    OpenLog.builder()
                            .openMethod(1L)
                            .nickname(nfcData.get().getUser().getNickname())
                            .doorLock(doorLock.get())
                            .build());
            return new OpenLockResponseDto.openLockByNfc(201, "인증되었습니다.");
        }
    }
}
