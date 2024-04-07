package univ.inu.Capstone.common.dto.registDoorlock;


import lombok.Data;

@Data
public class RegistDLRequestDto {
    @Data
    public static class RegistDL {
        private String rdlName;     // 카드키 이름
        private Long doorLockSeq;   // 도어락 구분자
    }
    @Data
    public static class inviteCode {
        private Long rdlSeq;    // 코드 생성자 NFC 구분자
        private int giveAuth;   // 초대 코드 수신자에게 부여할 권한
    }
    @Data
    public static class registNfcOther {
        private String rdlName;     // 카드키 이름
        private Long doorLockSeq;   // 도어락 구분자
        private Long inviteSeq;     // 초대 구분자
    }
}
