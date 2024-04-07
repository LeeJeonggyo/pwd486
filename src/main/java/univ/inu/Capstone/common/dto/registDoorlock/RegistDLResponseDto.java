package univ.inu.Capstone.common.dto.registDoorlock;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import univ.inu.Capstone.common.entity.RegistDoorLock;

@Data
public class RegistDLResponseDto {
    @Getter
    @Builder
    public static class RegistDL {
        private String rdlName;     // 카드키 이름
        private String serialNo;   // 도어락 구분자
    }

    @Getter
    @Builder
    public static class registNfcOther {
        private String rdlName;     // 카드키 이름
        private String serialNo;   // 도어락 구분자
    }
}
