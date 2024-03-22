package univ.inu.Capstone.common.dto.doorlock;

import lombok.Data;

@Data
public class DoorLockRequestDto {
    @Data
    public static class DoorLockCheck {
        private String serialNo;    // 시리얼넘버
    }

}
