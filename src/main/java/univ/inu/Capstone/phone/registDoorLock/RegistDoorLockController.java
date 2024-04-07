package univ.inu.Capstone.phone.registDoorLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import univ.inu.Capstone.common.dto.doorlock.DoorLockRequestDto;
import univ.inu.Capstone.common.dto.doorlock.DoorLockResponseDto;
import univ.inu.Capstone.common.dto.registDoorlock.RegistDLRequestDto;
import univ.inu.Capstone.common.dto.registDoorlock.RegistDLResponseDto;
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
     * @param dto DoorLockRequestDto.DoorLockBasic
     * @return ResponseEntity<DoorLockResponseDto.DoorLockResult>
     */
    @PostMapping("/machine")
    public ResponseEntity<DoorLockResponseDto.SaveMachine> registMachine(@RequestBody DoorLockRequestDto.DoorLockBasic dto){
        DoorLockResponseDto.SaveMachine result = registDoorLockService.registMachine(dto);
        return ResponseEntity.ok().body(result);
    }

    /**
     * 시리얼 넘버를 통한 도어락 검색
     * @param serialNo String
     * @return ResponseEntity<DoorLockResponseDto.SearchSerialNo>
     */
    @GetMapping("/{serialNo}/search")
    public ResponseEntity<DoorLockResponseDto.SearchSerialNo> searchSerialNo(@PathVariable("serialNo") String serialNo){
        DoorLockResponseDto.SearchSerialNo result = registDoorLockService.searchSerialNo(serialNo);
        return ResponseEntity.ok().body(result);
    }

    /**
     * 사용자 도어락 NFC 등록
     * @return ResponseEntity<DoorLockResponseDto.DoorLockResult>
     */
    @PostMapping("/registNfc")
    public ResponseEntity<RegistDLResponseDto.RegistDL> registNfc(@RequestBody RegistDLRequestDto.RegistDL dto, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("userSeq : {}", userDetails.getUserSeq());

        RegistDLResponseDto.RegistDL result = registDoorLockService.registDL(dto, userDetails.getUserSeq());
        return ResponseEntity.ok().body(result);
    }
}
