package cn.iocoder.yudao.module.school.dal.mysql.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.ExamRulePageReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.ExamRuleDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * 考试时间规则 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface ExamRuleMapper extends BaseMapperX<ExamRuleDO> {

    default PageResult<ExamRuleDO> selectPage(ExamRulePageReqVO reqVO) {
        LocalDate searchDate = reqVO.getDate();
        LocalDate startDate = searchDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = searchDate.with(TemporalAdjusters.lastDayOfMonth());

        return selectPage(reqVO, new LambdaQueryWrapperX<ExamRuleDO>()
                .or(i -> i.and(j -> j.le(ExamRuleDO::getStartDate, startDate).ge(ExamRuleDO::getEndDate, startDate))
                        .or(j -> j.le(ExamRuleDO::getStartDate, endDate).ge(ExamRuleDO::getEndDate, endDate)))
                .orderByDesc(ExamRuleDO::getId));
    }

    default List<ExamRuleDO> selectList(LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<ExamRuleDO>()
                .between(ExamRuleDO::getStartDate, startDate, endDate)
                .or()
                .between(ExamRuleDO::getEndDate, startDate, endDate));
    }
}