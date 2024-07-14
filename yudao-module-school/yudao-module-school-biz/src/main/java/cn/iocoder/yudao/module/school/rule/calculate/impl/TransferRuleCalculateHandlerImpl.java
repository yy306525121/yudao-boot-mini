package cn.iocoder.yudao.module.school.rule.calculate.impl;

import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import cn.iocoder.yudao.module.school.rule.calculate.RuleCalculateHandler;
import cn.iocoder.yudao.module.school.service.rule.TransferRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Order(40)
@RequiredArgsConstructor
public class TransferRuleCalculateHandlerImpl implements RuleCalculateHandler {
    private final TransferRuleService transferRuleService;

    @Override
    public List<CourseFeeDO> handleCourseFee(List<CourseFeeDO> courseFeeList, LocalDate startDate, LocalDate endDate) {
        return courseFeeList;
    }
}
