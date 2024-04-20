package univ.inu.Capstone.machine.openLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import univ.inu.Capstone.common.dto.apiResponse.ApiResponse;
import univ.inu.Capstone.common.entity.*;
import univ.inu.Capstone.common.repository.*;
import univ.inu.Capstone.machine.openLock.dto.OpenLockRequestDto;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OpenLockService {

    private final DoorLockRepository doorLockRepository;
    private final DoorLockSecretRespository doorLockSecretRespository;
    private final RegistDoorLockRepository registDoorLockRepository;
    private final OpenLogRepository openLogRepository;
    private final KeyCardRepository keyCardRepository;
    private final KeyBioRepository keyBioRepository;

    /**
     * 비밀번호 해제
     * @param dto OpenLockRequestDto.openBySecretNo
     * @return ApiResponse<?>
     */
    @Transactional
    public ApiResponse<?> openBySecretNo(OpenLockRequestDto.openBySecretNo dto){
        // 1. serialNo를 사용해서 도어락 조회 (없으면 안됨.)
        DoorLock doorLock = findDoorLock(dto.getSerialNo());
        if (doorLock == null) return ApiResponse.ERROR(404, "등록되지 않은 도어락입니다.");

        // 2. 비밀번호 틀린 횟수 확인 (5번 부터 안됨.)
        if (doorLock.getFailCntSecretNo() >= 5 ) return ApiResponse.FAILURE(401, "5회 이상 비밀번호를 잘못 입력하였습니다.");

        // 3. secretNo와 비밀번호를 비교
        Optional<DoorLockSecret> doorLockSecretOpt = doorLockSecretRespository.findByDoorLock_DoorLockSeqAndDlSecretNo(doorLock.getDoorLockSeq(), dto.getSecretNo());

        // 4. 비밀번호 해제 결과에 대한 핸드폰 알림 전송
        if (doorLockSecretOpt.isPresent()) { // 4-1. 해제 성공
            // 4-1-1. 틀린 횟수 0으로 초기화
            doorLock.openSecretNo(1);
            // 4-1-2. 비밀번호 해제 성공 알림 전송
            sendNotification(doorLock.getDoorLockSeq(), "[SUCCESS] 문 열림", "비밀번호가 사용되었습니다.");
            // 4-1-3. 비밀번호 해제 로그 생성
            saveOpenLog(1, 1L, doorLock, "");
            // 4-1-4. return
            return ApiResponse.SUCCESS("인증되었습니다.");
        } else { // 4-2. 해제 실패
            // 4-1-1. 틀린 횟수 +1
            doorLock.openSecretNo(0);
            // 4-1-2. 비밀번호 해제 실패 알림 전송
            sendNotification(doorLock.getDoorLockSeq(), "[FAIL] 문 열림 실패", "비밀번호가 사용되었습니다.");
            // 4-1-3. 비밀번호 해제 실패 로그 생성
            saveOpenLog(0, 1L, doorLock, "???");
            // 4-1-4. return
            return ApiResponse.FAILURE(400, "비밀번호 입력이 잘못되었습니다.");
        }
    }

    /**
     * RFID & NFC 해제
     * @param dto OpenLockRequestDto.openByRfidAndNfc
     * @return ApiResponse<?>
     */
    public ApiResponse<?> openByRfidAndNfc(OpenLockRequestDto.openByRfidAndNfc dto) {
        // 1. serialNo를 사용해서 도어락 조회 (없으면 안됨.)
        DoorLock doorLock = findDoorLock(dto.getSerialNo());
        if (doorLock == null) return ApiResponse.ERROR(404, "등록되지 않은 도어락입니다.");

        // 2. 해당 도어락에 등록된 keyCardData 가 nfc 로 등록된 것인지 확인
        Optional<RegistDoorLock> nfcOpt = registDoorLockRepository
                .findByRdlApproveAndNfcDataAndDoorLock_DoorLockSeq(1, dto.getKeyCardData(), doorLock.getDoorLockSeq());

        // 3. failCntTag 값 확인 (5이상인지 확인 / 5이상일 경우, nfc owner 권한만 오픈 가능)
        if (doorLock.getFailCntTag() >= 5
                && (nfcOpt.isEmpty() || nfcOpt.get().getRdlAuth() != 1))
            return ApiResponse.FAILURE(401, "owner 권한 이외의 태깅이 5회 이상 잘못 되었습니다.");

        // 4. nfc 로 등록된 사용자인 경우, 문 열림 알림 전송
        if (nfcOpt.isPresent()) {
            RegistDoorLock nfcEntity = nfcOpt.get();
            // 4-1-1. 틀린 횟수 0으로 초기화
            doorLock.openTag(1);
            // 4-1-2. 비밀번호 해제 성공 알림 전송
            sendNotification(doorLock.getDoorLockSeq(), "[SUCCESS] 문 열림", nfcEntity.getRdlName()+"님께서 문을 열었습니다.");
            // 4-1-3. 비밀번호 해제 로그 생성
            saveOpenLog(1, 2L, doorLock, nfcEntity.getRdlName()+"(핸드폰)");
            // 4-1-4. return
            return ApiResponse.SUCCESS("인증되었습니다.");
        }

        // 5. nfc 데이터가 아닌 경우, 키카드로 등록된 데이터인지 확인
        Optional<KeyCard> keyCardOpt = keyCardRepository
                .findByKeyCardDataAndDoorLock_DoorLockSeq(dto.getKeyCardData(), doorLock.getDoorLockSeq());

        // 6. 해제 결과에 대한 핸드폰 알림 전송
        if (keyCardOpt.isPresent()) { // 6-1. 해제 성공
            KeyCard keyCardEntity = keyCardOpt.get();
            // 6-1-1. 틀린 횟수 0으로 초기화
            doorLock.openTag(1);
            // 6-1-2. 비밀번호 해제 성공 알림 전송
            sendNotification(doorLock.getDoorLockSeq(), "[SUCCESS] 문 열림", keyCardEntity.getKeyCardName()+" 카드키가 사용되었습니다.");
            // 6-1-3. 비밀번호 해제 로그 생성
            saveOpenLog(1, 2L, doorLock, keyCardEntity.getKeyCardName()+"(키카드)");
            // 6-1-3. return
            return ApiResponse.SUCCESS("인증되었습니다.");
        } else { // 4-2. 해제 실패
            // 6-1-1. 틀린 횟수 +1
            doorLock.openTag(0);
            // 6-1-2. 비밀번호 해제 실패 알림 전송
            sendNotification(doorLock.getDoorLockSeq(), "[FAIL] 문 열림 실패", "태그기능이 사용되었습니다.");
            // 6-1-3. 비밀번호 해제 실패 로그 생성
            saveOpenLog(0, 2L, doorLock, "???");
            // 6-1-4. return
            return ApiResponse.FAILURE(400, "등록되지 않은 태깅 정보입니다.");
        }
    }

    /**
     * 지문 해제 : 디바이스에서 인증 후 결과 값만 전송
     * @param dto OpenLockRequestDto.openByFingerPrint
     * @return ApiResponse<?>
     */
    public ApiResponse<?> openByFingerPrint(OpenLockRequestDto.openByFingerPrint dto) {
        // 1. serialNo를 사용해서 도어락 조회 (없으면 안됨.)
        DoorLock doorLock = findDoorLock(dto.getSerialNo());
        if (doorLock == null) return ApiResponse.ERROR(404, "등록되지 않은 도어락입니다.");

        // 2. 비밀번호 해제 결과에 대한 핸드폰 알림 전송
        if (dto.getOpenYn() == 1) { // 2-1. 해제 성공
            // 2-1-1. 지문 번호 조회
            Optional<KeyBio> keyBioOpt = keyBioRepository.findByKeyBioDataAndDoorLock_DoorLockSeq(dto.getKeyBioData(), doorLock.getDoorLockSeq());
            if (keyBioOpt.isEmpty()) { // 이미 문은 열린 상태이므로 SUCCESS 상태로 알림 전송 및 로그 기록
                sendNotification(doorLock.getDoorLockSeq(), "[SUCCESS] 문 열림", "알수 없는 지문이 사용되었습니다.");
                saveOpenLog(1, 3L, doorLock, "???");
                return ApiResponse.ERROR(401, "등록되지 않은 지문정보입니다.");
            }
            // 2-1-2. 지문 해제 성공 알림 전송
            String keyBioName = keyBioOpt.get().getKeyBioName();
            sendNotification(doorLock.getDoorLockSeq(), "[SUCCESS] 문 열림", keyBioName+" 지문이 사용되었습니다.");
            // 2-1-3. 지문 해제 로그 생성
            saveOpenLog(1, 3L, doorLock, keyBioName);
            // 2-1-4. return
            return ApiResponse.SUCCESS("로그등록이 완료되었습니다.");
        } else { // 2-2. 해제 실패
            // 2-1-1. 지문 해제 실패 알림 전송
            sendNotification(doorLock.getDoorLockSeq(), "[FAIL] 문 열림 실패", "알수 없는 지문이 사용되었습니다.");
            // 2-1-2. 지문 해제 실패 로그 생성
            saveOpenLog(0, 3L, doorLock, "???");
            // 2-1-3. return
            return ApiResponse.SUCCESS("로그등록이 완료되었습니다.");
        }
    }


    // 태그리스 해제


    /* =====================================================================================================
     * 공통 코드
     * ===================================================================================================== */

    /**
     * serialNo를 사용해서 도어락 조회
     * @param serialNo String : 도어락 시리얼 번호
     * @return DoorLock : 찾은 도어락 객체
     */
    private DoorLock findDoorLock(String serialNo){
        Optional<DoorLock> doorLockOpt = doorLockRepository.findBySerialNo(serialNo);
        return doorLockOpt.orElse(null);
    }

    /**
     * owner 권한 사용자에게 도어락 해제 관련 알림 전송
     * @param doorLockSeq Long : 도어락 구분자
     * @param title String : 알림 제목
     * @param message String : 알림 내용
     */
    private void sendNotification(Long doorLockSeq, String title, String message) {
        // 1. 해당 도어락 owner (rdlAuth = 1) 찾기
        List<RegistDoorLock> registDoorLockOpt
                = registDoorLockRepository.findByRdlAuthAndDoorLock_DoorLockSeq(1, doorLockSeq);
        if (registDoorLockOpt.size() != 1) throw new RuntimeException("owner 권한을 가진 사용자가 없거나 2명이상입니다.");

        // 2. fcmToken 가져오기
        String fcmToken = registDoorLockOpt.get(0).getUser().getRefreshToken();

        log.info("=========================================================================================");
        log.info("fcmToken : {}", fcmToken);    // fcmToken : 파이어베이스에 저장한 해당 디바이스의 FCM 토큰 값)
        log.info("title : {}", title);          // title : 알림 제목
        log.info("message : {}", message);      // message : 알림으로 전달하려는 메시지
        log.info("=========================================================================================");
    }

    /**
     * 도어락 해제 관련 로그 저장
     * @param openYn int : 해제여부
     * @param openMethod Long : 해제 방법 구분자
     * @param doorLock DoorLock : 도어락Entity
     * @param nickname String : 비밀번호-알수 없음, nfc-해제한 사람 이름, 카드키/지문-해제구분자
     */
    private void saveOpenLog(int openYn, Long openMethod, DoorLock doorLock, String nickname){
        OpenLog openLog = OpenLog.builder()
                .openYn(openYn)
                .openMethod(openMethod)
                .nickname(nickname)
                .doorLock(doorLock)
                .build();
        openLogRepository.save(openLog);
    }
}
