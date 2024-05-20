package univ.inu.Capstone.machine.openLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import univ.inu.Capstone.common.dto.apiResponse.ApiResponse;
import univ.inu.Capstone.common.entity.*;
import univ.inu.Capstone.common.repository.*;
import univ.inu.Capstone.common.notification.NotificationService;
import univ.inu.Capstone.machine.openLock.dto.OpenLockRequestDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final ActivateNfcLogRepository activateNfcLogRepository;

    /**
     * 비밀번호 해제
     * @param dto OpenLockRequestDto.openBySecretNo
     * @return ApiResponse<?>
     */
    @Transactional
    public ApiResponse<?> openBySecretNo(OpenLockRequestDto.openBySecretNo dto) {
        // 1. serialNo를 사용해서 도어락 조회 (없으면 안됨.)
        DoorLock doorLock = findDoorLock(dto.getSerialNo());
        if (doorLock == null) return ApiResponse.ERROR(404, "This is an unregistered door lock.");

        // 2. 비밀번호 틀린 횟수 확인 (5번 부터 안됨.)
        if (doorLock.getFailCntSecretNo() >= 5 ) return ApiResponse.FAILURE(401, "password incorrectly more than 5 times.");

        // 3. secretNo와 비밀번호를 비교
        Optional<DoorLockSecret> doorLockSecretOpt = doorLockSecretRespository.findByDoorLock_DoorLockSeqAndDlSecretNo(doorLock.getDoorLockSeq(), dto.getSecretNo());

        // 4. 비밀번호 해제 결과에 대한 핸드폰 알림 전송
        if (doorLockSecretOpt.isPresent()) { // 4-1. 해제 성공
            // 4-1-1. 틀린 횟수 0으로 초기화
            doorLock.openSecretNo(1);
            // 4-1-2. 비밀번호 해제 성공 알림 전송
            sendNotification(doorLock.getDoorLockSeq(), "[SUCCESS] 문 열림", "비밀번호가 사용되었습니다.");
            // 4-1-3. 비밀번호 해제 로그 생성
            saveOpenLog(1, 1L, doorLock, "비밀번호", null);
            // 4-1-4. return
            return ApiResponse.SUCCESS("SUCCESS");
        } else { // 4-2. 해제 실패
            // 4-1-1. 틀린 횟수 +1
            doorLock.openSecretNo(0);
            // 4-1-2. 비밀번호 해제 실패 알림 전송
            sendNotification(doorLock.getDoorLockSeq(), "[FAIL] 문 열림 실패", "비밀번호가 사용되었습니다.");
            // 4-1-3. 비밀번호 해제 실패 로그 생성
            saveOpenLog(0, 1L, doorLock, "???(비밀번호)", null);
            // 4-1-4. return
            return ApiResponse.FAILURE(400, "The password input is incorrect");
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
        if (doorLock == null) return ApiResponse.ERROR(404, "This is an unregistered door lock.");

        // 2. 키카드로 등록된 데이터인지 확인 (nfc의 UUID 값을 보안상의 이유로 획득할수 없으므로 카드키데이터 먼저 확인할 필요가 있다. )
        Optional<KeyCard> keyCardOpt = keyCardRepository
                .findByKeyCardDataAndDoorLock_DoorLockSeq(dto.getKeyCardData(), doorLock.getDoorLockSeq());

        // 3. nfc 활성화 요청이 있었는지 확인
        Optional<ActivateNfcLog> activateNfcLogOpt = activateNfcLogRepository.findActivateLog(doorLock.getDoorLockSeq());

        // 4. failCntTag 값 확인 (5이상인지 확인 && (nfc 활성화 요청이 없음 || 키카드로 등록된 데이터가 있음) 문열림 불가)
        if (doorLock.getFailCntTag() >= 5
                && (activateNfcLogOpt.isEmpty() || keyCardOpt.isPresent()))
            return ApiResponse.FAILURE(401, "Tagging was incorrect more than 5 times.");

        // 5. 키카드 데이터가 존재하는 경우,
        if (keyCardOpt.isPresent()) {
            KeyCard keyCardEntity = keyCardOpt.get();
            // 5-1. 틀린 횟수 0으로 초기화
            doorLock.openTag(1);
            // 5-2. 비밀번호 해제 성공 알림 전송
            sendNotification(doorLock.getDoorLockSeq(), "[SUCCESS] 문 열림", keyCardEntity.getKeyCardName()+" 카드키가 사용되었습니다.");
            // 5-3. 비밀번호 해제 로그 생성
            saveOpenLog(1, 2L, doorLock, keyCardEntity.getKeyCardName(), null);
            // 5-4. return
            return ApiResponse.SUCCESS("SUCCESS");
        }

        // 6. 키카드 데이터가 존재하지 않고, nfc 활성화 요청이 있는 경우,
        if (activateNfcLogOpt.isPresent()){
            ActivateNfcLog activateNfcLog = activateNfcLogOpt.get();
            // 6-1. 활성화 요청한 구분값들이 해당 도어락에 등록된 nfc로 존재하는지 확인.
            Optional<RegistDoorLock> nfcOpt = registDoorLockRepository
                    .findByUser_UserSeqAndDoorLock_DoorLockSeq(activateNfcLog.getUser().getUserSeq(), activateNfcLog.getDoorLock().getDoorLockSeq());

            // 6-2. failCntTag 값 확인 (5이상인지 확인 / 5이상일 경우, nfc owner 권한만 오픈 가능)
            if (doorLock.getFailCntTag() >= 5
                    && (nfcOpt.isEmpty() || nfcOpt.get().getRdlAuth() != 1))
                return ApiResponse.FAILURE(401, "Tagging was incorrect more than 5 times.");

            // 6-3. nfc 로 등록된 사용자인 경우, 문 열림 알림 전송
            if (nfcOpt.isPresent()) {
                RegistDoorLock nfcEntity = nfcOpt.get();
                // 6-3-1. 틀린 횟수 0으로 초기화
                doorLock.openTag(1);
                // 6-3-2. owner 권한이 nfc를 사용하여 출입한 경우, 비밀번호 틀린 횟수도 0으로 초기화한다.
                if(nfcEntity.getRdlAuth() == 1) doorLock.openSecretNo(1);

                // 6-3-3. GUEST 권한은 알림 및 기록을 생성하지 않는다.
                if(nfcEntity.getRdlAuth() != 3){
                    // 6-3-3-1. 비밀번호 해제 성공 알림 전송 (GUEST 권한은 알림 X)
                    sendNotification(doorLock.getDoorLockSeq(), "[SUCCESS] 문 열림", nfcEntity.getRdlName()+"님께서 문을 열었습니다.");
                    // 6-3-3-2. 비밀번호 해제 로그 생성 (GUEST 권한은 해제 로그 X)
                    saveOpenLog(1, 2L, doorLock, nfcEntity.getRdlName()+"(핸드폰)", nfcEntity.getUser());
                }

                // 6-3-4. nfc 활성화 사용 처리
                activateNfcLog.updateUseYn();

                // 6-3-5. return
                return ApiResponse.SUCCESS("SUCCESS");
            }
        }

        // 7. 키카드 데이터가 존재하지 않고, (nfc 활성화 요청이 없거나, 활성화 요청에 사용자가 nfc 로 등록된 사용자가 아닌 경우)
        // 7-1. 틀린 횟수 +1
        doorLock.openTag(0);
        // 7-2. 비밀번호 해제 실패 알림 전송
        sendNotification(doorLock.getDoorLockSeq(), "[FAIL] 문 열림 실패", "태그기능이 사용되었습니다.");
        // 7-3. 비밀번호 해제 실패 로그 생성
        saveOpenLog(0, 2L, doorLock, "???(태그)", null);
        // 7-4. return
        return ApiResponse.FAILURE(400, "Tagging open is not possible.");
    }

    /**
     * 지문 해제 : 디바이스에서 인증 후 결과 값만 전송
     * @param dto OpenLockRequestDto.openByFingerPrint
     * @return ApiResponse<?>
     */
    public ApiResponse<?> openByFingerPrint(OpenLockRequestDto.openByFingerPrint dto) {
        // 1. serialNo를 사용해서 도어락 조회 (없으면 안됨.)
        DoorLock doorLock = findDoorLock(dto.getSerialNo());
        if (doorLock == null) return ApiResponse.ERROR(404, "This is an unregistered door lock.");

        // 2. 비밀번호 해제 결과에 대한 핸드폰 알림 전송
        if (dto.getOpenYn() == 1) { // 2-1. 해제 성공
            // 2-1-1. 지문 번호 조회
            Optional<KeyBio> keyBioOpt = keyBioRepository.findByKeyBioDataAndDoorLock_DoorLockSeq(dto.getKeyBioData(), doorLock.getDoorLockSeq());
            if (keyBioOpt.isEmpty()) { // 이미 문은 열린 상태이므로 SUCCESS 상태로 알림 전송 및 로그 기록
                sendNotification(doorLock.getDoorLockSeq(), "[SUCCESS] 문 열림", "알수 없는 지문이 사용되었습니다.");
                saveOpenLog(1, 3L, doorLock, "???(지문)", null);
                return ApiResponse.ERROR(401, "unregistered fingerprint");
            }
            // 2-1-2. 지문 해제 성공 알림 전송
            String keyBioName = keyBioOpt.get().getKeyBioName();
            sendNotification(doorLock.getDoorLockSeq(), "[SUCCESS] 문 열림", keyBioName+" 지문이 사용되었습니다.");
            // 2-1-3. 지문 해제 로그 생성
            saveOpenLog(1, 3L, doorLock, keyBioName, null);
            // 2-1-4. return
            return ApiResponse.SUCCESS("SUCCESS");
        } else { // 2-2. 해제 실패
            // 2-1-1. 지문 해제 실패 알림 전송
            sendNotification(doorLock.getDoorLockSeq(), "[FAIL] 문 열림 실패", "알수 없는 지문이 사용되었습니다.");
            // 2-1-2. 지문 해제 실패 로그 생성
            saveOpenLog(0, 3L, doorLock, "???(지문)", null);
            // 2-1-3. return
            return ApiResponse.SUCCESS("SUCCESS");
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
        if (doorLockOpt.isEmpty()) return ApiResponse.ERROR(404, "This is an unregistered door lock.");
        DoorLock doorLock = doorLockOpt.get();

        // 2. 태그리스 가능 시간 확인
        boolean taglessTimeValidation = taglessTimeValidation(doorLock.getDoorLockSeq());
        if (!taglessTimeValidation)
            return ApiResponse.FAILURE(404, "This is not tagless time.");

        // 3. kakaoId를 사용해서 사용자 조회(if: JWT 토큰 확인으로 Authentication 으로 확인 할 수 있다면 그렇게 변경할 것.)
        Optional<User> userOpt = userRepository.findByKakaoId(dto.getKakaoId());
        if (userOpt.isEmpty()) return ApiResponse.ERROR(404, "an unregistered user");

        // 4. owner 권한 확인
        User user = userOpt.get();
        Optional<RegistDoorLock> registDoorLockOpt
                = registDoorLockRepository.findByRdlAuthAndDoorLock_DoorLockSeqAndUser_UserSeq(1, doorLock.getDoorLockSeq(), user.getUserSeq());
        if (registDoorLockOpt.isEmpty()) return ApiResponse.ERROR(401, "do not have tagless access");
        String rdlName = registDoorLockOpt.get().getRdlName();

        // 5. 도어락으로 open 신호 보내기
//        requestOpenDoor();

        // 6. 알림전송
        sendNotification(doorLock.getDoorLockSeq(), "[SUCCESS] 문 열림", rdlName+"님께서 태그리스 기능을 사용하셨습니다.");

        // 7. 로그기록
        saveOpenLog(1, 4L, doorLock, rdlName+"(태그리스)", user);

        // 8. return
        return ApiResponse.SUCCESS("SUCCESS");
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
    private void saveOpenLog(int openYn, Long openMethod, DoorLock doorLock, String nickname, User user){
        // 1. 데이터 수집 미동의시 로그 데이터 저장 안함.
        if(doorLock.getDataYn() == 0) return;

        // 2. 로그 데이터 저장
        OpenLog openLog = OpenLog.builder()
                .openYn(openYn)
                .openMethod(openMethod)
                .nickname(nickname)
                .doorLock(doorLock)
                .user(user)
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
        LocalDate nowDate = nowDateTime.toLocalDate();                  // 현재 날짜

        String taglessTimeStr = getTaglessTime(doorLockSeq, dayOfWeekNumber);   // 현재 요일에 해당하는 태그리스 시작 시간
        if (taglessTimeStr == null) return false;

        LocalDateTime taglessDateTimeStart = getLocalDateTime(taglessTimeStr, nowDate);
        LocalDateTime taglessDateTimeEnd = taglessDateTimeStart.plusHours(3L);

        return !nowDateTime.isBefore(taglessDateTimeStart) && !nowDateTime.isAfter(taglessDateTimeEnd);
    }

    private static LocalDateTime getLocalDateTime(String taglessTimeStr, LocalDate nowDate) {
        String[] taglessTimeStrSep = taglessTimeStr.split("\\.");
        int taglessHour = Integer.parseInt(taglessTimeStrSep[0]);
        int taglessMin = taglessTimeStrSep.length > 1 ? (int) (Integer.parseInt(taglessTimeStrSep[1])*0.06) : 0;

        if(taglessTimeStr.indexOf('-') != -1){
            taglessHour = 23;
            taglessMin = 60 - taglessMin;
        }

        LocalTime taglessTimeStart = LocalTime.of(taglessHour, taglessMin, 0);

        if(taglessTimeStr.indexOf('-') != -1)
            return LocalDateTime.of(nowDate.minusDays(1L), taglessTimeStart);
        else
            return LocalDateTime.of(nowDate, taglessTimeStart);
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


    // 도어락으로 open 신호 보내기
    private void requestOpenDoor(){
        Map<String, String> params = new HashMap<>();
        params.put("command", "open");
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://127.0.0.1:5000/openDoor";
        ResponseEntity<String> response = restTemplate.postForEntity(url, params, String.class);
        System.out.println(response.getBody());
    }
}
