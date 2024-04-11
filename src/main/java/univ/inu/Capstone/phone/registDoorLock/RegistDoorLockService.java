package univ.inu.Capstone.phone.registDoorLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.dto.apiResponse.ApiResponse;
import univ.inu.Capstone.common.entity.*;
import univ.inu.Capstone.common.repository.*;
import univ.inu.Capstone.phone.registDoorLock.dto.RegistDoorLockRequestDto;
import univ.inu.Capstone.phone.registDoorLock.dto.RegistDoorLockResponseDto;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistDoorLockService {

    private final UserRepository userRepository;
    private final DoorLockRepository doorLockRepository;
    private final RegistDoorLockRepository registDoorLockRepository;
    private final DoorLockSecretRespository doorLockSecretRespository;
    private final DoorLockInviteRepository doorLockInviteRepository;

    /**
     * 도어락 기기 정보 등록
     * @param dto RegistDoorLockRequestDto.registMachine
     * @return ApiResponse<?>
     */
    public ApiResponse<?> registMachine(RegistDoorLockRequestDto.registMachine dto){
        // 1. 등록된 시리얼번호가 있는지 확인
        Optional<DoorLock> data = doorLockRepository.findBySerialNo(dto.getSerialNo());
        if (data.isPresent())
            return ApiResponse.FAILURE(400,"이미 존재하는 시리얼 번호입니다.");

        // 2. 1에서 없을 경우, 도어락 정보 등록
        DoorLock doorLock = doorLockRepository.save(dto.toEntity());

        // 3. 비밀번호 low 생성
        DoorLockSecret doorLockSecret = DoorLockSecret.builder()
                .dlSecretNo("0000")
                .doorLock(doorLock)
                .build();
        doorLockSecretRespository.save(doorLockSecret);

        // 4. 결과 return
        return ApiResponse.SUCCESS("SUCCESS : 도어락 정보가 등록되었습니다.");
    }

    /**
     * 시리얼 넘버를 통한 도어락 검색
     * @param serialNo String
     * @return ApiResponse<RegistDoorLockResponseDto.searchSerialNo>
     */
    public ApiResponse<RegistDoorLockResponseDto.searchSerialNo> searchSerialNo(String serialNo){
        // 시리얼 넘버로 등록된 도어락 정보 확인
        Optional<DoorLock> data = doorLockRepository.findBySerialNo(serialNo);
        return data.map(
                doorLock -> ApiResponse.SUCCESS("잘못된 시리얼번호입니다.",
                        RegistDoorLockResponseDto.searchSerialNo.builder()
                                .doorLockSeq(doorLock.getDoorLockSeq())
                                .build()))
                .orElseGet(() -> ApiResponse.FAILURE(404, "잘못된 시리얼번호입니다."));
    }

    /**
     * 사용자 도어락 NFC 등록 (owner)
     * @param dto RegistDoorLockRequestDto.registNfc
     * @param userSeq Long
     * @return ApiResponse<RegistDoorLockResponseDto.registNfc>
     */
    @Transactional
    public ApiResponse<RegistDoorLockResponseDto.registNfc> registNfc(RegistDoorLockRequestDto.registNfc dto, Long userSeq){
        // 1. 넘어온 userSeq 데이터가 있는지 확인
        Optional<User> user = userRepository.findById(userSeq);
        if (user.isEmpty())
            return ApiResponse.ERROR(401, "알 수 없는 사용자입니다.");

        // 2. 넘어온 도어락 구분자값으로 등록된 도어락 정보가 있는지 확인
        Optional<DoorLock> doorLock = doorLockRepository.findById(dto.getDoorLockSeq());
        if (doorLock.isEmpty())
            return ApiResponse.ERROR(404, "알 수 없는 도어락입니다.");

        // 3. 넘어온 도어락 구분자값으로 등록된 사용자가 있는지 확인 (사용자가 없어야함.)
        List<RegistDoorLock> data = registDoorLockRepository.findByDoorLock_DoorLockSeq(dto.getDoorLockSeq());

        // 4. 사용자가 있을 경우, OWNER 로 등록 불가
        if (!data.isEmpty())
            return ApiResponse.FAILURE(401, "OWNER 로 이미 등록된 사용자가 있습니다.");

        // 5. 사용자가 없을 경우, OWNER 권한
        RegistDoorLock registDoorLock = RegistDoorLock.builder()
                .user(user.get())
                .doorLock(doorLock.get())
                .rdlName(dto.getRdlName())
                .rdlAuth(1)
                .rdlApprove(1)
                .build();
        registDoorLockRepository.save(registDoorLock);

        return ApiResponse.SUCCESS(
                "OWNER 등록이 완료되었습니다.",
                RegistDoorLockResponseDto.registNfc.builder()
                        .serialNo(registDoorLock.getDoorLock().getSerialNo())
                        .rdlName(registDoorLock.getRdlName())
                        .build());
    }

    /**
     * member, guest 초대 코드 생성
     * @param dto RegistDLRequestDto.inviteCode
     * @return RegistDLResponseDto.inviteCode
     */
    public ApiResponse<RegistDoorLockResponseDto.inviteCode> inviteCode(RegistDoorLockRequestDto.inviteCode dto){
        // 1. 해당 사용자가 해당 도어락의 OWNER 권한을 가진자가 맞는지 확인 (맞아야 함.)
        Optional<RegistDoorLock> registDoorLock = registDoorLockRepository.findById(dto.getRdlSeq());
        if(registDoorLock.isEmpty() || registDoorLock.get().getRdlAuth() != 1)
            return ApiResponse.ERROR(404, "해당 사용자는 owner 권한이 없습니다.");

        // 2. 초대 코드 생성
        String inviteCode = makeInviteCode();
        Optional<DoorLockInvite> inviteCodeEntity = findByInviteCode(inviteCode);
        while(inviteCodeEntity.isPresent()){
            inviteCode = makeInviteCode();
            inviteCodeEntity = findByInviteCode(inviteCode);
        }

        // 3. 생성된 코드 및 도어락 & user & 등록 만료시간과 함께 정보 저장.
        DoorLockInvite doorLockInvite = DoorLockInvite.builder()
                .inviteCode(inviteCode)
                .rdlAuth(dto.getGiveAuth())
                .useYn(0)
                .doorLock(registDoorLock.get().getDoorLock())
                .user(registDoorLock.get().getUser())
                .build();
        doorLockInviteRepository.save(doorLockInvite);

        // 4. inviteCode 반환 (초대코드는 생성 이후 5분까지만 유효)
        return ApiResponse.SUCCESS(
                "초대코드가 생성되었습니다.",
                RegistDoorLockResponseDto.inviteCode.builder()
                    .inviteCode(inviteCode)
                    .build());
    }

    /**
     * 랜덤 초대 코드 생성
     * @return String
     */
    private String makeInviteCode() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        int length = 10;
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 초대코드 조회 (생성 이후 5분까지만 유효)
     * @param inviteCode String
     * @return Optional<DoorLockInvite>
     */
    private Optional<DoorLockInvite> findByInviteCode(String inviteCode){
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(5);
        return doorLockInviteRepository.findByInviteCode(inviteCode, expireTime);
    }


    /**
     * 초대 코드 조회
     * @param inviteCode String
     * @return RegistDLResponseDto.searchInviteCode
     */
    @Transactional
    public ApiResponse<RegistDoorLockResponseDto.searchInviteCode> searchInviteCode(String inviteCode){
        Optional<DoorLockInvite> inviteCodeEntity = findByInviteCode(inviteCode);
        return inviteCodeEntity.map(
                doorLockInvite -> ApiResponse.SUCCESS("사용가능한 초대코드입니다.",
                        RegistDoorLockResponseDto.searchInviteCode.builder()
                                .inviteSeq(doorLockInvite.getInviteSeq())
                                .doorLockSeq(doorLockInvite.getDoorLock().getDoorLockSeq())
                                .build()))
                .orElseGet(() -> ApiResponse.FAILURE(401, "유효하지 않은 초대코드 입니다."));
    }


    /**
     * owner 권한 이외, NFC 등록 API
     * @param dto RegistDLRequestDto.registNfcOther
     * @param userSeq Long
     * @return RegistDLResponseDto.registNfcOther
     */
    @Transactional
    public ApiResponse<RegistDoorLockResponseDto.registNfcOther> registNfcOther(RegistDoorLockRequestDto.registNfcOther dto, Long userSeq){
        // 1. 넘어온 userSeq 데이터가 있는지 확인
        Optional<User> user = userRepository.findById(userSeq);
        if (user.isEmpty())
            return ApiResponse.ERROR(401, "알 수 없는 사용자입니다.");

        // 2. 넘어온 도어락 구분자값으로 등록된 도어락 정보가 있는지 확인
        Optional<DoorLock> doorLock = doorLockRepository.findById(dto.getDoorLockSeq());
        if (doorLock.isEmpty())
            return ApiResponse.ERROR(404, "알 수 없는 도어락입니다.");

        // 3. 초대 코드 조회를 통해 부여할 권한 확인
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(5);
        Optional<DoorLockInvite> inviteCode = doorLockInviteRepository.findByInviteSeq(dto.getInviteSeq(), expireTime);
        if (inviteCode.isEmpty())
            return ApiResponse.ERROR(401, "유효하지 않은 초대코드 입니다.");
        DoorLockInvite inviteCodeEntity = inviteCode.get();

        // 4. 넘어온 도어락 구분자값으로 등록된 사용자가 있는지 확인(사용자가 없다는 건 OWNER 권한자가 없다는 의미가 됨.)
        List<RegistDoorLock> data = registDoorLockRepository.findByDoorLock_DoorLockSeq(dto.getDoorLockSeq());
        if (data.isEmpty())
            return ApiResponse.ERROR(401, "OWNER 로 등록된 사용자가 없습니다.");

        // 5. 넘어온 도어락 구분자 및 사용자 구분자 코드로 등록된 데이터가 있는지 확인
        if(registDoorLockRepository.findByUser_UserSeqAndDoorLock_DoorLockSeq(userSeq, dto.getDoorLockSeq()).isPresent())
            return ApiResponse.ERROR(401, "이미 등록된 사용자입니다.");

        // 6. 초대 코드에 맞는 권한으로 NFC 데이터 저장
        RegistDoorLock registDoorLock = RegistDoorLock.builder()
                .user(user.get())
                .doorLock(doorLock.get())
                .rdlName(dto.getRdlName())
                .rdlAuth(inviteCodeEntity.getRdlAuth())
                .rdlApprove(0)
                .build();
        registDoorLockRepository.save(registDoorLock);

        // 7. 초대코드 사용처리
        inviteCodeEntity.usingCode();

        return ApiResponse.SUCCESS("등록되었습니다.",
                RegistDoorLockResponseDto.registNfcOther.builder()
                        .serialNo(registDoorLock.getDoorLock().getSerialNo())
                        .rdlName(registDoorLock.getRdlName())
                        .build());
    }
}
