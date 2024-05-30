package cn.iocoder.yudao.module.school.service.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.TransferRulePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.TransferRuleSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.TransferRuleDO;
import jakarta.validation.Valid;

/**
 * 临时调课 Service 接口
 *
 * @author yangzy
 */
public interface TransferRuleService {

    /**
     * 创建临时调课
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTransferRule(@Valid TransferRuleSaveReqVO createReqVO);

    /**
     * 更新临时调课
     *
     * @param updateReqVO 更新信息
     */
    void updateTransferRule(@Valid TransferRuleSaveReqVO updateReqVO);

    /**
     * 删除临时调课
     *
     * @param id 编号
     */
    void deleteTransferRule(Long id);

    /**
     * 获得临时调课
     *
     * @param id 编号
     * @return 临时调课
     */
    TransferRuleDO getTransferRule(Long id);

    /**
     * 获得临时调课分页
     *
     * @param pageReqVO 分页查询
     * @return 临时调课分页
     */
    PageResult<TransferRuleDO> getTransferRulePage(TransferRulePageReqVO pageReqVO);

}