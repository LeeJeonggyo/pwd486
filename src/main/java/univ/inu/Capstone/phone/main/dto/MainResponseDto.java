package univ.inu.Capstone.phone.main.dto;

import lombok.Data;

@Data
public class MainResponseDto {
    @Data
    public static class delNfcOther {
        private String state;
        private String result;

        public delNfcOther(String result){
            if("FAIL".equals(result)){
                this.result = result;
            } else {
                this.state = "201";
                this.result = result;
            }
        }
    }
}
