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
}
