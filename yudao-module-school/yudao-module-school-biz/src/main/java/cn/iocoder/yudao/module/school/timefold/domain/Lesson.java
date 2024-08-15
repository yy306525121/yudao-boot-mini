package cn.iocoder.yudao.module.school.timefold.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.Set;

/**
 * 规划实体
 */
@Data
@PlanningEntity
public class Lesson {

    @PlanningId
    private String id;

    private SubjectDO subject;

    private TeacherDO teacher;

    private GradeDO grade;

    private CourseTypeDO courseType;

    private Set<Integer> preferWeeks;
    private Set<Long> preferTimeSlotIds;

    /**
     * 是否有连堂课
     */
    private Boolean continuousFlag;

    /**
     * 连堂课绑定UUID,UUID一样的课程必须在一起
     */
    private String continuousUuid;



    @PlanningVariable
    private DayOfWeek dayOfWeek;

    @PlanningVariable
    private TimeSlotDO timeSlot;
}
