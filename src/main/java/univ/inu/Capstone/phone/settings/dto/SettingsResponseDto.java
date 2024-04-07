package univ.inu.Capstone.phone.settings.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import univ.inu.Capstone.common.entity.OpenLog;

import java.time.format.DateTimeFormatter;

public class SettingsResponseDto {
    @Getter
    @Builder
    public static class changePw {
        private String result;      // 변경 결과
    }

    @Getter
    @Builder
    public static class viewLog {
        private String openMethod;      // 해제 방법
        private String nickname;        // 사용자 이름 
        private String inpDate;         // 출입 날짜
        private String inpTime;         // 출입 시간

        public void setOpenMethod(Long openMethod) {
            if (openMethod == 0L)
                this.openMethod = "해제 실패";
            else if (openMethod == 1L)
                this.openMethod = "password";
            else if (openMethod == 2L)
                this.openMethod = "password";
            else if (openMethod == 3L)
                this.openMethod = "cardKey";
            else if (openMethod == 4L)
                this.openMethod = "fingerprint";
            else
                this.openMethod = "정의 할수 없는 출입";
        }
    }

    @Getter
    @Builder
    public static class usePermit {
        private int state;          // 결과 state
        private String result;      // 결과 msg
    }

    @Getter
    @Builder
    public static class delNfcOther {
        private int state;          // 결과 state
        private String result;      // 결과 msg
    }

    @Getter
    @Builder
    public static class tossOwnerAuth {
        private int state;          // 결과 state
        private String result;      // 결과 msg
    }
}
