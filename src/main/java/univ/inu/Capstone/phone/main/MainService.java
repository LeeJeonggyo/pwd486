package univ.inu.Capstone.phone.main;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.dto.apiResponse.ApiResponse;
import univ.inu.Capstone.common.entity.RegistDoorLock;
import univ.inu.Capstone.common.repository.RegistDoorLockRepository;
import univ.inu.Capstone.phone.main.dto.MainRequestDto;
import univ.inu.Capstone.phone.main.dto.MainResponseDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService {

    private final RegistDoorLockRepository registDoorLockRepository;

    /**
     * 사용자별 등록된 nfc 데이터 리스트 출력
     * @param userSeq Long 사용자 구분자
     * @return ApiResponse<List<MainResponseDto.getMyNfcList>>
     */
    public ApiResponse<?> getMyNfcList(Long userSeq){
        // 1. 사용자로 등록된 NFC 조회
        List<RegistDoorLock> nfcEntityList = registDoorLockRepository.findByUser_UserSeq(userSeq);

        // 2. dto 전환
        List<MainResponseDto.getMyNfcList> nfcList = nfcEntityList.stream()
                .map(MainResponseDto.getMyNfcList::new)
                .collect(Collectors.toList());

        return ApiResponse.SUCCESS("조회되었습니다.", nfcList);
    }

    /**
     * owner 권한 이외, 등록된 NFC 삭제 API
     * @param dto MainRequestDto.delNfcOther
     * @return ApiResponse<MainResponseDto.delNfcOther>
     */
    public ApiResponse<?> delNfcOther(MainRequestDto.delNfcOther dto, Long userSeq){
        // 1. 해당 도어락에 등록된 NFC가 맞는지 확인
        Optional<RegistDoorLock> check = registDoorLockRepository.findById(dto.getRdlSeq());
        if(check.isEmpty()
                || !userSeq.equals(check.get().getUser().getUserSeq()))
            return ApiResponse.FAILURE(404, "잘못된 도어락 정보입니다.");

        // 2. 해당 NFC의 권한이 owner 인지 아닌지 확인(owner 가 아니여야 함.)
        if(check.get().getRdlAuth() == 1)
            return ApiResponse.FAILURE(401, "권한이 없습니다.");

        // 3. delete
        registDoorLockRepository.delete(check.get());
        return ApiResponse.SUCCESS("삭제되었습니다.");
    }
}
