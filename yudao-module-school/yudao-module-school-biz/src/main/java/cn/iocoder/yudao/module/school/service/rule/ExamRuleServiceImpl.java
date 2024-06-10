package cn.iocoder.yudao.module.school.service.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.ExamRulePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.ExamRuleSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.ExamRuleDO;
import cn.iocoder.yudao.module.school.dal.mysql.course.TimeSlotMapper;
import cn.iocoder.yudao.module.school.dal.mysql.rule.ExamRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.EXAM_RULE_NOT_EXISTS;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.EXAM_RULE_PARAM_ERROR;

/**
 * 考试时间规则 Service 实现类
 *
 * @author yangzy
 */
@Service
@Validated
@RequiredArgsConstructor
public class ExamRuleServiceImpl implements ExamRuleService {

    private final ExamRuleMapper examRuleMapper;
    private final TimeSlotMapper timeSlotMapper;

    @Override
    public Long createExamRule(ExamRuleSaveReqVO createReqVO) {
        validateExamRuleParams(createReqVO);
        // 插入
        ExamRuleDO examRule = BeanUtils.toBean(createReqVO, ExamRuleDO.class);
        examRuleMapper.insert(examRule);
        // 返回
        return examRule.getId();
    }

    @Override
    public void updateExamRule(ExamRuleSaveReqVO updateReqVO) {
        // 校验存在
        validateExamRuleExists(updateReqVO.getId());
        // 校验参数是否正确
        validateExamRuleParams(updateReqVO);
        // 更新
        ExamRuleDO updateObj = BeanUtils.toBean(updateReqVO, ExamRuleDO.class);
        examRuleMapper.updateById(updateObj);
    }

    @Override
    public void deleteExamRule(Long id) {
        // 校验存在
        validateExamRuleExists(id);
        // 删除
        examRuleMapper.deleteById(id);
    }

    @Override
    public ExamRuleDO getExamRule(Long id) {
        return examRuleMapper.selectById(id);
    }

    @Override
    public PageResult<ExamRuleDO> getExamRulePage(ExamRulePageReqVO pageReqVO) {
        return examRuleMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ExamRuleDO> getExamRuleList(LocalDate startDate, LocalDate endDate) {
        return examRuleMapper.selectList(startDate, endDate);
    }

    private void validateExamRuleExists(Long id) {
        if (examRuleMapper.selectById(id) == null) {
            throw exception(EXAM_RULE_NOT_EXISTS);
        }
    }

    private void validateExamRuleParams(ExamRuleSaveReqVO reqVO) {
        LocalDate startDate = reqVO.getStartDate();
        LocalDate endDate = reqVO.getEndDate();
        Long startTimeSlotId = reqVO.getStartTimeSlotId();
        Long endTimeSlotId = reqVO.getEndTimeSlotId();

        if (startDate.isAfter(endDate)) {
            throw exception(EXAM_RULE_PARAM_ERROR);
        }

        TimeSlotDO startTimeSlot = timeSlotMapper.selectById(startTimeSlotId);
        TimeSlotDO endTimeSlot = timeSlotMapper.selectById(endTimeSlotId);
        if (startDate.isEqual(endDate) && startTimeSlot.getSort().compareTo(endTimeSlot.getSort()) <= 0) {
            throw exception(EXAM_RULE_PARAM_ERROR);
        }
    }


}