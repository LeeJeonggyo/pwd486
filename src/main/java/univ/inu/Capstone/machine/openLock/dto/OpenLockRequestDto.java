package univ.inu.Capstone.machine.openLock.dto;

import lombok.Data;

@Data
public class OpenLockRequestDto {
    @Data
    public static class openLockByNfc{
        private Long rdlSeq;        // NFC 구분자
        private String serialNo;    // 도어락 시리얼 넘버
    }
}
