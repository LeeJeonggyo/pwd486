package univ.inu.Capstone.phone.registDoorLock.dto;

import lombok.Data;
import univ.inu.Capstone.common.entity.DoorLock;

@Data
public class RegistDoorLockRequestDto {
    @Data
    public static class registMachine {
        private String serialNo;    // 시리얼넘버
        private String btSerialNo;   // 비콘 시리얼 넘버
        public DoorLock toEntity(){
            return DoorLock.builder()
                    .serialNo(this.serialNo)
                    .btSerialNo(this.btSerialNo)
                    .build();
        }
    }
    @Data
    public static class registNfc {
        private String rdlName;     // 카드키 이름
        private Long doorLockSeq;   // 도어락 구분자
    }
    @Data
    public static class inviteCode {
        private Long rdlSeq;    // 코드 생성자 NFC 구분자
        private int giveAuth;   // 초대 코드 수신자에게 부여할 권한
    }
    @Data
    public static class registNfcOther {
        private String rdlName;     // 카드키 이름
        private Long doorLockSeq;   // 도어락 구분자
        private Long inviteSeq;     // 초대 구분자
    }
}
