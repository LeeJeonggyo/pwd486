package univ.inu.Capstone.phone.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.dto.apiResponse.ApiResponse;
import univ.inu.Capstone.common.entity.*;
import univ.inu.Capstone.common.repository.*;
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
    private final KeyCardRepository keyCardRepository;
    private final KeyBioRepository keyBioRepository;
    private final OpenLogRepository openLogRepository;

    /**
     * 도어락 비밀번호 변경
     * @param dto SettingsRequestDto.changePw
     * @param userSeq Long
     * @return ApiResponse<?>
     */
    @Transactional
    public ApiResponse<?> changePw (SettingsRequestDto.changePw dto, Long userSeq){
        // 1. 새 비밀번호와 확인 값이 같은지 확인
        if(!dto.getNewSecretNo().equals(dto.getCheckSecretNo()))
            return ApiResponse.FAILURE(400, "새 비밀번호 입력이 잘못되었습니다.");

        // 2. 변경하려는 도어락에 사용자가 owner권한으로 등록되어 있는지 확인
        Optional<RegistDoorLock> registDoorLock = registDoorLockRepository.findByUser_UserSeqAndDoorLock_DoorLockSeq(userSeq, dto.getDoorLockSeq());
        if(registDoorLock.isEmpty() || registDoorLock.get().getRdlAuth() != 1)
            return ApiResponse.FAILURE(401, "권한이 없습니다.");

        // 3. 해당 도어락의 비밀번호가 입력된 현재 비밀번호와 같은지 확인
        Optional<DoorLockSecret> doorLockSecret = doorLockSecretRespository.findByDoorLock_DoorLockSeqAndDlSecretNo(dto.getDoorLockSeq(), dto.getDlSecretNo());
        if(doorLockSecret.isEmpty())
            return ApiResponse.FAILURE(401, "현재 비밀번호를 다시 입력해주십시오.");

        // 4. 1~3까지 문제가 없다면, 새로운 비밀번호로 변경
        doorLockSecret.get().changePw(dto.getNewSecretNo(), userSeq);

        return ApiResponse.SUCCESS("비밀번호가 변경되었습니다.");
    }

    /**
     * 출입로그 조회
     * @param dto SettingsRequestDto.viewLog
     * @return ApiResponse<List<SettingsResponseDto.viewLog>>
     */
    public ApiResponse<List<SettingsResponseDto.viewLog>> viewLog(SettingsRequestDto.viewLog dto, Long userSeq){
        // 1. 요청자가 해당 도어락의 owner 권한을 가진사람이 맞는지 확인
        Optional<RegistDoorLock> registDoorLock = registDoorLockRepository.findByUser_UserSeqAndDoorLock_DoorLockSeq(userSeq, dto.getDoorLockSeq());
        if(registDoorLock.isEmpty() || registDoorLock.get().getRdlAuth() != 1)
            return ApiResponse.ERROR(401, "권한이 없습니다.");

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

        return ApiResponse.SUCCESS("SUCCESS", resultList);
    }

    /**
     * 등록된 nfc, 지문, 카드키 전체 조회
     * @param dto SettingsRequestDto.viewRegistKey
     * @param userSeq Long
     * @return ApiResponse<?>
     */
    public ApiResponse<?> viewRegistKey(SettingsRequestDto.viewRegistKey dto, Long userSeq){
        // 1. rdlSeq 로 등록된 NFC 확인 (해당 정보 없으면 안됨.)
        Optional<RegistDoorLock> rdlOpt = registDoorLockRepository.findById(dto.getRdlSeq());
        if (rdlOpt.isEmpty())
            return ApiResponse.FAILURE(404,"잘못된 정보입니다.");
        RegistDoorLock registDoorLock = rdlOpt.get();

        // 2. 1에서 구한 userSeq 값이 로그인 userSeq 와 같은지 확인 (다르면 안됨.)
        if (!userSeq.equals(registDoorLock.getUser().getUserSeq()))
            return ApiResponse.FAILURE(404,"잘못된 정보입니다.");

        // 3. owner 권한인지 확인
        if (registDoorLock.getRdlAuth() != 1)
            return ApiResponse.FAILURE(401,"조회 권한이 없습니다.");

        // 4. NFC 정보
        List<RegistDoorLock> rdlList = registDoorLockRepository.findByDoorLock_DoorLockSeq(registDoorLock.getDoorLock().getDoorLockSeq());
        List<SettingsResponseDto.viewRegistKeyNfc> rdlListDto = new ArrayList<>();
        for (RegistDoorLock entity : rdlList) {
            rdlListDto.add(SettingsResponseDto.viewRegistKeyNfc.builder()
                            .rdlSeq(entity.getRdlSeq())
                            .rdlName(entity.getRdlName())
                            .rdlAuth(entity.getRdlAuth())
                            .rdlApprove(entity.getRdlApprove())
                            .build());
        }

        // 5. 카드키 정보
        List<KeyCard> keyCardList = keyCardRepository.findByDoorLock_DoorLockSeq(registDoorLock.getDoorLock().getDoorLockSeq());
        List<SettingsResponseDto.viewRegistKeyCard> keyCardListDto = new ArrayList<>();
        for (KeyCard entity : keyCardList) {
            keyCardListDto.add(SettingsResponseDto.viewRegistKeyCard.builder()
                    .keyCardSeq(entity.getKeyCardSeq())
                    .keyCardData(entity.getKeyCardData())
                    .build());
        }

        // 6. 지문 정보
        List<KeyBio> keyBioList = keyBioRepository.findByDoorLock_DoorLockSeq(registDoorLock.getDoorLock().getDoorLockSeq());
        List<SettingsResponseDto.viewRegistKeyBio> keyBioListDto = new ArrayList<>();
        for (KeyBio entity : keyBioList) {
            keyBioListDto.add(SettingsResponseDto.viewRegistKeyBio.builder()
                    .keyBioSeq(entity.getKeyBioSeq())
                    .keyBioData(entity.getKeyBioData())
                    .build());
        }

        return ApiResponse.SUCCESS(
                "조회가 완료되었습니다.",
                SettingsResponseDto.viewRegistKey.builder()
                        .rdlList(rdlListDto)
                        .keyCardList(keyCardListDto)
                        .keyBioList(keyBioListDto)
                        .build());
    }

    /**
     * member & guest 사용허가
     * @param dto SettingsRequestDto.usePermit
     * @param userSeq Long
     * @return ApiResponse<?>
     */
    @Transactional
    public ApiResponse<?> usePermit(SettingsRequestDto.usePermit dto, Long userSeq){
        // 1. 사용을 허가하려는 rdlSeq값 확인
        Optional<RegistDoorLock> permitEntity = registDoorLockRepository.findById(dto.getRdlSeq());
        if (permitEntity.isEmpty()
                || permitEntity.get().getRdlApprove() == 1)
            return ApiResponse.FAILURE(404, "피승인자의 정보가 올바르지 않습니다.");

        // 2. userSeq 와 1에서 구한 도어락 구분자로 요청자가 owner 권한인지 확인
        Optional<RegistDoorLock> userEntity = registDoorLockRepository.findByUser_UserSeqAndDoorLock_DoorLockSeq(userSeq, permitEntity.get().getDoorLock().getDoorLockSeq());
        if (userEntity.isEmpty()
                || userEntity.get().getRdlAuth() != 1)
            return ApiResponse.FAILURE(401, "승인 요청자의 정보가 올바르지 않습니다.");

        // 3. owner 권한이 맞을 경우, 사용 허가처리
        RegistDoorLock entity = permitEntity.get();
        entity.permit();

        return ApiResponse.SUCCESS("SUCCESS");
    }

    /**
     * member & guest 삭제
     * @param dto SettingsRequestDto.delNfcOther
     * @param userSeq Long
     * @return ApiResponse<?>
     */
    @Transactional
    public ApiResponse<?> delNfcOther(SettingsRequestDto.delNfcOther dto, Long userSeq){
        // 1. 삭제하려는 rdlSeq값 확인
        Optional<RegistDoorLock> permitEntity = registDoorLockRepository.findById(dto.getRdlSeq());
        if (permitEntity.isEmpty())
            return ApiResponse.FAILURE(404, "피승인자의 정보가 올바르지 않습니다.");

        // 2. userSeq 와 1에서 구한 도어락 구분자로 요청자가 owner 권한인지 확인
        Optional<RegistDoorLock> userEntity = registDoorLockRepository.findByUser_UserSeqAndDoorLock_DoorLockSeq(userSeq, permitEntity.get().getDoorLock().getDoorLockSeq());
        if (userEntity.isEmpty()
                || userEntity.get().getRdlAuth() != 1)
            return ApiResponse.FAILURE(401, "승인 요청자의 정보가 올바르지 않습니다.");

        // 3. owner 권한이 맞을 경우, 삭제 처리
        RegistDoorLock entity = permitEntity.get();
        registDoorLockRepository.delete(entity);

        return ApiResponse.SUCCESS("SUCCESS");
    }

    /**
     * 카드키 삭제
     * @param dto SettingsRequestDto.delKeyCard
     * @param userSeq Long
     * @return ApiResponse<?>
     */
    @Transactional
    public ApiResponse<?> delKeyCard(SettingsRequestDto.delKeyCard dto, Long userSeq){
        // 1. 삭제하려는 keyCardSeq 값 확인
        Optional<KeyCard> permitEntity = keyCardRepository.findById(dto.getKeyCardSeq());
        if (permitEntity.isEmpty())
            return ApiResponse.FAILURE(404, "삭제하려는 정보가 올바르지 않습니다.");

        // 2. userSeq 와 1에서 구한 도어락 구분자로 요청자가 owner 권한인지 확인
        Optional<RegistDoorLock> userEntity = registDoorLockRepository.findByUser_UserSeqAndDoorLock_DoorLockSeq(userSeq, permitEntity.get().getDoorLock().getDoorLockSeq());
        if (userEntity.isEmpty()
                || userEntity.get().getRdlAuth() != 1)
            return ApiResponse.FAILURE(401, "삭제 요청자의 정보가 올바르지 않습니다.");

        // 3. owner 권한이 맞을 경우, 라즈베리와 통신해서 도어락기기에서 정보 삭제 요청

        // 4. DB 에서 카드 정보 삭제
        KeyCard entity = permitEntity.get();
        keyCardRepository.delete(entity);

        return ApiResponse.SUCCESS("삭제되었습니다.");
    }

    /**
     * 지문 삭제
     * @param dto SettingsRequestDto.delKeyBio
     * @param userSeq Long
     * @return ApiResponse<?>
     */
    @Transactional
    public ApiResponse<?> delKeyBio(SettingsRequestDto.delKeyBio dto, Long userSeq){
        // 1. 삭제하려는 keyCardSeq 값 확인
        Optional<KeyBio> permitEntity = keyBioRepository.findById(dto.getKeyBioSeq());
        if (permitEntity.isEmpty())
            return ApiResponse.FAILURE(404, "삭제하려는 정보가 올바르지 않습니다.");

        // 2. userSeq 와 1에서 구한 도어락 구분자로 요청자가 owner 권한인지 확인
        Optional<RegistDoorLock> userEntity = registDoorLockRepository.findByUser_UserSeqAndDoorLock_DoorLockSeq(userSeq, permitEntity.get().getDoorLock().getDoorLockSeq());
        if (userEntity.isEmpty()
                || userEntity.get().getRdlAuth() != 1)
            return ApiResponse.FAILURE(401, "삭제 요청자의 정보가 올바르지 않습니다.");

        // 3. owner 권한이 맞을 경우, 라즈베리와 통신해서 도어락기기에서 정보 삭제 요청

        // 4. DB 에서 카드 정보 삭제
        KeyBio entity = permitEntity.get();
        keyBioRepository.delete(entity);

        return ApiResponse.SUCCESS("삭제되었습니다.");
    }

    /**
     * owner 권한 양도
     * @param dto SettingsRequestDto.tossOwnerAuth
     * @param userSeq Long
     * @return ApiResponse<?>
     */
    @Transactional
    public ApiResponse<?> tossOwnerAuth(SettingsRequestDto.tossOwnerAuth dto, Long userSeq){
        // 1. 양도받는 사용자 rdlSeq값 확인
        Optional<RegistDoorLock> otherOpt = registDoorLockRepository.findById(dto.getRdlSeq());
        if (otherOpt.isEmpty()
                || otherOpt.get().getRdlApprove() == 0)
            return ApiResponse.FAILURE(404, "피승인자의 정보가 올바르지 않습니다.");

        // 2. userSeq 와 1에서 구한 도어락 구분자로 요청자가 owner 권한인지 확인
        Optional<RegistDoorLock> userOpt = registDoorLockRepository.findByUser_UserSeqAndDoorLock_DoorLockSeq(userSeq, otherOpt.get().getDoorLock().getDoorLockSeq());
        if (userOpt.isEmpty()
                || userOpt.get().getRdlAuth() != 1)
            return ApiResponse.FAILURE(401, "승인 요청자의 정보가 올바르지 않습니다.");

        // 3. owner 권한 양도
        RegistDoorLock otherEntity = otherOpt.get();
        RegistDoorLock userEntity = userOpt.get();

        userEntity.changeAuth(otherEntity.getRdlAuth());
        otherEntity.changeAuth(1);

        return ApiResponse.SUCCESS("SUCCESS");
    }

}
