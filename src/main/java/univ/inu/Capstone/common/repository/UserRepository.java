package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import univ.inu.Capstone.common.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(String kakaoId);
    Optional<User> findByUserSeqAndFcmToken(Long userSeq, String fcmToken);
    Optional<User> findByRefreshToken(String refreshToken);
}
