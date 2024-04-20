package univ.inu.Capstone.machine.openLock;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import univ.inu.Capstone.machine.openLock.dto.OpenLockRequestDto;

@RestController
@RequestMapping("/machine/pw486/openLock")
@RequiredArgsConstructor
public class OpenLockController {

    private final OpenLockService openLockService;

    /**
     * 비밀번호 해제
     * @param dto OpenLockRequestDto.openBySecretNo
     * @return ResponseEntity<?>
     */
    @PostMapping("/secretNo")
    public ResponseEntity<?> openBySecretNo(@RequestBody OpenLockRequestDto.openBySecretNo dto){
        return ResponseEntity.ok().body(openLockService.openBySecretNo(dto));
    }

    /**
     * RFID & NFC 해제
     * @param dto OpenLockRequestDto.openByRfidAndNfc
     * @return ResponseEntity<?>
     */
    @PostMapping("/rfidAndNfc")
    public ResponseEntity<?> openByRfidAndNfc(@RequestBody OpenLockRequestDto.openByRfidAndNfc dto){
        return ResponseEntity.ok().body(openLockService.openByRfidAndNfc(dto));
    }

    /**
     * 지문 해제 : 디바이스에서 인증 후 결과 값만 전송
     * @param dto OpenLockRequestDto.openByFingerPrint
     * @return ResponseEntity<?>
     */
    @PostMapping("/fingerPrint")
    public ResponseEntity<?> openByFingerPrint(@RequestBody OpenLockRequestDto.openByFingerPrint dto){
        return ResponseEntity.ok().body(openLockService.openByFingerPrint(dto));
    }

    // 태그리스 해제

}
