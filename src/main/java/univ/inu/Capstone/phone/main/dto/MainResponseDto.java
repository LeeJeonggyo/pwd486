package univ.inu.Capstone.phone.main.dto;

import lombok.Getter;
import univ.inu.Capstone.common.entity.RegistDoorLock;

@Getter
public class MainResponseDto {

    @Getter
    public static class getMyNfcList {
        private Long rdlSeq;
        private String rdlName;
        private String nfcData;
        private int rdlAuth;
        private int rdlApprove;
        private Long doorLockSeq;
        public getMyNfcList(RegistDoorLock entity) {
            this.rdlSeq = entity.getRdlSeq();
            this.rdlName = entity.getRdlName();
            this.nfcData = entity.getNfcData();
            this.rdlAuth = entity.getRdlAuth();
            this.rdlApprove = entity.getRdlApprove();
            this.doorLockSeq = entity.getDoorLock().getDoorLockSeq();
        }
    }
}
