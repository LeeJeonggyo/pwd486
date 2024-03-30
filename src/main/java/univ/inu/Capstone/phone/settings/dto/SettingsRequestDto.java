package univ.inu.Capstone.phone.settings.dto;

import lombok.Data;

@Data
public class SettingsRequestDto {
    @Data
    public static class changePw {
        private Long doorLockSeq;       // 도어락 구분자
        private String dlSecretNo;      // 현재 비밀번호
        private String newSecretNo;     // 새 비밀번로
        private String checkSecretNo;   // 새 비밀번호 확인
    }

    @Data
    public static class viewLog {
        private Long doorLockSeq;       // 도어락 구분자
    }
}
