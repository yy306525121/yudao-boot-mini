package cn.iocoder.yudao.module.school.rule.calculate;

import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;

import java.time.LocalDate;
import java.util.List;

public interface RuleCalculateHandler {

    List<CourseFeeDO> handleCourseFee(List<CourseFeeDO> courseFeeList, LocalDate startDate, LocalDate endDate);
}
