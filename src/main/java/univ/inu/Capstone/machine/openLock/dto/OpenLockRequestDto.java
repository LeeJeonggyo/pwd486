package univ.inu.Capstone.machine.openLock.dto;

import lombok.Data;

@Data
public class OpenLockRequestDto {
    @Data
    public static class openBySecretNo{
        private String serialNo;    // 도어락 구분을 위한 시리얼 번호
        private String secretNo;    // 입력된 비밀번호
    }
    @Data
    public static class openByRfidAndNfc{
        private String serialNo;        // 도어락 시리얼 넘버
        private String keyCardData;     // RFID & NFC 데이터
    }
    @Data
    public static class openByFingerPrint{
        private String serialNo;    // 도어락 시리얼 넘버
        private int keyBioData;     // 등록된 지문 번호
        private int openYn;         // 해제 여부(0:fail / 1:success)
    }
    @Data
    public static class openByTagless{
        private String btSerial;    // 도어락 비콘 시리얼 넘버
        private String kakaoId;     // 등록된 사용자 id
    }
}
