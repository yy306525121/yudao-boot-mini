package cn.iocoder.yudao.module.school.optplanner.domain;

import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import lombok.Data;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.time.DayOfWeek;
import java.util.List;

@Data
@PlanningSolution
public class TimeTableOpt {
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
    private List<CoursePlanOpt> coursePlanOptList;

    @PlanningScore
    private HardSoftScore score;

}
