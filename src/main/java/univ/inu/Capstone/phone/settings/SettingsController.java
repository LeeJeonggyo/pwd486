package univ.inu.Capstone.phone.settings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import univ.inu.Capstone.common.dto.apiResponse.ApiResponse;
import univ.inu.Capstone.common.utils.CustomUserDetails;
import univ.inu.Capstone.phone.settings.dto.SettingsRequestDto;
import univ.inu.Capstone.phone.settings.dto.SettingsResponseDto;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pw486/settings")
public class SettingsController {
    private final SettingsService settingsService;

    /**
     * 도어락 비밀번호 변경
     * @param dto SettingsRequestDto.changePw
     * @param authentication Authentication
     * @return SettingsResponseDto.changePw
     */
    @PostMapping("/pw/change")
    public ResponseEntity<SettingsResponseDto.changePw> changePw(@RequestBody SettingsRequestDto.changePw dto, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok().body(settingsService.changePw(dto, userDetails.getUserSeq()));
    }

    /**
     * 출입로그 조회
     * @param dto SettingsRequestDto.viewLog
     * @param authentication Authentication
     * @return ResponseEntity<Map<String, Object>>
     */
    @PostMapping("/view/log")
    public ResponseEntity<Map<String, Object>> viewLog(@RequestBody SettingsRequestDto.viewLog dto, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok().body(settingsService.viewLog(dto, userDetails.getUserSeq()));
    }

    /**
     * 등록된 nfc, 지문, 카드키 전체 조회
     * @param dto SettingsRequestDto.viewRegistKey
     * @param authentication Authentication
     * @return ResponseEntity<?>
     */
    @PostMapping("/view/regist/key")
    public ResponseEntity<?> viewRegistKey(@RequestBody SettingsRequestDto.viewRegistKey dto, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok().body(settingsService.viewRegistKey(dto, userDetails.getUserSeq()));
    }


    /**
     * member & guest 사용허가
     * @param dto SettingsRequestDto.usePermit
     * @param authentication Authentication
     * @return ResponseEntity<SettingsResponseDto.usePermit>
     */
    @PostMapping("/use/permit")
    public ResponseEntity<SettingsResponseDto.usePermit> usePermit(@RequestBody SettingsRequestDto.usePermit dto, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok().body(settingsService.usePermit(dto, userDetails.getUserSeq()));
    }

    /**
     * member & guest 삭제
     * @param dto SettingsRequestDto.delNfcOther
     * @param authentication Authentication
     * @return ResponseEntity<SettingsResponseDto.delNfcOther>
     */
    @PostMapping("/delete/nfc/other")
    public ResponseEntity<SettingsResponseDto.delNfcOther> delNfcOther(@RequestBody SettingsRequestDto.delNfcOther dto, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok().body(settingsService.delNfcOther(dto, userDetails.getUserSeq()));
    }

    /**
     * 카드키 삭제
     * @param dto SettingsRequestDto.delKeyCard
     * @param authentication Authentication
     * @return ResponseEntity<?>
     */
    @PostMapping("/delete/keyCard")
    public ResponseEntity<?> delKeyCard(@RequestBody SettingsRequestDto.delKeyCard dto, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok().body(settingsService.delKeyCard(dto, userDetails.getUserSeq()));
    }

    /**
     * 지문 삭제
     * @param dto SettingsRequestDto.delKeyBio
     * @param authentication Authentication
     * @return ResponseEntity<?>
     */
    @PostMapping("/delete/keyBio")
    public ResponseEntity<?> delKeyBio(@RequestBody SettingsRequestDto.delKeyBio dto, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok().body(settingsService.delKeyBio(dto, userDetails.getUserSeq()));
    }

    /**
     * owner 권한 양도
     * @param dto SettingsRequestDto.tossOwnerAuth
     * @param authentication Authentication
     * @return ResponseEntity<SettingsResponseDto.tossOwnerAuth>
     */
    @PostMapping("/toss/owner")
    public ResponseEntity<SettingsResponseDto.tossOwnerAuth> tossOwnerAuth(@RequestBody SettingsRequestDto.tossOwnerAuth dto, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok().body(settingsService.tossOwnerAuth(dto, userDetails.getUserSeq()));
    }
}
