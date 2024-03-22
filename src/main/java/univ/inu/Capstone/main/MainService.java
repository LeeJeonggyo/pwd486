package univ.inu.Capstone.main;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.entity.User;
import univ.inu.Capstone.common.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MainService {

    private final UserRepository userRepository;
    public String mainTest(String kakaoId){
        Optional<User> user = userRepository.findByKakaoId(kakaoId);
        return user.isEmpty() ? "":user.get().getKakaoId();
    }
}
