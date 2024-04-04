package univ.inu.Capstone.phone.main;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.entity.RegistDoorLock;
import univ.inu.Capstone.common.repository.RegistDoorLockRepository;
import univ.inu.Capstone.phone.main.dto.MainRequestDto;
import univ.inu.Capstone.phone.main.dto.MainResponseDto;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MainService {

    private final RegistDoorLockRepository registDoorLockRepository;

    /**
     * owner 권한 이외, 등록된 NFC 삭제 API
     * @param dto MainRequestDto.delNfcOther
     * @return MainResponseDto.delNfcOther
     */
    public MainResponseDto.delNfcOther delNfcOther(MainRequestDto.delNfcOther dto, Long userSeq){
        // 1. 해당 도어락에 등록된 NFC가 맞는지 확인
        Optional<RegistDoorLock> check = registDoorLockRepository.findById(dto.getRdlSeq());
        if(check.isEmpty()
                || !userSeq.equals(check.get().getUser().getUserSeq()))
            return new MainResponseDto.delNfcOther("FAIL");

        // 2. 해당 NFC의 권한이 owner 인지 아닌지 확인(owner 가 아니여야 함.)
        if(check.get().getRdlAuth() == 1)
            return new MainResponseDto.delNfcOther("FAIL");

        // 3. delete
        registDoorLockRepository.delete(check.get());
        return new MainResponseDto.delNfcOther("SUCCESS");
    }
}
