package cn.iocoder.yudao.module.school.calculate;

import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CoursePlanDO;

public interface CourseFeeCalculate {

    boolean support(CoursePlanDO coursePlan);

    CourseFeeDO calculate(CoursePlanDO coursePlan);
}
