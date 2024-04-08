package univ.inu.Capstone.machine.saveKey;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/machine/pw486/saveKey")
@RequiredArgsConstructor
public class SaveKeyController {
    private final SaveKeyService saveKeyService;

    // 카드키 등록

    // 지문 등록

    // 비밀번호 변경

    // 카드키 삭제

    // 지문 삭제

}
