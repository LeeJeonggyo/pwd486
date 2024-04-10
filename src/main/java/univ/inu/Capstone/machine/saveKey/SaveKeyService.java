package univ.inu.Capstone.machine.saveKey;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.dto.apiResponse.ApiResponse;
import univ.inu.Capstone.common.entity.DoorLock;
import univ.inu.Capstone.common.entity.KeyBio;
import univ.inu.Capstone.common.entity.KeyCard;
import univ.inu.Capstone.common.repository.DoorLockRepository;
import univ.inu.Capstone.common.repository.KeyBioRepository;
import univ.inu.Capstone.common.repository.KeyCardRepository;
import univ.inu.Capstone.machine.saveKey.dto.SaveKeyRequestDto;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SaveKeyService {

    private final DoorLockRepository doorLockRepository;
    private final KeyCardRepository keyCardRepository;
    private final KeyBioRepository keyBioRepository;

    /**
     * 카드키 등록
     * @param dto SaveKeyRequestDto.saveCardKey
     * @return ApiResponse<?>
     */
    @Transactional
    public ApiResponse<?> saveCardKey(SaveKeyRequestDto.saveCardKey dto){
        // 1. 도어락 확인
        Optional<DoorLock> doorLockOpt = doorLockRepository.findBySerialNo(dto.getSerialNo());
        if (doorLockOpt.isEmpty()) return ApiResponse.FAILURE(404, "요청한 도어락 정보를 찾을 수 없습니다.");
        DoorLock doorLock = doorLockOpt.get();

        // 2. 해당 번호로 등록된 카드키가 있는지 확인
        Optional<KeyCard> keyCard = keyCardRepository.findByKeyCardDataAndDoorLock_DoorLockSeq(dto.getKeyCardData(), doorLock.getDoorLockSeq());
        if (keyCard.isPresent()) return ApiResponse.FAILURE(400, "이미 존재하는 데이터입니다.");

        // 3. 2에서 없을 경우, 등록
        KeyCard save = KeyCard.builder()
                .keyCardData(dto.getKeyCardData())
                .doorLock(doorLock)
                .build();
        keyCardRepository.save(save);

        return ApiResponse.SUCCESS("카드키 등록에 성공했습니다.");
    }

    /**
     * 지문 등록
     * @param dto SaveKeyRequestDto.saveBioKey
     * @return ApiResponse<?>
     */
    public ApiResponse<?> saveBioKey(SaveKeyRequestDto.saveBioKey dto){
        // 1. 도어락 확인
        Optional<DoorLock> doorLockOpt = doorLockRepository.findBySerialNo(dto.getSerialNo());
        if (doorLockOpt.isEmpty()) return ApiResponse.FAILURE(404, "요청한 도어락 정보를 찾을 수 없습니다.");
        DoorLock doorLock = doorLockOpt.get();

        // 2. 해당 번호로 등록된 지문이 있는지 확인
        Optional<KeyBio> keyBio = keyBioRepository.findByKeyBioDataAndDoorLock_DoorLockSeq(dto.getKeyBioData(), doorLock.getDoorLockSeq());
        if (keyBio.isPresent()) return ApiResponse.FAILURE(400, "이미 존재하는 데이터입니다.");

        // 3. 2에서 없을 경우, 등록
        KeyBio save = KeyBio.builder()
                .keyBioData(dto.getKeyBioData())
                .doorLock(doorLock)
                .build();
        keyBioRepository.save(save);

        return ApiResponse.SUCCESS("지문 등록에 성공했습니다.");
    }
}
