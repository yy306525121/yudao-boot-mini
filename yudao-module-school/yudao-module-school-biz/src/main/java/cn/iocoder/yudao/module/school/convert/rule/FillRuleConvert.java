package cn.iocoder.yudao.module.school.convert.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeRespVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.FillRulePageRespVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.HolidayRulePageRespVO;
import cn.iocoder.yudao.module.school.convert.course.TimeSlotConvert;
import cn.iocoder.yudao.module.school.convert.grade.GradeConvert;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.FillRuleDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * @author yangzy
 */
@Mapper
public interface FillRuleConvert {
    FillRuleConvert INSTANCE = Mappers.getMapper(FillRuleConvert.class);

    default FillRulePageRespVO convert(FillRuleDO fillRule,
                                       List<GradeDO> gradeList,
                                       List<TimeSlotDO> timeSlotList) {
        Map<Long, GradeDO> gradeMap = convertMap(gradeList, GradeDO::getId);
        Map<Long, TimeSlotDO> timeSlotMap = convertMap(timeSlotList, TimeSlotDO::getId);

        FillRulePageRespVO respVO = BeanUtils.toBean(fillRule, FillRulePageRespVO.class);

        List<GradeRespVO> gradeRespVOList = new ArrayList<>();
        for (Long gradeId : fillRule.getGradeIds()) {
            GradeDO grade = gradeMap.get(gradeId);
            gradeRespVOList.add(GradeConvert.INSTANCE.convert(grade));
        }
        respVO.setGradeList(gradeRespVOList);

        TimeSlotDO startTimeSlot = timeSlotMap.get(fillRule.getStartTimeSlotId());
        respVO.setStartTimeSlot(TimeSlotConvert.INSTANCE.convert(startTimeSlot));

        TimeSlotDO endTimeSlot = timeSlotMap.get(fillRule.getEndTimeSlotId());
        respVO.setEndTimeSlot(TimeSlotConvert.INSTANCE.convert(endTimeSlot));

        return respVO;
    }

    default PageResult<FillRulePageRespVO> convertPage(PageResult<FillRuleDO> page,
                                                       List<GradeDO> gradeList,
                                                       List<TimeSlotDO> timeSlotList) {
        PageResult<FillRulePageRespVO> pageResult = new PageResult<>();

        List<FillRulePageRespVO> respVOList = new ArrayList<>();
        for (FillRuleDO fillRule : page.getList()) {
            FillRulePageRespVO respVO = convert(fillRule, gradeList, timeSlotList);
            respVOList.add(respVO);
        }

        pageResult.setList(respVOList);
        pageResult.setTotal(page.getTotal());

        return pageResult;
    }
}
