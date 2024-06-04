package cn.iocoder.yudao.module.school.service.rule;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.*;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.ExamRuleDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.school.dal.mysql.rule.ExamRuleMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.*;

/**
 * 考试时间规则 Service 实现类
 *
 * @author yangzy
 */
@Service
@Validated
public class ExamRuleServiceImpl implements ExamRuleService {

    @Resource
    private ExamRuleMapper examRuleMapper;

    @Override
    public Long createExamRule(ExamRuleSaveReqVO createReqVO) {
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

    private void validateExamRuleExists(Long id) {
        if (examRuleMapper.selectById(id) == null) {
            throw exception(EXAM_RULE_NOT_EXISTS);
        }
    }

    @Override
    public ExamRuleDO getExamRule(Long id) {
        return examRuleMapper.selectById(id);
    }

    @Override
    public PageResult<ExamRuleDO> getExamRulePage(ExamRulePageReqVO pageReqVO) {
        return examRuleMapper.selectPage(pageReqVO);
    }

}