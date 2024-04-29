package univ.inu.Capstone.common.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import univ.inu.Capstone.common.entity.DoorLock;
import univ.inu.Capstone.common.entity.TaglessTime;
import univ.inu.Capstone.common.repository.DoorLockRepository;
import univ.inu.Capstone.common.repository.TaglessTimeRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaglessConfig {

    private final DoorLockRepository doorLockRepository;
    private final TaglessTimeRepository taglessTimeRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 ? * MON")
    public void forTagless(){
        // 1. ai 서비스 동의 도어락 리스트 조회
        List<DoorLock> doorLockList = doorLockRepository.findByAiYn(1);

        for (DoorLock entity : doorLockList) {
            // 2. 각 도어락 별 데이터 조회
            TaglessTime taglessTime = entity.getTaglessTime();

            // 3. 각 요일별 최빈값 도출 및 update
            List<Object[]> taglessList = taglessTimeRepository.findDtoForBatch(entity.getDoorLockSeq());
            taglessTime.updateTaglessTime(taglessList);
        }
    }
}
