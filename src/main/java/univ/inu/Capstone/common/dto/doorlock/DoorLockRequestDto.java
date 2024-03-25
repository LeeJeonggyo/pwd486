package univ.inu.Capstone.common.dto.doorlock;

import lombok.Data;
import univ.inu.Capstone.common.entity.DoorLock;

@Data
public class DoorLockRequestDto {
    @Data
    public static class DoorLockBasic {
        private String serialNo;    // 시리얼넘버

        public DoorLock toEntity(){
            return DoorLock.builder()
                    .serialNo(this.serialNo)
                    .build();
        }
    }

}
