package univ.inu.Capstone.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import univ.inu.Capstone.common.entity.TaglessTime;

import java.util.List;

public interface TaglessTimeRepository extends JpaRepository<TaglessTime, Long> {
    @Query( value = "SELECT " +
            "    b.maxCnt as max_cnt, " +
            "    c._day as tagless_day, " +
            "    min(c._time) as tagless_time, " +
            "    c.door_lock_seq as door_lock_seq " +
            "FROM ( " +
            "    SELECT " +
            "        max(a._cnt) as maxCnt " +
            "        , a._day " +
            "        , a.door_lock_seq " +
            "    FROM ( " +
            "        SELECT " +
            "            count(*) as '_cnt', " +
            "            dayofweek(inp_date) as '_day', " +
            "            CASE  " +
            "                WHEN date_format(inp_date, '%i') < 15 THEN date_format(inp_date, '%H') - 0.5 " +
            "                WHEN date_format(inp_date, '%i') < 30 THEN date_format(inp_date, '%H') - 0.25 " +
            "                WHEN date_format(inp_date, '%i') < 45 THEN date_format(inp_date, '%H') " +
            "                ELSE date_format(inp_date, '%H') + 0.25 " +
            "            END as '_time', " +
            "            door_lock_seq " +
            "        FROM open_log ol " +
            "        WHERE open_yn = 1 " +
            "        AND door_lock_seq = :doorLockSeq " +
            "        GROUP BY _day, _time, door_lock_seq " +
            "    ) a " +
            "    GROUP BY a._day, a.door_lock_seq " +
            ") b " +
            "INNER JOIN ( " +
            "    SELECT " +
            "        count(*) as '_cnt', " +
            "        dayofweek(inp_date) as '_day', " +
            "        CASE  " +
            "            WHEN date_format(inp_date, '%i') < 15 THEN date_format(inp_date, '%H') - 0.5 " +
            "            WHEN date_format(inp_date, '%i') < 30 THEN date_format(inp_date, '%H') - 0.25 " +
            "            WHEN date_format(inp_date, '%i') < 45 THEN date_format(inp_date, '%H') " +
            "            ELSE date_format(inp_date, '%H') + 0.25 " +
            "        END as '_time', " +
            "        door_lock_seq " +
            "    FROM open_log ol " +
            "    WHERE open_yn = 1 " +
            "    AND door_lock_seq = :doorLockSeq " +
            "    GROUP BY _day, _time, door_lock_seq " +
            ") c ON b.maxCnt = c._cnt and b._day = c._day and b.door_lock_seq = c.door_lock_seq " +
            "group by tagless_day, door_lock_seq", nativeQuery = true)
    List<Object[]> findDtoForBatch(@Param("doorLockSeq") Long doorLockSeq);
}
