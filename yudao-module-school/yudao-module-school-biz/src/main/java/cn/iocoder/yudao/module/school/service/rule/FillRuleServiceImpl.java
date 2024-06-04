package cn.iocoder.yudao.module.school.service.rule;

import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.mysql.course.TimeSlotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.*;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.FillRuleDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.school.dal.mysql.rule.FillRuleMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.*;

/**
 * 补课规则 Service 实现类
 *
 * @author yangzy
 */
@Service
@Validated
@RequiredArgsConstructor
public class FillRuleServiceImpl implements FillRuleService {

    private final FillRuleMapper fillRuleMapper;
    private final TimeSlotMapper timeSlotMapper;

    @Override
    public Long createFillRule(FillRuleSaveReqVO createReqVO) {
        validateFillRuleParams(createReqVO);
        // 插入
        FillRuleDO fillRule = BeanUtils.toBean(createReqVO, FillRuleDO.class);
        fillRuleMapper.insert(fillRule);
        // 返回
        return fillRule.getId();
    }



    @Override
    public void updateFillRule(FillRuleSaveReqVO updateReqVO) {
        validateFillRuleParams(updateReqVO);
        // 校验存在
        validateFillRuleExists(updateReqVO.getId());
        // 更新
        FillRuleDO updateObj = BeanUtils.toBean(updateReqVO, FillRuleDO.class);
        fillRuleMapper.updateById(updateObj);
    }

    @Override
    public void deleteFillRule(Long id) {
        // 校验存在
        validateFillRuleExists(id);
        // 删除
        fillRuleMapper.deleteById(id);
    }


    @Override
    public FillRuleDO getFillRule(Long id) {
        return fillRuleMapper.selectById(id);
    }

    @Override
    public PageResult<FillRuleDO> getFillRulePage(FillRulePageReqVO pageReqVO) {
        return fillRuleMapper.selectPage(pageReqVO);
    }


    private void validateFillRuleExists(Long id) {
        if (fillRuleMapper.selectById(id) == null) {
            throw exception(FILL_RULE_NOT_EXISTS);
        }
    }

    private void validateFillRuleParams(FillRuleSaveReqVO reqVO) {
        TimeSlotDO startTimeSlot = timeSlotMapper.selectById(reqVO.getStartTimeSlotId());
        TimeSlotDO endTimeSlot = timeSlotMapper.selectById(reqVO.getEndTimeSlotId());
        if (startTimeSlot.getSort() >= endTimeSlot.getSort()) {
            throw exception(FILL_RULE_PARAM_ERROR);
        }
    }
}