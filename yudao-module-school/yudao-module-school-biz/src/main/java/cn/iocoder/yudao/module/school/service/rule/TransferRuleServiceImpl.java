package cn.iocoder.yudao.module.school.service.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.TransferRulePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.TransferRuleSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.TransferRuleDO;
import cn.iocoder.yudao.module.school.dal.mysql.rule.TransferRuleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.TRANSFER_RULE_NOT_EXISTS;

/**
 * 临时调课 Service 实现类
 *
 * @author yangzy
 */
@Service
@Validated
public class TransferRuleServiceImpl implements TransferRuleService {

    @Resource
    private TransferRuleMapper transferRuleMapper;

    @Override
    public Long createTransferRule(TransferRuleSaveReqVO createReqVO) {
        // 插入
        TransferRuleDO transferRule = BeanUtils.toBean(createReqVO, TransferRuleDO.class);
        transferRuleMapper.insert(transferRule);
        // 返回
        return transferRule.getId();
    }

    @Override
    public void updateTransferRule(TransferRuleSaveReqVO updateReqVO) {
        // 校验存在
        validateTransferRuleExists(updateReqVO.getId());
        // 更新
        TransferRuleDO updateObj = BeanUtils.toBean(updateReqVO, TransferRuleDO.class);
        transferRuleMapper.updateById(updateObj);
    }

    @Override
    public void deleteTransferRule(Long id) {
        // 校验存在
        validateTransferRuleExists(id);
        // 删除
        transferRuleMapper.deleteById(id);
    }

    private void validateTransferRuleExists(Long id) {
        if (transferRuleMapper.selectById(id) == null) {
            throw exception(TRANSFER_RULE_NOT_EXISTS);
        }
    }

    @Override
    public TransferRuleDO getTransferRule(Long id) {
        return transferRuleMapper.selectById(id);
    }

    @Override
    public PageResult<TransferRuleDO> getTransferRulePage(TransferRulePageReqVO pageReqVO) {
        return transferRuleMapper.selectPage(pageReqVO);
    }

}