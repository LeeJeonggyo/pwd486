package univ.inu.Capstone.machine.saveKey;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import univ.inu.Capstone.machine.saveKey.dto.SaveKeyRequestDto;

@RestController
@RequestMapping("/machine/pw486/saveKey")
@RequiredArgsConstructor
public class SaveKeyController {
    private final SaveKeyService saveKeyService;

    /**
     * 카드키 등록
     * @param dto SaveKeyRequestDto.saveKeyCard
     * @return ResponseEntity<?>
     */
    @PostMapping("/saveKey/card")
    public ResponseEntity<?> saveKeyCard(@RequestBody SaveKeyRequestDto.saveKeyCard dto){
        return ResponseEntity.ok().body(saveKeyService.saveKeyCard(dto));
    }

    /**
     * 지문 등록
     * @param dto SaveKeyRequestDto.saveKeyBio
     * @return ResponseEntity<?>
     */
    @PostMapping("/saveKey/bio")
    public ResponseEntity<?> saveKeyBio(@RequestBody SaveKeyRequestDto.saveKeyBio dto){
        return ResponseEntity.ok().body(saveKeyService.saveKeyBio(dto));
    }

    /**
     * 비밀번호 변경
     * @param dto SaveKeyRequestDto.changePwd
     * @return ResponseEntity<?>
     */
    @PostMapping("/change/pwd")
    public ResponseEntity<?> changePwd(@RequestBody SaveKeyRequestDto.changePwd dto){
        return ResponseEntity.ok().body(saveKeyService.changePwd(dto));
    }

    /**
     * 카드키 삭제
     * @param dto SaveKeyRequestDto.delKeyCard
     * @return ResponseEntity<?>
     */
    @PostMapping("/delKey/card")
    public ResponseEntity<?> delKeyCard(@RequestBody SaveKeyRequestDto.delKeyCard dto){
        return ResponseEntity.ok().body(saveKeyService.delKeyCard(dto));
    }

    /**
     * 지문 정보 삭제
     * @param dto SaveKeyRequestDto.delKeyBio
     * @return ResponseEntity<?>
     */
    @PostMapping("/delKey/bio")
    public ResponseEntity<?> delKeyBio(@RequestBody SaveKeyRequestDto.delKeyBio dto){
        return ResponseEntity.ok().body(saveKeyService.delKeyBio(dto));
    }
}
