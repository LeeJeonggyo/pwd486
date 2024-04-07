package univ.inu.Capstone.phone.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.entity.DoorLockSecret;
import univ.inu.Capstone.common.entity.OpenLog;
import univ.inu.Capstone.common.entity.RegistDoorLock;
import univ.inu.Capstone.common.repository.DoorLockSecretRespository;
import univ.inu.Capstone.common.repository.OpenLogRepository;
import univ.inu.Capstone.common.repository.RegistDoorLockRepository;
import univ.inu.Capstone.phone.settings.dto.SettingsRequestDto;
import univ.inu.Capstone.phone.settings.dto.SettingsResponseDto;

import javax.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final RegistDoorLockRepository registDoorLockRepository;
    private final DoorLockSecretRespository doorLockSecretRespository;
    private final OpenLogRepository openLogRepository;

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

    /**
     * 출입로그 조회
     * @param dto SettingsRequestDto.viewLog
     * @return Map<String, Object>
     */
    public Map<String, Object> viewLog(SettingsRequestDto.viewLog dto, Long userSeq){
        Map<String, Object> result = new HashMap<>();

        // 1. 요청자가 해당 도어락의 owner 권한을 가진사람이 맞는지 확인
        Optional<RegistDoorLock> registDoorLock = registDoorLockRepository.findByUser_UserSeqAndDoorLock_DoorLockSeq(userSeq, dto.getDoorLockSeq());
        if(registDoorLock.isEmpty() || registDoorLock.get().getRdlAuth() != 1) throw new RuntimeException("권한이 없습니다.");

        // 2. 도어락 구분자를 사용해서 출입 로그 가져오기 & dto 변환
        List<OpenLog> openLogList = openLogRepository.findByDoorLock_DoorLockSeq(dto.getDoorLockSeq());
        List<SettingsResponseDto.viewLog> resultList = new ArrayList<>();
        for (OpenLog entity : openLogList) {
            SettingsResponseDto.viewLog data = SettingsResponseDto.viewLog.builder()
                    .nickname(entity.getNickname())
                    .inpDate(entity.getInpDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .inpTime(entity.getInpDate().format(DateTimeFormatter.ofPattern("HH시 mm분 ss.SSS초")))
                    .build();
            data.setOpenMethod(entity.getOpenMethod());
            resultList.add(data);
        }

        result.put("state", 201);
        result.put("result", "SUCCESS");
        result.put("data", resultList);

        return result;
    }

    /**
     * member & guest 사용허가
     * @param dto SettingsRequestDto.usePermit
     * @param userSeq Long
     * @return SettingsResponseDto.usePermit
     */
    @Transactional
    public SettingsResponseDto.usePermit usePermit(SettingsRequestDto.usePermit dto, Long userSeq){
        // 1. 사용을 허가하려는 rdlSeq값 확인
        Optional<RegistDoorLock> permitEntity = registDoorLockRepository.findById(dto.getRdlSeq());
        if (permitEntity.isEmpty()
                || permitEntity.get().getRdlApprove() == 1)
            return SettingsResponseDto.usePermit.builder()
                    .state(404)
                    .result("피승인자의 정보가 올바르지 않습니다.")
                    .build();

        // 2. userSeq 와 1에서 구한 도어락 구분자로 요청자가 owner 권한인지 확인
        Optional<RegistDoorLock> userEntity = registDoorLockRepository.findByUser_UserSeqAndDoorLock_DoorLockSeq(userSeq, permitEntity.get().getDoorLock().getDoorLockSeq());
        if (userEntity.isEmpty()
                || userEntity.get().getRdlAuth() != 1)
            return SettingsResponseDto.usePermit.builder()
                    .state(404)
                    .result("승인 요청자의 정보가 올바르지 않습니다.")
                    .build();

        // 3. owner 권한이 맞을 경우, 사용 허가처리
        RegistDoorLock entity = permitEntity.get();
        entity.permit();

        return SettingsResponseDto.usePermit.builder()
                .state(201)
                .result("SUCCESS")
                .build();
    }

    /**
     * member & guest 삭제
     * @param dto SettingsRequestDto.delNfcOther
     * @param userSeq Long
     * @return SettingsResponseDto.delNfcOther
     */
    @Transactional
    public SettingsResponseDto.delNfcOther delNfcOther(SettingsRequestDto.delNfcOther dto, Long userSeq){
        // 1. 삭제하려는 rdlSeq값 확인
        Optional<RegistDoorLock> permitEntity = registDoorLockRepository.findById(dto.getRdlSeq());
        if (permitEntity.isEmpty())
            return SettingsResponseDto.delNfcOther.builder()
                    .state(404)
                    .result("존재하지 않는 사용자입니다.")
                    .build();

        // 2. userSeq 와 1에서 구한 도어락 구분자로 요청자가 owner 권한인지 확인
        Optional<RegistDoorLock> userEntity = registDoorLockRepository.findByUser_UserSeqAndDoorLock_DoorLockSeq(userSeq, permitEntity.get().getDoorLock().getDoorLockSeq());
        if (userEntity.isEmpty()
                || userEntity.get().getRdlAuth() != 1)
            return SettingsResponseDto.delNfcOther.builder()
                    .state(404)
                    .result("승인 요청자의 정보가 올바르지 않습니다.")
                    .build();

        // 3. owner 권한이 맞을 경우, 사용 허가처리
        RegistDoorLock entity = permitEntity.get();
        registDoorLockRepository.delete(entity);

        return SettingsResponseDto.delNfcOther.builder()
                .state(201)
                .result("SUCCESS")
                .build();
    }

}
