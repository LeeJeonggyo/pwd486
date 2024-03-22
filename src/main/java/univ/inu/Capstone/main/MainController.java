package univ.inu.Capstone.main;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import univ.inu.Capstone.login.dto.LoginDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pw486/main")
public class MainController {

    private final MainService mainService;

    @ResponseBody
    @PostMapping("/mainTest")
    public ResponseEntity<String> mainTest(@RequestBody LoginDto dto){
        String result = mainService.mainTest(dto.getKakaoId());
        return ResponseEntity.ok().body(result);
    }
    @PostMapping("/test")
    public ResponseEntity<String> test(Authentication authentication){
        // name에 userSeq 값이 String 데이터 타입으로 들어가 있다.
        return ResponseEntity.ok().body(authentication.getName());
    }
}
