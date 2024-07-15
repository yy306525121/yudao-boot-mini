package cn.iocoder.yudao.module.school.optplanner.domain;

import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import lombok.Data;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.DayOfWeek;

/**
 * 规划实体
 */
@Data
@PlanningEntity
public class CoursePlanOpt {


    @PlanningId
    private Long id;

    private SubjectDO subject;

    private TeacherDO teacher;

    @PlanningVariable
    private DayOfWeek dayOfWeek;

    @PlanningVariable
    private TimeSlotDO timeSlot;

    private GradeDO grade;
}
