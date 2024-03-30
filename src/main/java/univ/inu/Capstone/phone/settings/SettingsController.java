package univ.inu.Capstone.phone.settings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import univ.inu.Capstone.common.utils.CustomUserDetails;
import univ.inu.Capstone.phone.settings.dto.SettingsRequestDto;
import univ.inu.Capstone.phone.settings.dto.SettingsResponseDto;

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
}
