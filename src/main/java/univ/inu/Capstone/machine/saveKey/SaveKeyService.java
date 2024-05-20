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
        if (doorLockOpt.isEmpty()) return ApiResponse.FAILURE(404, "Not found doorLock");
        DoorLock doorLock = doorLockOpt.get();

        // 2. 해당 번호로 등록된 카드키가 있는지 확인
        Optional<KeyCard> keyCard = keyCardRepository.findByKeyCardDataAndDoorLock_DoorLockSeq(dto.getKeyCardData(), doorLock.getDoorLockSeq());
        if (keyCard.isPresent()) return ApiResponse.FAILURE(400, "already exists");

        // 3. 2에서 없을 경우, 등록
        KeyCard save = KeyCard.builder()
                .keyCardName("카드키")
                .keyCardData(dto.getKeyCardData())
                .doorLock(doorLock)
                .build();
        keyCardRepository.save(save);

        return ApiResponse.SUCCESS("SUCCESS");
    }

    /**
     * 지문 등록
     * @param dto SaveKeyRequestDto.saveBioKey
     * @return ApiResponse<?>
     */
    public ApiResponse<?> saveKeyBio(SaveKeyRequestDto.saveKeyBio dto){
        // 1. 도어락 확인
        Optional<DoorLock> doorLockOpt = doorLockRepository.findBySerialNo(dto.getSerialNo());
        if (doorLockOpt.isEmpty()) return ApiResponse.FAILURE(404, "Not found doorLock");
        DoorLock doorLock = doorLockOpt.get();

        // 2. 해당 번호로 등록된 지문이 있는지 확인
        Optional<KeyBio> keyBio = keyBioRepository.findByKeyBioDataAndDoorLock_DoorLockSeq(dto.getKeyBioData(), doorLock.getDoorLockSeq());
        if (keyBio.isPresent()) return ApiResponse.FAILURE(400, "already exists");

        // 3. 2에서 없을 경우, 등록
        KeyBio save = KeyBio.builder()
                .keyBioName("지문 "+dto.getKeyBioData())
                .keyBioData(dto.getKeyBioData())
                .doorLock(doorLock)
                .build();
        keyBioRepository.save(save);

        return ApiResponse.SUCCESS("SUCCESS");
    }

    /**
     * 비밀번호 변경
     * @param dto SaveKeyRequestDto.changePwd
     * @return ApiResponse<?>
     */
    @Transactional
    public ApiResponse<?> changePwd(SaveKeyRequestDto.changePwd dto){
        // 1. 도어락 확인
        Optional<DoorLock> doorLockOpt = doorLockRepository.findBySerialNo(dto.getSerialNo());
        if (doorLockOpt.isEmpty()) return ApiResponse.FAILURE(404, "Not found doorLock");
        DoorLock doorLock = doorLockOpt.get();

        // 2. 도어락의 비밀번호 entity 호출
        Optional<DoorLockSecret> doorLockSecret = doorLockSecretRespository.findByDoorLock_DoorLockSeq(doorLock.getDoorLockSeq());
        if (doorLockSecret.isEmpty()) return ApiResponse.FAILURE(404, "Not found PWD");

        // 3. 2에서 없을 경우, 등록
        DoorLockSecret update = doorLockSecret.get();
        update.changePwUnknownUser(dto.getSecretNo());

        return ApiResponse.SUCCESS("SUCCESS");
    }

    /**
     * 카드키 삭제
     * @param dto SaveKeyRequestDto.delKeyCard
     * @return ApiResponse<?>
     */
    public ApiResponse<?> delKeyCard(SaveKeyRequestDto.delKeyCard dto){
        // 1. 도어락 확인
        Optional<DoorLock> doorLockOpt = doorLockRepository.findBySerialNo(dto.getSerialNo());
        if (doorLockOpt.isEmpty()) return ApiResponse.FAILURE(404, "Not found doorLock");
        DoorLock doorLock = doorLockOpt.get();

        // 2. 해당 번호로 등록된 카드키가 있는지 확인
        Optional<KeyCard> keyCard = keyCardRepository.findByKeyCardDataAndDoorLock_DoorLockSeq(dto.getKeyCardData(), doorLock.getDoorLockSeq());
        if (keyCard.isEmpty()) return ApiResponse.FAILURE(400, "Not found CARD KEY");

        // 3. 2에서 있을 경우, 삭제
        keyCardRepository.delete(keyCard.get());

        return ApiResponse.SUCCESS("SUCCESS");
    }

    /**
     * 지문 정보 삭제
     * @param dto SaveKeyRequestDto.delKeyCard
     * @return ApiResponse<?>
     */
    public ApiResponse<?> delKeyBio(SaveKeyRequestDto.delKeyBio dto){
        // 1. 도어락 확인
        Optional<DoorLock> doorLockOpt = doorLockRepository.findBySerialNo(dto.getSerialNo());
        if (doorLockOpt.isEmpty()) return ApiResponse.FAILURE(404, "Not found doorLock");
        DoorLock doorLock = doorLockOpt.get();

        // 2. 해당 번호로 등록된 지문이 있는지 확인
        Optional<KeyBio> keyBio = keyBioRepository.findByKeyBioDataAndDoorLock_DoorLockSeq(dto.getKeyBioData(), doorLock.getDoorLockSeq());
        if (keyBio.isEmpty()) return ApiResponse.FAILURE(400, "Not found fingerprint");

        // 3. 2에서 있을 경우, 삭제
        keyBioRepository.delete(keyBio.get());

        return ApiResponse.SUCCESS("SUCCESS");
    }
}
