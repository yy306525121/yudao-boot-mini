package cn.iocoder.yudao.module.school.service.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.FillRulePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.FillRuleSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.FillRuleDO;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

/**
 * 补课规则 Service 接口
 *
 * @author yangzy
 */
public interface FillRuleService {

    /**
     * 创建补课规则
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createFillRule(@Valid FillRuleSaveReqVO createReqVO);

    /**
     * 更新补课规则
     *
     * @param updateReqVO 更新信息
     */
    void updateFillRule(@Valid FillRuleSaveReqVO updateReqVO);

    /**
     * 删除补课规则
     *
     * @param id 编号
     */
    void deleteFillRule(Long id);

    /**
     * 获得补课规则
     *
     * @param id 编号
     * @return 补课规则
     */
    FillRuleDO getFillRule(Long id);

    /**
     * 获得补课规则分页
     *
     * @param pageReqVO 分页查询
     * @return 补课规则分页
     */
    PageResult<FillRuleDO> getFillRulePage(FillRulePageReqVO pageReqVO);

    List<FillRuleDO> getFillRuleList(LocalDate startDate, LocalDate endDate);
}