package univ.inu.Capstone.common.dto.registDoorlock;


import lombok.Data;

@Data
public class RegistDLRequestDto {
    @Data
    public static class RegistDL {
        private String rdlName;     // 카드키 이름
        private Long doorLockSeq;   // 도어락 구분자
    }
}
