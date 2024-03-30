package univ.inu.Capstone.phone.settings.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class SettingsResponseDto {
    @Getter
    @Builder
    public static class changePw {
        private String result;      // 변경 결과
    }
}
