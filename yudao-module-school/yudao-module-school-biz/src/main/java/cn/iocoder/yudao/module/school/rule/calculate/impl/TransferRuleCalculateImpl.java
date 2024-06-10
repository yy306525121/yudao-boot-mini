package cn.iocoder.yudao.module.school.rule.calculate.impl;

import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import cn.iocoder.yudao.module.school.rule.calculate.BaseRuleCalculate;
import cn.iocoder.yudao.module.school.service.rule.TransferRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Order(40)
@RequiredArgsConstructor
public class TransferRuleCalculateImpl implements BaseRuleCalculate {
    private final TransferRuleService transferRuleService;

    @Override
    public List<CourseFeeDO> handleCourseFee(List<CourseFeeDO> courseFeeList, LocalDate startDate, LocalDate endDate) {
        return List.of();
    }
}
