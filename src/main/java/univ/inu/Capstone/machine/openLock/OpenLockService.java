package univ.inu.Capstone.machine.openLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import univ.inu.Capstone.common.dto.apiResponse.ApiResponse;
import univ.inu.Capstone.common.entity.*;
import univ.inu.Capstone.common.repository.*;
import univ.inu.Capstone.common.notification.NotificationService;
import univ.inu.Capstone.machine.openLock.dto.OpenLockRequestDto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OpenLockService {

    private final NotificationService notificationService;
    private final DoorLockRepository doorLockRepository;
    private final DoorLockSecretRespository doorLockSecretRespository;
    private final RegistDoorLockRepository registDoorLockRepository;
    private final OpenLogRepository openLogRepository;
    private final KeyCardRepository keyCardRepository;
    private final KeyBioRepository keyBioRepository;
    private final UserRepository userRepository;
    private final TaglessTimeRepository taglessTimeRepository;

    /**
     * 비밀번호 해제
     * @param dto OpenLockRequestDto.openBySecretNo
     * @return ApiResponse<?>
     */
    @Transactional
    public ApiResponse<?> openBySecretNo(OpenLockRequestDto.openBySecretNo dto) {
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
    @Transactional
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
            // 4-1-2. owner 권한이 nfc를 사용하여 출입한 경우, 비밀번호 틀린 횟수도 0으로 초기화한다.
            if(nfcEntity.getRdlAuth() == 1) doorLock.openSecretNo(1);

            // 4-1-3. GUEST 권한은 알림 및 기록을 생성하지 않는다.
            if(nfcEntity.getRdlAuth() != 3){
                // 4-1-3-1. 비밀번호 해제 성공 알림 전송 (GUEST 권한은 알림 X)
                sendNotification(doorLock.getDoorLockSeq(), "[SUCCESS] 문 열림", nfcEntity.getRdlName()+"님께서 문을 열었습니다.");
                // 4-1-3-2. 비밀번호 해제 로그 생성 (GUEST 권한은 해제 로그 X)
                saveOpenLog(1, 2L, doorLock, nfcEntity.getRdlName()+"(핸드폰)");
            }

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

    /**
     * 태그리스 해제
     * @param dto OpenLockRequestDto.openByTagless
     * @return ApiResponse<?>
     */
    public ApiResponse<?> openByTagless(OpenLockRequestDto.openByTagless dto) {
        // 1. btSerialNo을 사용해서 도어락 조회 (없으면 안됨.)
        Optional<DoorLock> doorLockOpt = doorLockRepository.findByBtSerialNo(dto.getBtSerialNo());
        if (doorLockOpt.isEmpty()) return ApiResponse.ERROR(404, "등록되지 않은 도어락입니다.");
        DoorLock doorLock = doorLockOpt.get();

        // 2. 태그리스 가능 시간 확인
        boolean taglessTimeValidation = taglessTimeValidation(doorLock.getDoorLockSeq());
        if (!taglessTimeValidation)
            return ApiResponse.FAILURE(404, "태그리스 시간이 아닙니다.");

        // 3. kakaoId를 사용해서 사용자 조회(if: JWT 토큰 확인으로 Authentication 으로 확인 할 수 있다면 그렇게 변경할 것.)
        Optional<User> userOpt = userRepository.findByKakaoId(dto.getKakaoId());
        if (userOpt.isEmpty()) return ApiResponse.ERROR(404, "등록되지 않은 사용자입니다.");

        // 4. owner 권한 확인
        User user = userOpt.get();
        Optional<RegistDoorLock> registDoorLockOpt
                = registDoorLockRepository.findByRdlAuthAndDoorLock_DoorLockSeqAndUser_UserSeq(1, doorLock.getDoorLockSeq(), user.getUserSeq());
        if (registDoorLockOpt.isEmpty()) return ApiResponse.ERROR(401, "태그리스 접근 권한이 없습니다.");
        String rdlName = registDoorLockOpt.get().getRdlName();

        // 5. 도어락으로 open 신호 보내기

        // 6. 알림전송
        sendNotification(doorLock.getDoorLockSeq(), "[SUCCESS] 문 열림", rdlName+"님께서 태그리스 기능을 사용하셨습니다.");

        // 7. 로그기록
        saveOpenLog(1, 4L, doorLock, rdlName);

        // 8. return
        return ApiResponse.SUCCESS("인증되었습니다.");
    }


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
        // 2. owner 권한을 가진 사용자가 없거나 2명이상인 경우, 알림 없이 진행
        if (registDoorLockOpt.size() != 1) return;

        // 3. fcmToken 가져오기
        String fcmToken = registDoorLockOpt.get(0).getUser().getFcmToken();

        // 4. 알림 전송
        notificationService.sendNotification(fcmToken, title, message);
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


    /**
     * 태그리스 가능 시간 확인
     * @param doorLockSeq Long : 도어락 구분자
     * @return boolean : 태그리스 작동 가능 여부
     */
    private boolean taglessTimeValidation(Long doorLockSeq){
        LocalDateTime nowDateTime = LocalDateTime.now();
        int dayOfWeekNumber = nowDateTime.getDayOfWeek().getValue();    // 현재 요일
        LocalTime nowTime = nowDateTime.toLocalTime();                  // 현재 시간

        String taglessTimeStr = getTaglessTime(doorLockSeq, dayOfWeekNumber);   // 현재 요일에 해당하는 태그리스 시작 시간
        if (taglessTimeStr == null) return false;
        String[] taglessTimeStrSep = taglessTimeStr.split("\\.");

        int taglessHour = Integer.parseInt(taglessTimeStrSep[0]);
        int taglessMin = taglessTimeStrSep.length > 1 ? (int) (Integer.parseInt(taglessTimeStrSep[1])*0.06) : 0;
        LocalTime taglessTimeStart = LocalTime.of(taglessHour, taglessMin, 0);
        LocalTime taglessTimeEnd = taglessTimeStart.plusHours(3L);

        return !nowTime.isBefore(taglessTimeStart) && !nowTime.isAfter(taglessTimeEnd);
    }

    /**
     * 요일에 맞는 태그리스 시간 추출
     * @param doorLockSeq Long : 도어락 구분자
     * @param dayOfWeekNumber int : 요일 번호
     * @return String : 요일에 맞는 태그리스 시간
     */
    private String getTaglessTime(Long doorLockSeq, int dayOfWeekNumber){
        Optional<TaglessTime> taglessTimeOpt = taglessTimeRepository.findByDoorLock_doorLockSeq(doorLockSeq);
        if (taglessTimeOpt.isEmpty()) return null;     // 태그리스 로우가 없는 경우
        TaglessTime taglessTime = taglessTimeOpt.get();

        switch (dayOfWeekNumber){
            case 1:
                return taglessTime.getMonTime();
            case 2:
                return taglessTime.getTueTime();
            case 3:
                return taglessTime.getWedTime();
            case 4:
                return taglessTime.getThuTime();
            case 5:
                return taglessTime.getFriTime();
            case 6:
                return taglessTime.getSatTime();
            case 7:
                return taglessTime.getSunTime();
            default:
                return null;
        }
    }
}
