package univ.inu.Capstone.phone.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.entity.DoorLockSecret;
import univ.inu.Capstone.common.entity.RegistDoorLock;
import univ.inu.Capstone.common.repository.DoorLockSecretRespository;
import univ.inu.Capstone.common.repository.RegistDoorLockRepository;
import univ.inu.Capstone.phone.settings.dto.SettingsRequestDto;
import univ.inu.Capstone.phone.settings.dto.SettingsResponseDto;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final RegistDoorLockRepository registDoorLockRepository;
    private final DoorLockSecretRespository doorLockSecretRespository;

    /**
     * 도어락 비밀번호 변경
     * @param dto SettingsRequestDto.changePw
     * @param userSeq Long
     * @return SettingsResponseDto.changePw
     */
    @Transactional
    public SettingsResponseDto.changePw changePw (SettingsRequestDto.changePw dto, Long userSeq){
        // 1. 새 비밀번호와 확인 값이 같은지 확인
        if(!dto.getNewSecretNo().equals(dto.getCheckSecretNo()))  throw new RuntimeException("새 비밀번호 입력이 잘못되었습니다.");

        // 2. 변경하려는 도어락에 사용자가 owner권한으로 등록되어 있는지 확인
        Optional<RegistDoorLock> registDoorLock = registDoorLockRepository.findByUser_UserSeqAndDoorLock_DoorLockSeq(userSeq, dto.getDoorLockSeq());
        if(registDoorLock.isEmpty() || registDoorLock.get().getRdlAuth() != 1) throw new RuntimeException("권한이 없습니다.");

        // 3. 해당 도어락의 비밀번호가 입력된 현재 비밀번호와 같은지 확인
        Optional<DoorLockSecret> doorLockSecret = doorLockSecretRespository.findByDoorLock_DoorLockSeqAndDlSecretNo(dto.getDoorLockSeq(), dto.getDlSecretNo());
        if(doorLockSecret.isEmpty()) throw new RuntimeException("현재 비밀번호를 다시 입력해주십시오.");

        // 4. 1~3까지 문제가 없다면, 새로운 비밀번호로 변경
        doorLockSecret.get().changePw(dto.getNewSecretNo(), userSeq);

        return SettingsResponseDto.changePw.builder()
                .result("SUCCESS")
                .build();
    }

}
