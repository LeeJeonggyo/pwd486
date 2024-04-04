package univ.inu.Capstone.machine.openLock.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OpenLockResponseDto {
    @Data
    public static class openLockByNfc{
        private int state;
        private String result;
        private String msg;

        public openLockByNfc(int state, String msg){
            this.state = state;
            this.msg = msg;
            if(state == 400)
                this.result = "FAIL";
            else
                this.result = "SUCCESS";
        }
    }
}
