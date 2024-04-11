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
     * NFC 를 사용하여 도어락 해제
     * @param dto OpenLockRequestDto.openLockByNfc
     * @return ResponseEntity<?>
     */
    @PostMapping("/nfc")
    public ResponseEntity<?> openLockByNfc(@RequestBody OpenLockRequestDto.openLockByNfc dto){
        return ResponseEntity.ok().body(openLockService.openLockByNfc(dto));
    }
}
