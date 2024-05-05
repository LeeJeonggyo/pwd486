package univ.inu.Capstone.phone.settings.dto;

import lombok.Builder;
import lombok.Getter;
import univ.inu.Capstone.common.entity.OpenLog;
import univ.inu.Capstone.common.entity.TaglessTime;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SettingsResponseDto {
    @Getter
    @Builder
    public static class lastLog {
        private int dataYn;         // 로그데이터 존재 여부
        private Long openMethod;    // 오픈 방식
        private String nickname;    // 오픈한 사람
        private String userName;    // owner 이름
        private String lastTime;    // 마지막 로그 시간
    }

    @Getter
    public static class viewLog {
        private int openYn;             // 해제 여부
        private Long openMethod;      // 해제 방법
        private String nickname;        // 사용자 이름
        private String inpDate;         // 출입 날짜
        private String inpTime;         // 출입 시간
        private int isThisUser;         // 요청한 사용자와 로그에 등록된 사용자가 일치하는지 여부

        public viewLog(OpenLog entity, Long userSeq){
            this.openYn = entity.getOpenYn();
            this.openMethod = entity.getOpenMethod();
            this.nickname = entity.getNickname();
            this.inpDate = entity.getInpDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            this.inpTime = entity.getInpDate().format(DateTimeFormatter.ofPattern("HH시 mm분 ss.SSS초"));
            this.isThisUser = (entity.getUser() != null && userSeq.equals(entity.getUser().getUserSeq())) ? 1 : 0;
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
    public static class viewApproveNfcList{
        private List<viewRegistKeyNfc> rdlList;         // NFC 정보 리스트
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

    @Getter
    @Builder
    public static class aiServiceToggle {
        private int aiYn;       // AI 서비스 동의 여부
        private taglessTimeDto time;    // 저장된 태그리스 타임
    }
}
