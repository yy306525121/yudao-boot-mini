package cn.iocoder.yudao.module.school.timefold.domain;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.List;

@Data
@PlanningSolution
public class TimeTable {
    /**
     * 问题事实
     */
    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<TimeSlotDO> timeSlotList;

    /**
     * 问题事实
     */
    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<DayOfWeek> dayOfWeekList;

    @PlanningEntityCollectionProperty
    private List<Lesson> lessonList;

    @PlanningScore
    private HardSoftScore score;

}
