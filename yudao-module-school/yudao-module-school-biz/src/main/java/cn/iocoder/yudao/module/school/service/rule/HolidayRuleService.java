package cn.iocoder.yudao.module.school.service.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.HolidayRulePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.HolidayRuleSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.HolidayRuleDO;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

/**
 * 放假时间规则 Service 接口
 *
 * @author yangzy
 */
public interface HolidayRuleService {

    /**
     * 创建放假时间规则
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createHolidayRule(@Valid HolidayRuleSaveReqVO createReqVO);

    /**
     * 更新放假时间规则
     *
     * @param updateReqVO 更新信息
     */
    void updateHolidayRule(@Valid HolidayRuleSaveReqVO updateReqVO);

    /**
     * 删除放假时间规则
     *
     * @param id 编号
     */
    void deleteHolidayRule(Long id);

    /**
     * 获得放假时间规则
     *
     * @param id 编号
     * @return 放假时间规则
     */
    HolidayRuleDO getHolidayRule(Long id);

    /**
     * 获得放假时间规则分页
     *
     * @param pageReqVO 分页查询
     * @return 放假时间规则分页
     */
    PageResult<HolidayRuleDO> getHolidayRulePage(HolidayRulePageReqVO pageReqVO);

    /**
     * 根据时间
     * @param startDate
     * @param endDate
     * @return
     */
    List<HolidayRuleDO> getHolidayRuleList(LocalDate startDate, LocalDate endDate);
}