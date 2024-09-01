package cn.iocoder.yudao.module.school.timetable.domain;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.Set;

/**
 * 规划实体
 */
@Getter
@Setter
public class Lesson {

    private Long id;

    private SubjectDO subject;

    private TeacherDO teacher;

    private GradeDO grade;

    private CourseTypeDO courseType;

    private Set<Integer> preferWeeks;

    private Set<Long> preferTimeSlotIds;

    /**
     * 是否有连堂课
     */
    private boolean continuousFlag;


    private DayOfWeek dayOfWeek;

    private TimeSlotDO timeSlot;

    @Override
    public String toString() {
        return "班级：" + grade.getName() + "，教师：" + teacher.getName() + "，课程：" + subject.getName() + "，课程类型" + courseType.getName() + "，是否连堂课：" + continuousFlag;
    }
}