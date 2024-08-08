package cn.iocoder.yudao.module.school.timetabling.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import lombok.Data;

import java.time.DayOfWeek;

/**
 * 规划实体
 */
@Data
@PlanningEntity
public class Lesson {

    @PlanningId
    private Long id;

    private SubjectDO subject;

    private TeacherDO teacher;

    private GradeDO grade;

    @PlanningVariable
    private DayOfWeek dayOfWeek;

    @PlanningVariable
    private TimeSlotDO timeSlot;
}
