package univ.inu.Capstone.phone.registDoorLock.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistDoorLockResponseDto {
    @Getter
    @Builder
    public static class searchSerialNo {
        private Long doorLockSeq;   // 도어락 구분자
    }
    @Getter
    @Builder
    public static class registNfc {
        private String rdlName;     // 카드키 이름
        private String serialNo;   // 도어락 구분자
    }
    @Getter
    @Builder
    public static class inviteCode {
        private String inviteCode;
    }
    @Getter
    @Builder
    public static class searchInviteCode {
        private Long doorLockSeq;   // 도어락 구분자
        private Long inviteSeq;     // 초대 구분자
    }
    @Getter
    @Builder
    public static class registNfcOther {
        private String rdlName;     // 카드키 이름
        private String serialNo;   // 도어락 구분자
    }
}
