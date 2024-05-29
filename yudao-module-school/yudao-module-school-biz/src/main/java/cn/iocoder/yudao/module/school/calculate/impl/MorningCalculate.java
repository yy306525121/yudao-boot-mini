package cn.iocoder.yudao.module.school.calculate.impl;

import cn.iocoder.yudao.module.school.calculate.CourseFeeCalculate;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CoursePlanDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.enums.course.CourseTypeEnum;
import cn.iocoder.yudao.module.school.service.course.CourseTypeService;
import cn.iocoder.yudao.module.school.service.teacher.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 早自习课时费计算
 */
@Order(10)
@Component
@RequiredArgsConstructor
public class MorningCalculate implements CourseFeeCalculate {
    private final CourseTypeService courseTypeService;
    private final TeacherService teacherService;

    @Override
    public boolean support(CoursePlanDO coursePlan) {
        Long courseTypeId = coursePlan.getCourseTypeId();
        CourseTypeDO courseType = courseTypeService.getCourseType(courseTypeId);
        return courseType.getType().equals(CourseTypeEnum.MORNING.getType());
    }

    @Override
    public CourseFeeDO calculate(CoursePlanDO coursePlan) {
        CourseTypeDO courseType = courseTypeService.getCourseType(coursePlan.getCourseTypeId());
        Long subjectId = coursePlan.getSubjectId();
        List<TeacherDO> teacherList = teacherService.getTeacherListBySubjectIds(Collections.singletonList(subjectId));

        for (TeacherDO teacherDO : teacherList) {

        }
        CourseFeeDO courseFee = new CourseFeeDO();
        courseFee.setCount(courseType.getNum());

        return null;
    }
}
