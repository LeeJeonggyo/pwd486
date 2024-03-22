package univ.inu.Capstone.phone.registDoorLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import univ.inu.Capstone.common.utils.CustomUserDetails;
import univ.inu.Capstone.phone.login.dto.LoginDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pw486/regist")
public class RegistDoorLockController {

    private final RegistDoorLockService registDoorLockService;

    /**
     * 도어락 기기 정보 등록
     * @param dto LoginDto
     * @return ResponseEntity<String>
     */
    @PostMapping("/machine")
    public ResponseEntity<String> saveMachine(@RequestBody LoginDto dto, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("userSeq : {}", userDetails.getUserSeq());
        log.info("nickname : {}", userDetails.getUsername());
        log.info("email : {}", userDetails.getEmail());

//        String result = registDoorLockService.mainTest(dto.getKakaoId());
        return ResponseEntity.ok().body("");
    }
}
