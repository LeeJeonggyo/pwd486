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

    @Data
    public static class viewRegistKey {
        private Long rdlSeq;        // NFC 구분자
    }

    @Data
    public static class usePermit {
        private Long rdlSeq;        // 등록 NFC 구분자
    }

    @Data
    public static class delNfcOther {
        private Long rdlSeq;        // 삭제 NFC 구분자
    }

    @Data
    public static class delKeyCard {
        private Long keyCardSeq;    // 삭제 카드키 구분자
    }

    @Data
    public static class delKeyBio {
        private Long keyBioSeq;    // 삭제 카드키 구분자
    }

    @Data
    public static class tossOwnerAuth {
        private Long rdlSeq;        // owner 권한을 양도할 NFC 구분자
    }
}
