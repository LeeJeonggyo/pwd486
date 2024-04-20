package univ.inu.Capstone.phone.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.dto.apiResponse.ApiResponse;
import univ.inu.Capstone.common.dto.user.UserDto;
import univ.inu.Capstone.common.entity.User;
import univ.inu.Capstone.common.repository.UserRepository;
import univ.inu.Capstone.common.utils.JwtUtil;
import univ.inu.Capstone.phone.login.dto.LoginDto;
import univ.inu.Capstone.phone.login.dto.TokenDto;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;

    @Value("${jwt.secret.ACCESS_TOKEN_KEY}")
    private String accessTokenKey;
    @Value("${jwt.secret.REFRESH_TOKEN_KEY}")
    private String refreshTokenKey;
    private Long accessExpireTimeMs = 1000*60*5L; // 5분
    private Long refreshExpireTimeMs = 1000*60*10L; // 10분

    /**
     * 카카오 로그인 (최초)
     * @param dto LoginDto
     * @return ApiResponse<TokenDto.responseDto>
     */
    @Transactional
    public ApiResponse<TokenDto.responseDto> firstLogin(LoginDto dto){
        // 1. kakaoId로 중복 체크 진행
        Optional<User> user = userRepository.findByKakaoId(dto.getKakaoId());

        // 2. id가 없는 경우, 회원가입 진행
        User login = null;
        if(user.isPresent()) login = user.get();
        else {
            User newUser = User.builder()
                    .kakaoId(dto.getKakaoId())
                    .fcmToken(dto.getFcmToken())
                    .nickname(dto.getNickname())
                    .email(dto.getEmail())
                    .build();
            login = userRepository.save(newUser);
        }

        // 3. entity -> dto 로 데이터 담기
        UserDto userDto = UserDto.builder()
                .userSeq(login.getUserSeq())
                .nickname(login.getNickname())
                .email(login.getEmail())
                .build();

        // 4. 1 또는 2에서 발급받은 데이터를 통해 JWT 생성 및 반환
        String accessToken = JwtUtil.createAccessToken(userDto, accessTokenKey, accessExpireTimeMs);
        String refreshToken = JwtUtil.createRefreshToken(userDto, refreshTokenKey, refreshExpireTimeMs);
        login.updateRefreshToken(refreshToken);

        // TokenDto 데이터 담아서 전달
        return ApiResponse.SUCCESS("SUCCESS : JWT 발급",
                TokenDto.responseDto.builder()
                        .nickname(userDto.getNickname())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build());
    }

    /**
     * accessToken 재발급 (only refreshToken)
     * @param request HttpServletRequest
     * @return TokenDto.responseDto
     */
    @Transactional
    public ApiResponse<TokenDto.responseDto> refreshLogin(HttpServletRequest request){

        // http header로 부터 refresh token 추출
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 제대로된 형식으로 들어왔는지 체크
        if (authorization == null || !authorization.startsWith("Bearer")){
            log.error("잘못된 authorization 입니다.");
            return ApiResponse.ERROR(401, "잘못된 authorization 입니다.");
        }

        // refresh token 추출
        String refreshToken = authorization.split(" ")[1];

        // refresh token Expired 여부 확인
        if(JwtUtil.isExpired(refreshToken, refreshTokenKey)){
            log.error("Token이 만료되었습니다.");
            return ApiResponse.FAILURE(401, "Token이 만료되었습니다.");
        }

        // DB에 저장된 refresh token 인지 확인
        Optional<User> checkUser = userRepository.findByRefreshToken(refreshToken);
        User checkUserEntity = null;
        if(checkUser.isEmpty()) return null;
        else checkUserEntity = checkUser.get();


        // refresh token에 저장된 데이터로 accessToken, refreshToken 재발급
        UserDto userDto = JwtUtil.getUserDto(refreshToken, refreshTokenKey);
        String reAccessToken = JwtUtil.createAccessToken(userDto, accessTokenKey, accessExpireTimeMs);
        String reRefreshToken = JwtUtil.createRefreshToken(userDto, refreshTokenKey, refreshExpireTimeMs);    // accessToken 유효시간의 30배
        checkUserEntity.updateRefreshToken(reRefreshToken);

        // TokenDto에 데이터 담아서 전달
        return ApiResponse.SUCCESS("SUCCESS : JWT 재발급",
                TokenDto.responseDto.builder()
                    .nickname(userDto.getNickname())
                    .accessToken(reAccessToken)
                    .refreshToken(reRefreshToken)
                    .build());
    }
}
