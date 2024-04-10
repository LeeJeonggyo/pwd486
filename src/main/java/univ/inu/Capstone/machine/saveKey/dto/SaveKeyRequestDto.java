package univ.inu.Capstone.machine.saveKey.dto;

import lombok.Data;

@Data
public class SaveKeyRequestDto {
    @Data
    public static class saveCardKey{
        private String serialNo;    // 도어락 시리얼넘버
        private String keyCardData; // 등록된 카드키 번호
    }
    @Data
    public static class saveBioKey{
        private String serialNo;    // 도어락 시리얼넘버
        private String keyBioData;  // 등록된 지문 번호
    }
    @Data
    public static class changePwd{
        private String serialNo;    // 도어락 시리얼넘버
        private String secretNo;    // 변경된 비밀번호
    }
    @Data
    public static class delCardKey{
        private String serialNo;    // 도어락 시리얼넘버
        private String keyCardData; // 등록된 카드키 번호
    }
}
