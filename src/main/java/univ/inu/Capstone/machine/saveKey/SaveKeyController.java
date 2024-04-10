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
     * @param dto SaveKeyRequestDto.saveCardKey
     * @return ResponseEntity<?>
     */
    @PostMapping("/save/cardKey")
    public ResponseEntity<?> saveCardKey(@RequestBody SaveKeyRequestDto.saveCardKey dto){
        return ResponseEntity.ok().body(saveKeyService.saveCardKey(dto));
    }

    /**
     * 지문 등록
     * @param dto SaveKeyRequestDto.saveBioKey
     * @return ResponseEntity<?>
     */
    @PostMapping("/save/bioKey")
    public ResponseEntity<?> saveBioKey(@RequestBody SaveKeyRequestDto.saveBioKey dto){
        return ResponseEntity.ok().body(saveKeyService.saveBioKey(dto));
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
     * @param dto SaveKeyRequestDto.delCardKey
     * @return ResponseEntity<?>
     */
    @PostMapping("/del/cardKey")
    public ResponseEntity<?> delCardKey(@RequestBody SaveKeyRequestDto.delCardKey dto){
        return ResponseEntity.ok().body(saveKeyService.delCardKey(dto));
    }

    // 지문 삭제

}
