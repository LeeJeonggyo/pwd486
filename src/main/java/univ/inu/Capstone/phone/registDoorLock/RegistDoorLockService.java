package univ.inu.Capstone.phone.registDoorLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import univ.inu.Capstone.common.dto.doorlock.DoorLockRequestDto;
import univ.inu.Capstone.common.dto.doorlock.DoorLockResponseDto;
import univ.inu.Capstone.common.dto.registDoorlock.RegistDLRequestDto;
import univ.inu.Capstone.common.dto.registDoorlock.RegistDLResponseDto;
import univ.inu.Capstone.common.entity.*;
import univ.inu.Capstone.common.repository.*;

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
     * @param dto DoorLockRequestDto.DoorLockBasic
     * @return DoorLockResponseDto.DoorLockResult
     */
    public DoorLockResponseDto.SaveMachine registMachine(DoorLockRequestDto.DoorLockBasic dto){
        String result = "SUCCESS";
        Optional<DoorLock> data = doorLockRepository.findBySerialNo(dto.getSerialNo());

        if (data.isPresent()) result = "FAIL";
        else {
            DoorLock doorLock = doorLockRepository.save(dto.toEntity());

            // 비밀번호 low 생성
            DoorLockSecret doorLockSecret = DoorLockSecret.builder()
                    .dlSecretNo("0000")
                    .doorLock(doorLock)
                    .build();
            doorLockSecretRespository.save(doorLockSecret);
        }

        return DoorLockResponseDto.SaveMachine.builder()
                .result(result)
                .build();
    }

    /**
     * 시리얼 넘버를 통한 도어락 검색
     * @param serialNo String
     * @return DoorLockResponseDto.SearchSerialNo
     */
    public DoorLockResponseDto.SearchSerialNo searchSerialNo(String serialNo){
        // 시리얼 넘버로 등록된 도어락 정보 확인
        Optional<DoorLock> data = doorLockRepository.findBySerialNo(serialNo);
        return DoorLockResponseDto.SearchSerialNo.builder()
                .doorLockSeq(data.map(DoorLock::getDoorLockSeq).orElse(null))
                .result(data.isPresent() ? "SUCCESS":"FAIL")
                .build();
    }

    /**
     * 사용자 도어락 NFC 등록 (owner)
     * @param dto RegistDLRequestDto.RegistDL
     * @param userSeq Long
     * @return RegistDLResponseDto.RegistDL
     */
    @Transactional
    public RegistDLResponseDto.RegistDL registDL(RegistDLRequestDto.RegistDL dto, Long userSeq){
        // 1. 넘어온 userSeq 데이터가 있는지 확인
        Optional<User> user = userRepository.findById(userSeq);
        if (user.isEmpty()) throw new RuntimeException("알 수 없는 사용자입니다.");

        // 2. 넘어온 도어락 구분자값으로 등록된 도어락 정보가 있는지 확인
        Optional<DoorLock> doorLock = doorLockRepository.findById(dto.getDoorLockSeq());
        if (doorLock.isEmpty()) throw new RuntimeException("알 수 없는 도어락입니다.");

        // 3. 넘어온 도어락 구분자값으로 등록된 사용자가 있는지 확인 (사용자가 없어야함.)
        List<RegistDoorLock> data = registDoorLockRepository.findByDoorLock_DoorLockSeq(dto.getDoorLockSeq());

        // 4. 사용자가 있을 경우, OWNER 로 등록 불가
        if (!data.isEmpty()) throw new RuntimeException("OWNER 로 이미 등록된 사용자가 있습니다.");

        // 5. 사용자가 없을 경우, OWNER 권한
        RegistDoorLock registDoorLock = RegistDoorLock.builder()
                .user(user.get())
                .doorLock(doorLock.get())
                .rdlName(dto.getRdlName())
                .rdlAuth(1)
                .rdlApprove(0)
                .build();
        registDoorLockRepository.save(registDoorLock);

        return RegistDLResponseDto.RegistDL.builder()
                .serialNo(registDoorLock.getDoorLock().getSerialNo())
                .rdlName(registDoorLock.getRdlName())
                .build();
    }

    /**
     * member, guest 초대 코드 생성
     * @param dto RegistDLRequestDto.inviteCode
     * @return RegistDLResponseDto.inviteCode
     */
    public RegistDLResponseDto.inviteCode inviteCode(RegistDLRequestDto.inviteCode dto){
        // 1. 해당 사용자가 해당 도어락의 OWNER 권한을 가진자가 맞는지 확인 (맞아야 함.)
        Optional<RegistDoorLock> registDoorLock = registDoorLockRepository.findById(dto.getRdlSeq());
        if(registDoorLock.isEmpty() || registDoorLock.get().getRdlAuth() != 1) throw new RuntimeException("해당 사용자는 owner 권한이 없습니다.");

        // 2. 초대 코드 생성
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(1);
        String inviteCode = makeInviteCode();
        log.info("inviteCode : {}",inviteCode);
        Optional<DoorLockInvite> inviteCodeEntity = doorLockInviteRepository.findByInviteCode(inviteCode, expireTime);

        while(inviteCodeEntity.isPresent()){
            inviteCode = makeInviteCode();
            log.info("inviteCode : {}",inviteCode);
            inviteCodeEntity = doorLockInviteRepository.findByInviteCode(inviteCode, expireTime);
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

        // 4. inviteCode 반환
        return RegistDLResponseDto.inviteCode.builder()
                .inviteCode(inviteCode)
                .build();
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
     * 초대 코드 조회
     * @param inviteCode String
     * @return RegistDLResponseDto.searchInviteCode
     */
    @Transactional
    public RegistDLResponseDto.searchInviteCode searchInviteCode(String inviteCode){
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(1);
        Optional<DoorLockInvite> inviteCodeEntity = doorLockInviteRepository.findByInviteCode(inviteCode, expireTime);
        if (inviteCodeEntity.isEmpty())
            return RegistDLResponseDto.searchInviteCode.builder()
                    .result("유효하지 않은 초대코드 입니다.")
                    .build();
        else return RegistDLResponseDto.searchInviteCode.builder()
                .result("사용간한 초대코드입니다.")
                .inviteSeq(inviteCodeEntity.get().getInviteSeq())
                .doorLockSeq(inviteCodeEntity.get().getDoorLock().getDoorLockSeq())
                .build();
    }


    /**
     * owner 권한 이외, NFC 등록 API
     * @param dto RegistDLRequestDto.registNfcOther
     * @param userSeq Long
     * @return RegistDLResponseDto.registNfcOther
     */
    @Transactional
    public RegistDLResponseDto.registNfcOther registNfcOther(RegistDLRequestDto.registNfcOther dto, Long userSeq){
        // 1. 넘어온 userSeq 데이터가 있는지 확인
        Optional<User> user = userRepository.findById(userSeq);
        if (user.isEmpty()) throw new RuntimeException("알 수 없는 사용자입니다.");

        // 2. 넘어온 도어락 구분자값으로 등록된 도어락 정보가 있는지 확인
        Optional<DoorLock> doorLock = doorLockRepository.findById(dto.getDoorLockSeq());
        if (doorLock.isEmpty()) throw new RuntimeException("알 수 없는 도어락입니다.");

        // 3. 초대 코드 조회를 통해 부여할 권한 확인
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(1);
        Optional<DoorLockInvite> inviteCode = doorLockInviteRepository.findByInviteSeq(dto.getInviteSeq(), expireTime);
        if (inviteCode.isEmpty()) throw new RuntimeException("유효하지 않은 초대코드 입니다.");
        DoorLockInvite inviteCodeEntity = inviteCode.get();

        // 4. 넘어온 도어락 구분자값으로 등록된 사용자가 있는지 확인(사용자가 없다는 건 OWNER 권한자가 없다는 의미가 됨.)
        List<RegistDoorLock> data = registDoorLockRepository.findByDoorLock_DoorLockSeq(dto.getDoorLockSeq());
        if (data.isEmpty()) throw new RuntimeException("OWNER 로 등록된 사용자가 없습니다.");

        // 5. 넘어온 도어락 구분자 및 사용자 구분자 코드로 등록된 데이터가 있는지 확인
        if(registDoorLockRepository.findByUser_UserSeqAndDoorLock_DoorLockSeq(userSeq, dto.getDoorLockSeq()).isPresent())
            throw new RuntimeException("이미 등록된 사용자입니다.");

        // 6. 초대 코드에 맞는 권한으로 NFC 데이터 저장
        RegistDoorLock registDoorLock = RegistDoorLock.builder()
                .user(user.get())
                .doorLock(doorLock.get())
                .rdlName(dto.getRdlName())
                .rdlAuth(inviteCodeEntity.getRdlAuth())
                .rdlApprove(1)
                .build();
        registDoorLockRepository.save(registDoorLock);

        // 7. 초대코드 사용처리
        inviteCodeEntity.usingCode();

        return RegistDLResponseDto.registNfcOther.builder()
                .serialNo(registDoorLock.getDoorLock().getSerialNo())
                .rdlName(registDoorLock.getRdlName())
                .build();
    }
}
