package cn.iocoder.yudao.module.school.dal.mysql.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.HolidayRulePageReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.HolidayRuleDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 放假时间规则 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface HolidayRuleMapper extends BaseMapperX<HolidayRuleDO> {

    default PageResult<HolidayRuleDO> selectPage(HolidayRulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HolidayRuleDO>()
                .eqIfPresent(HolidayRuleDO::getGradeId, reqVO.getGradeId())
                .betweenIfPresent(HolidayRuleDO::getStartDate, reqVO.getStartDate())
                .eqIfPresent(HolidayRuleDO::getStartTimeSlotId, reqVO.getStartTimeSlotId())
                .betweenIfPresent(HolidayRuleDO::getEndDate, reqVO.getEndDate())
                .eqIfPresent(HolidayRuleDO::getEndTimeSlotId, reqVO.getEndTimeSlotId())
                .betweenIfPresent(HolidayRuleDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(HolidayRuleDO::getId));
    }

}