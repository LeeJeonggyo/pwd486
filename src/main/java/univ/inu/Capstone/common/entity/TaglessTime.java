package univ.inu.Capstone.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TaglessTime extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taglessSeq;   // 구분자

    private String monTime;     // 월 태그리스 시간
    private String tueTime;     // 화 태그리스 시간
    private String wedTime;     // 수 태그리스 시간
    private String thuTime;     // 목 태그리스 시간
    private String friTime;     // 금 태그리스 시간
    private String satTime;     // 토 태그리스 시간
    private String sunTime;     // 일 태그리스 시간

    @OneToOne
    @JoinColumn(name="doorLockSeq")
    private DoorLock doorLock;  // 도어락 테이블 조인

    /* =======================================================
     * update
     * ======================================================= */
    public void updateTaglessTime(List<Object[]> list){
        for (Object[] entity: list){
            int taglessDay = (int) entity[1];
            String taglessTime = (String) entity[2];
            switch (taglessDay){
                case 1 :
                    this.sunTime = taglessTime;
                    break;
                case 2 :
                    this.monTime = taglessTime;
                    break;
                case 3 :
                    this.tueTime = taglessTime;
                    break;
                case 4 :
                    this.wedTime = taglessTime;
                    break;
                case 5 :
                    this.thuTime = taglessTime;
                    break;
                case 6 :
                    this.friTime = taglessTime;
                    break;
                case 7 :
                    this.satTime = taglessTime;
                    break;
            }
        }
    }
}
