package univ.inu.Capstone.phone.main;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import univ.inu.Capstone.common.utils.CustomUserDetails;
import univ.inu.Capstone.phone.main.dto.MainRequestDto;

@RestController
@RequestMapping("/api/pw486/main")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    /**
     * 사용자별 등록된 nfc 데이터 리스트 출력
     * @param authentication Authentication
     * @return ResponseEntity<?>
     */
    @PostMapping("/getMyNfcList")
    public ResponseEntity<?> getMyNfcList(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok().body(mainService.getMyNfcList(userDetails.getUserSeq()));
    }

    /**
     * owner 권한 이외, 등록된 NFC 삭제 API
     * @param dto MainRequestDto.delNfcOther
     * @return ResponseEntity<?>
     */
    @PostMapping("/delNfcOther")
    public ResponseEntity<?> delNfcOther(@RequestBody MainRequestDto.delNfcOther dto, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok().body(mainService.delNfcOther(dto, userDetails.getUserSeq()));
    }
}
