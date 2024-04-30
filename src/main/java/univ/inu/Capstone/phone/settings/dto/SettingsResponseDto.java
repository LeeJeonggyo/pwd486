package univ.inu.Capstone.phone.settings.dto;

import lombok.Builder;
import lombok.Getter;
import univ.inu.Capstone.common.entity.TaglessTime;

import java.util.List;

public class SettingsResponseDto {
    @Getter
    @Builder
    public static class viewLog {
        private int openYn;             // 해제 여부
        private String openMethod;      // 해제 방법
        private String nickname;        // 사용자 이름
        private String inpDate;         // 출입 날짜
        private String inpTime;         // 출입 시간

        public void setOpenMethod(Long openMethod) {
            if (openMethod == 1L)
                this.openMethod = "password";
            else if (openMethod == 2L)
                this.openMethod = "TAG(NFC or RFID)";
            else if (openMethod == 3L)
                this.openMethod = "fingerprint";
            else if (openMethod == 4L)
                this.openMethod = "Tagless";
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
        private String keyCardName;         // 등록된 카드키 번호
        private String keyCardData;         // 등록된 카드키 번호
    }

    @Getter
    @Builder
    public static class viewRegistKeyBio {
        private Long keyBioSeq;             // 구분자
        private String keyBioName;         // 등록된 카드키 번호
        private int keyBioData;          // 등록된 지문 번호
    }

    @Getter
    @Builder
    public static class selectPrivateYn {
        private int dataYn;     // 데이터 수집동의 여부
        private int aiYn;       // AI 서비스 동의 여부
        private taglessTimeDto time;    // 저장된 태그리스 타임
    }

    @Getter
    public static class taglessTimeDto {
        private final String monTime;     // 월 태그리스 시간
        private final String tueTime;     // 화 태그리스 시간
        private final String wedTime;     // 수 태그리스 시간
        private final String thuTime;     // 목 태그리스 시간
        private final String friTime;     // 금 태그리스 시간
        private final String satTime;     // 토 태그리스 시간
        private final String sunTime;     // 일 태그리스 시간

        public taglessTimeDto(TaglessTime entity){
            this.monTime = entity.getMonTime();
            this.tueTime = entity.getTueTime();
            this.wedTime = entity.getWedTime();
            this.thuTime = entity.getThuTime();
            this.friTime = entity.getFriTime();
            this.satTime = entity.getSatTime();
            this.sunTime = entity.getSunTime();
        }
    }
}
