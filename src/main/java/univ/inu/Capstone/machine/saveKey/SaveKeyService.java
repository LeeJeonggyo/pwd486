package univ.inu.Capstone.machine.saveKey;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.dto.apiResponse.ApiResponse;
import univ.inu.Capstone.common.entity.DoorLock;
import univ.inu.Capstone.common.entity.DoorLockSecret;
import univ.inu.Capstone.common.entity.KeyBio;
import univ.inu.Capstone.common.entity.KeyCard;
import univ.inu.Capstone.common.repository.DoorLockRepository;
import univ.inu.Capstone.common.repository.DoorLockSecretRespository;
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
    private final DoorLockSecretRespository doorLockSecretRespository;

    /**
     * 카드키 등록
     * @param dto SaveKeyRequestDto.saveKeyCard
     * @return ApiResponse<?>
     */
    @Transactional
    public ApiResponse<?> saveKeyCard(SaveKeyRequestDto.saveKeyCard dto){
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

        return ApiResponse.SUCCESS("카드키 정보 등록에 성공했습니다.");
    }

    /**
     * 지문 등록
     * @param dto SaveKeyRequestDto.saveBioKey
     * @return ApiResponse<?>
     */
    public ApiResponse<?> saveKeyBio(SaveKeyRequestDto.saveKeyBio dto){
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

        return ApiResponse.SUCCESS("지문 정보 등록에 성공했습니다.");
    }

    /**
     * 비밀번호 변경
     * @param dto SaveKeyRequestDto.changePwd
     * @return ApiResponse<?>
     */
    public ApiResponse<?> changePwd(SaveKeyRequestDto.changePwd dto){
        // 1. 도어락 확인
        Optional<DoorLock> doorLockOpt = doorLockRepository.findBySerialNo(dto.getSerialNo());
        if (doorLockOpt.isEmpty()) return ApiResponse.FAILURE(404, "요청한 도어락 정보를 찾을 수 없습니다.");
        DoorLock doorLock = doorLockOpt.get();

        // 2. 도어락의 비밀번호 entity 호출
        Optional<DoorLockSecret> doorLockSecret = doorLockSecretRespository.findByDoorLock_DoorLockSeq(doorLock.getDoorLockSeq());
        if (doorLockSecret.isEmpty()) return ApiResponse.FAILURE(404, "요청한 도어락의 pwd 정보를 찾을 수 없습니다.");

        // 3. 2에서 없을 경우, 등록
        DoorLockSecret update = doorLockSecret.get();
        update.changePw(dto.getSecretNo(), null);

        return ApiResponse.SUCCESS("비밀번호가 변경되었습니다.");
    }

    /**
     * 카드키 삭제
     * @param dto SaveKeyRequestDto.delKeyCard
     * @return ApiResponse<?>
     */
    public ApiResponse<?> delKeyCard(SaveKeyRequestDto.delKeyCard dto){
        // 1. 도어락 확인
        Optional<DoorLock> doorLockOpt = doorLockRepository.findBySerialNo(dto.getSerialNo());
        if (doorLockOpt.isEmpty()) return ApiResponse.FAILURE(404, "요청한 도어락 정보를 찾을 수 없습니다.");
        DoorLock doorLock = doorLockOpt.get();

        // 2. 해당 번호로 등록된 카드키가 있는지 확인
        Optional<KeyCard> keyCard = keyCardRepository.findByKeyCardDataAndDoorLock_DoorLockSeq(dto.getKeyCardData(), doorLock.getDoorLockSeq());
        if (keyCard.isEmpty()) return ApiResponse.FAILURE(400, "존재하지 않는 카드키입니다.");

        // 3. 2에서 있을 경우, 삭제
        keyCardRepository.delete(keyCard.get());

        return ApiResponse.SUCCESS("카드키 정보가 삭제 되었습니다.");
    }

    /**
     * 지문 정보 삭제
     * @param dto SaveKeyRequestDto.delKeyCard
     * @return ApiResponse<?>
     */
    public ApiResponse<?> delKeyBio(SaveKeyRequestDto.delKeyBio dto){
        // 1. 도어락 확인
        Optional<DoorLock> doorLockOpt = doorLockRepository.findBySerialNo(dto.getSerialNo());
        if (doorLockOpt.isEmpty()) return ApiResponse.FAILURE(404, "요청한 도어락 정보를 찾을 수 없습니다.");
        DoorLock doorLock = doorLockOpt.get();

        // 2. 해당 번호로 등록된 지문이 있는지 확인
        Optional<KeyBio> keyBio = keyBioRepository.findByKeyBioDataAndDoorLock_DoorLockSeq(dto.getKeyBioData(), doorLock.getDoorLockSeq());
        if (keyBio.isEmpty()) return ApiResponse.FAILURE(400, "존재하지 않는 카드키입니다.");

        // 3. 2에서 있을 경우, 삭제
        keyBioRepository.delete(keyBio.get());

        return ApiResponse.SUCCESS("지문 정보가 삭제 되었습니다.");
    }
}
