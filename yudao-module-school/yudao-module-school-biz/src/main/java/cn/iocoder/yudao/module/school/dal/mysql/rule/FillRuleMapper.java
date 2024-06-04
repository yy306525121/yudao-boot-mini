package cn.iocoder.yudao.module.school.dal.mysql.rule;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.FillRuleDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.*;

/**
 * 补课规则 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface FillRuleMapper extends BaseMapperX<FillRuleDO> {

    default PageResult<FillRuleDO> selectPage(FillRulePageReqVO reqVO) {
        LocalDate searchDate = reqVO.getDate();
        LocalDate startDate = searchDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = searchDate.with(TemporalAdjusters.lastDayOfMonth());

        return selectPage(reqVO, new LambdaQueryWrapperX<FillRuleDO>()
                .between(FillRuleDO::getDate, startDate, endDate)
                .orderByDesc(FillRuleDO::getId));
    }

}