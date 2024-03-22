package univ.inu.Capstone.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import univ.inu.Capstone.common.dto.UserDto;
;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
public class JwtUtil {
    /* ===============================================================
     * secretKey 인코딩
     * =============================================================== */
    private static Key getSiginingKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /* ===============================================================
     * 토큰 생성
     * =============================================================== */
    // access 토큰 생성
    public static String createAccessToken(UserDto dto, String secretKey, long expireTimeMs) {
        // 일종의 map 객체로 JWT 토큰안에 필요한 데이터들을 넣기 위해 사용
        Claims claims = Jwts.claims();
        claims.put("userSeq", dto.getUserSeq());
        claims.put("nickname", dto.getNickname());
        claims.put("email", dto.getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(getSiginingKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    // refresh 토큰 생성 (refresh 토큰의 유효시간은 access 토큰 유효 시간의 30배)
    public static String createRefreshToken(UserDto dto, String secretKey, long expireTimeMs) {
        // 일종의 map 객체로 JWT 토큰안에 필요한 데이터들을 넣기 위해 사용
        Claims claims = Jwts.claims();
        claims.put("userSeq", dto.getUserSeq());
        claims.put("nickname", dto.getNickname());
        claims.put("email", dto.getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(getSiginingKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    /* ===============================================================
     * 토큰 Expired 여부 확인
     * =============================================================== */
    public static boolean isExpired(String token, String secretKey) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(getSiginingKey(secretKey)).build()
                .parseClaimsJws(token);
        return claimsJws.getBody().getExpiration().before(new Date());
    }

    /* ===============================================================
     * 토큰에서 userSeq 값 꺼내기
     * =============================================================== */
    public static Long getUserSeq(String token, String secretKey) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(getSiginingKey(secretKey)).build()
                .parseClaimsJws(token);
        return claimsJws.getBody().get("userSeq", Long.class);
    }

    /* ===============================================================
     * 토큰에서 사용자 객체 꺼내기
     * =============================================================== */
    public static UserDto getUserDto(String token, String secretKey) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(getSiginingKey(secretKey)).build()
                .parseClaimsJws(token);

        return UserDto.builder()
                .userSeq(claimsJws.getBody().get("userSeq", Long.class))
                .nickname(claimsJws.getBody().get("nickname", String.class))
                .email(claimsJws.getBody().get("email", String.class))
                .build();
    }
}
