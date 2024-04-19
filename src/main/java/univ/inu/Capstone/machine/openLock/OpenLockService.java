package univ.inu.Capstone.machine.openLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.dto.apiResponse.ApiResponse;
import univ.inu.Capstone.common.entity.DoorLock;
import univ.inu.Capstone.common.entity.DoorLockSecret;
import univ.inu.Capstone.common.entity.OpenLog;
import univ.inu.Capstone.common.entity.RegistDoorLock;
import univ.inu.Capstone.common.repository.DoorLockRepository;
import univ.inu.Capstone.common.repository.DoorLockSecretRespository;
import univ.inu.Capstone.common.repository.OpenLogRepository;
import univ.inu.Capstone.common.repository.RegistDoorLockRepository;
import univ.inu.Capstone.machine.openLock.dto.OpenLockRequestDto;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenLockService {

    private final DoorLockRepository doorLockRepository;
    private final DoorLockSecretRespository doorLockSecretRespository;
    private final RegistDoorLockRepository registDoorLockRepository;
    private final OpenLogRepository openLogRepository;


    /**
     * 도어락 해제 관련 알림 전송
     * @param doorLockSeq Long
     * @param title String
     * @param message String
     */
    private void sendNotification(Long doorLockSeq, String title, String message) {
        // 1. 해당 도어락 owner (rdlAuth = 1) 찾기
        List<RegistDoorLock> registDoorLockOpt
                = registDoorLockRepository.findByRdlAuthAndDoorLock_DoorLockSeq(1, doorLockSeq);
        if (registDoorLockOpt.size() != 1) throw new RuntimeException("owner 권한을 가진 사용자 정보에 이상 발생");

        // 2. fcmToken 가져오기
        String fcmToken = registDoorLockOpt.get(0).getUser().getRefreshToken();

        log.info("=========================================================================================");
        log.info("fcmToken : {}", fcmToken);    // fcmToken : 파이어베이스에 저장한 해당 디바이스의 FCM 토큰 값)
        log.info("title : {}", title);          // title : 알림 제목
        log.info("message : {}", message);      // message : 알림으로 전달하려는 메시지
        log.info("=========================================================================================");
    }


    /**
     * 비밀번호 해제
     * @param dto OpenLockRequestDto.openBySecretNo
     * @return ApiResponse<?>
     */
    @Transactional
    public ApiResponse<?> openBySecretNo(OpenLockRequestDto.openBySecretNo dto){
        // 1. serialNo를 사용해서 도어락 조회 (없으면 안됨.)
        Optional<DoorLock> doorLockOpt = doorLockRepository.findBySerialNo(dto.getSerialNo());
        if (doorLockOpt.isEmpty()) return ApiResponse.ERROR(404, "등록되지 않은 도어락입니다.");
        DoorLock doorLock = doorLockOpt.get();

        // 2. 비밀번호 틀린 횟수 확인 (5번 부터 안됨.)
        if (doorLock.getFailCntSecretNo() == 5 ) return ApiResponse.FAILURE(401, "5회 이상 비밀번호 잘못 입력하였습니다.");

        // 3. secretNo와 비밀번호를 비교
        Optional<DoorLockSecret> doorLockSecretOpt = doorLockSecretRespository.findByDoorLock_DoorLockSeqAndDlSecretNo(doorLock.getDoorLockSeq(), dto.getSecretNo());

        // 4. 비밀번호 해제 결과에 대한 핸드폰 알림 전송
        if (doorLockSecretOpt.isEmpty()) { // 4-1. 해제 실패
            // 4-1-1. 틀린 횟수 +1
            doorLock.openFailSecretNo(0);
            // 4-1-2. 비밀번호 해제 실패 알림 전송
            sendNotification(doorLock.getDoorLockSeq(), "[FAIL] 문 열림 실패", "비밀번호가 사용되었습니다.");
            // 4-1-3. return
            return ApiResponse.FAILURE(400, "비밀번호 입력이 잘못되었습니다.");
        } else { // 4-2. 해제 성공
            // 4-1-1. 틀린 횟수 0으로 초기화
            doorLock.openFailSecretNo(1);
            // 4-1-2. 비밀번호 해제 성공 알림 전송
            sendNotification(doorLock.getDoorLockSeq(), "[SUCCESS] 문 열림", "비밀번호가 사용되었습니다.");
            // 4-1-3. return
            return ApiResponse.SUCCESS("인증되었습니다.");
        }
    }

    /**
     * RFID & NFC 해제
     * @param dto OpenLockRequestDto.openByRfidAndNfc
     * @return ApiResponse<?>
     */
    public ApiResponse<?> openByRfidAndNfc(OpenLockRequestDto.openByRfidAndNfc dto) {
        return ApiResponse.SUCCESS("인증되었습니다.");
    }

    // 지문 해제

    // 비콘 해제

}
