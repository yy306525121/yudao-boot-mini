package cn.iocoder.yudao.module.school.service.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.ExamRulePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.ExamRuleSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.ExamRuleDO;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

/**
 * 考试时间规则 Service 接口
 *
 * @author yangzy
 */
public interface ExamRuleService {

    /**
     * 创建考试时间规则
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createExamRule(@Valid ExamRuleSaveReqVO createReqVO);

    /**
     * 更新考试时间规则
     *
     * @param updateReqVO 更新信息
     */
    void updateExamRule(@Valid ExamRuleSaveReqVO updateReqVO);

    /**
     * 删除考试时间规则
     *
     * @param id 编号
     */
    void deleteExamRule(Long id);

    /**
     * 获得考试时间规则
     *
     * @param id 编号
     * @return 考试时间规则
     */
    ExamRuleDO getExamRule(Long id);

    /**
     * 获得考试时间规则分页
     *
     * @param pageReqVO 分页查询
     * @return 考试时间规则分页
     */
    PageResult<ExamRuleDO> getExamRulePage(ExamRulePageReqVO pageReqVO);

    List<ExamRuleDO> getExamRuleList(LocalDate startDate, LocalDate endDate);
}