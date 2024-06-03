package cn.iocoder.yudao.module.school.service.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.HolidayRulePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.HolidayRuleSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.HolidayRuleDO;
import cn.iocoder.yudao.module.school.dal.mysql.rule.HolidayRuleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.HOLIDAY_RULE_NOT_EXISTS;


/**
 * 放假时间规则 Service 实现类
 *
 * @author yangzy
 */
@Service
@Validated
public class HolidayRuleServiceImpl implements HolidayRuleService {

    @Resource
    private HolidayRuleMapper holidayRuleMapper;

    @Override
    public Long createHolidayRule(HolidayRuleSaveReqVO createReqVO) {
        // 插入
        HolidayRuleDO holidayRule = BeanUtils.toBean(createReqVO, HolidayRuleDO.class);
        holidayRuleMapper.insert(holidayRule);
        // 返回
        return holidayRule.getId();
    }

    @Override
    public void updateHolidayRule(HolidayRuleSaveReqVO updateReqVO) {
        // 校验存在
        validateHolidayRuleExists(updateReqVO.getId());
        // 更新
        HolidayRuleDO updateObj = BeanUtils.toBean(updateReqVO, HolidayRuleDO.class);
        holidayRuleMapper.updateById(updateObj);
    }

    @Override
    public void deleteHolidayRule(Long id) {
        // 校验存在
        validateHolidayRuleExists(id);
        // 删除
        holidayRuleMapper.deleteById(id);
    }

    private void validateHolidayRuleExists(Long id) {
        if (holidayRuleMapper.selectById(id) == null) {
            throw exception(HOLIDAY_RULE_NOT_EXISTS);
        }
    }

    @Override
    public HolidayRuleDO getHolidayRule(Long id) {
        return holidayRuleMapper.selectById(id);
    }

    @Override
    public PageResult<HolidayRuleDO> getHolidayRulePage(HolidayRulePageReqVO pageReqVO) {
        return holidayRuleMapper.selectPage(pageReqVO);
    }

}