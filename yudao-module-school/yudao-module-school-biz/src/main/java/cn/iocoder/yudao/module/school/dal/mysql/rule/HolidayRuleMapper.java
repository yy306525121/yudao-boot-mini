package cn.iocoder.yudao.module.school.dal.mysql.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.HolidayRulePageReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.HolidayRuleDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * 放假时间规则 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface HolidayRuleMapper extends BaseMapperX<HolidayRuleDO> {

    default PageResult<HolidayRuleDO> selectPage(HolidayRulePageReqVO reqVO) {
        LocalDate searchDate = reqVO.getDate();
        LocalDate startDate = searchDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = searchDate.with(TemporalAdjusters.lastDayOfMonth());

        return selectPage(reqVO, new LambdaQueryWrapperX<HolidayRuleDO>()
                .between(HolidayRuleDO::getStartDate, startDate, endDate)
                .or()
                .between(HolidayRuleDO::getEndDate, startDate, endDate)
                .orderByDesc(HolidayRuleDO::getId));
    }

    default List<HolidayRuleDO> selectList(LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<HolidayRuleDO>()
                .between(HolidayRuleDO::getStartDate, startDate, endDate)
                .or()
                .between(HolidayRuleDO::getEndDate, startDate, endDate));
    }
}