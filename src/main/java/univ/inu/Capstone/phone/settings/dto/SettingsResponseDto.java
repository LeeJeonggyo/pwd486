package univ.inu.Capstone.phone.settings.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class SettingsResponseDto {
    @Getter
    @Builder
    public static class viewLog {
        private String openMethod;      // 해제 방법
        private String nickname;        // 사용자 이름 
        private String inpDate;         // 출입 날짜
        private String inpTime;         // 출입 시간

        public void setOpenMethod(Long openMethod) {
            if (openMethod == 0L)
                this.openMethod = "해제 실패";
            else if (openMethod == 1L)
                this.openMethod = "password";
            else if (openMethod == 2L)
                this.openMethod = "password";
            else if (openMethod == 3L)
                this.openMethod = "cardKey";
            else if (openMethod == 4L)
                this.openMethod = "fingerprint";
            else
                this.openMethod = "정의 할수 없는 출입";
        }
    }

    @Getter
    @Builder
    public static class viewRegistKey {
        private List<viewRegistKeyNfc> rdlList;         // NFC 정보 리스트
        private List<viewRegistKeyCard> keyCardList;    // 카드키 정보 리스트
        private List<viewRegistKeyBio> keyBioList;      // 지문 정보 리스트

    }

    @Getter
    @Builder
    public static class viewRegistKeyNfc {
        private Long rdlSeq;    // 구분자
        private String rdlName; // 카드키 이름
        private int rdlAuth;    // 권한 (1:OWNER / 2:MEMBER / 3:GUEST)
        private int rdlApprove; // 승인여부 (0: 미승인 / 1: 승인) - OWNER 권한은 무조건 1
    }

    @Getter
    @Builder
    public static class viewRegistKeyCard {
        private Long keyCardSeq;            // 구분자
        private String keyCardData;         // 등록된 카드키 번호
    }

    @Getter
    @Builder
    public static class viewRegistKeyBio {
        private Long keyBioSeq;             // 구분자
        private String keyBioData;          // 등록된 지문 번호
    }
}
