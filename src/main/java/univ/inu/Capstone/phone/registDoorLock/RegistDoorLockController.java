package univ.inu.Capstone.phone.registDoorLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import univ.inu.Capstone.common.utils.CustomUserDetails;
import univ.inu.Capstone.phone.registDoorLock.dto.RegistDoorLockRequestDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pw486/regist")
public class RegistDoorLockController {

    private final RegistDoorLockService registDoorLockService;

    /**
     * 도어락 기기 정보 등록
     * @param dto RegistDoorLockRequestDto.registMachine
     * @return ResponseEntity<?>
     */
    @PostMapping("/machine")
    public ResponseEntity<?> registMachine(@RequestBody RegistDoorLockRequestDto.registMachine dto){
        return ResponseEntity.ok().body(registDoorLockService.registMachine(dto));
    }

    /**
     * 시리얼 넘버를 통한 도어락 검색
     * @param serialNo String
     * @return ResponseEntity<?>
     */
    @GetMapping("/{serialNo}/search")
    public ResponseEntity<?> searchSerialNo(@PathVariable("serialNo") String serialNo){
        return ResponseEntity.ok().body(registDoorLockService.searchSerialNo(serialNo));
    }

    /**
     * 사용자 도어락 NFC 등록 (owner)
     * @param dto RegistDoorLockRequestDto.registNfc
     * @param authentication Authentication
     * @return ResponseEntity<?>
     */
    @PostMapping("/registNfc")
    public ResponseEntity<?> registNfc(@RequestBody RegistDoorLockRequestDto.registNfc dto, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok().body(registDoorLockService.registNfc(dto, userDetails.getUserSeq()));
    }

    /**
     * member, guest 초대 코드 생성
     * @param dto RegistDoorLockRequestDto.inviteCode
     * @return ResponseEntity<?>
     */
    @PostMapping("/inviteCode")
    public ResponseEntity<?> inviteCode(@RequestBody RegistDoorLockRequestDto.inviteCode dto){
        return ResponseEntity.ok().body(registDoorLockService.inviteCode(dto));
    }

    /**
     * 초대 코드 조회
     * @param inviteCode String
     * @return ResponseEntity<?>
     */
    @GetMapping("/inviteCode/{inviteCode}/search")
    public ResponseEntity<?> searchInviteCode(@PathVariable("inviteCode") String inviteCode){
        return ResponseEntity.ok().body(registDoorLockService.searchInviteCode(inviteCode));
    }

    /**
     * owner 권한 이외, NFC 등록 API
     * @param dto RegistDLRequestDto.registNfcOther
     * @param authentication Authentication
     * @return ResponseEntity<?>
     */
    @PostMapping("/registNfcOther")
    public ResponseEntity<?> registNfcOther(@RequestBody RegistDoorLockRequestDto.registNfcOther dto, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok().body(registDoorLockService.registNfcOther(dto, userDetails.getUserSeq()));
    }
}
