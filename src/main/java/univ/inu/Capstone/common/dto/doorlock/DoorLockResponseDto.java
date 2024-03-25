package univ.inu.Capstone.common.dto.doorlock;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;


public class DoorLockResponseDto {
    @Getter
    @Builder
    public static class SaveMachine {
        private String result;    // 결과값("SUCCESS / FAIL")
    }

    @Getter
    @Builder
    public static class SearchSerialNo {
        private Long doorLockSeq;   // 도어락 구분자
        private String result;      // 결과값("SUCCESS / FAIL")
    }
}
