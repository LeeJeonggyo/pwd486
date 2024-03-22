package univ.inu.Capstone.phone.registDoorLock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.entity.User;
import univ.inu.Capstone.common.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistDoorLockService {

    private final UserRepository userRepository;
    public String mainTest(String kakaoId){
        Optional<User> user = userRepository.findByKakaoId(kakaoId);
        return user.isEmpty() ? "":user.get().getKakaoId();
    }
}
