package cn.iocoder.yudao.module.school.dal.mysql.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.TransferRulePageReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.TransferRuleDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * 临时调课 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface TransferRuleMapper extends BaseMapperX<TransferRuleDO> {

    default PageResult<TransferRuleDO> selectPage(TransferRulePageReqVO reqVO) {
        LocalDate searchDate = reqVO.getDate();
        LocalDate startDate = searchDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = searchDate.with(TemporalAdjusters.lastDayOfMonth());

        return selectPage(reqVO, new LambdaQueryWrapperX<TransferRuleDO>()
                .between(TransferRuleDO::getDate, startDate, endDate)
                .orderByDesc(TransferRuleDO::getId));
    }

    default TransferRuleDO selectOne(LocalDate date, Long gradeId, Long timeSlotId) {
        return selectOne(TransferRuleDO::getDate, date, TransferRuleDO::getGradeId, gradeId, TransferRuleDO::getTimeSlotId, timeSlotId);
    }
}