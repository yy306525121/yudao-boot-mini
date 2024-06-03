package cn.iocoder.yudao.module.school.convert.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeRespVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.HolidayRulePageRespVO;
import cn.iocoder.yudao.module.school.convert.course.TimeSlotConvert;
import cn.iocoder.yudao.module.school.convert.grade.GradeConvert;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.HolidayRuleDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

@Mapper
public interface HolidayRuleConvert {
    HolidayRuleConvert INSTANCE = Mappers.getMapper(HolidayRuleConvert.class);

    default HolidayRulePageRespVO convert(HolidayRuleDO holidayRule,
                                          List<GradeDO> gradeList,
                                          List<TimeSlotDO> timeSlotList) {
        Map<Long, GradeDO> gradeMap = convertMap(gradeList, GradeDO::getId);
        Map<Long, TimeSlotDO> timeSlotMap = convertMap(timeSlotList, TimeSlotDO::getId);

        HolidayRulePageRespVO respVO = new HolidayRulePageRespVO();
        respVO.setId(holidayRule.getId());
        respVO.setStartDate(holidayRule.getStartDate());
        respVO.setEndDate(holidayRule.getEndDate());

        Set<Long> gradeIds = holidayRule.getGradeIds();
        List<GradeRespVO> gradeRespVOList = new ArrayList<>();
        for (Long gradeId : gradeIds) {
            GradeDO grade = gradeMap.get(gradeId);
            gradeRespVOList.add(GradeConvert.INSTANCE.convert(grade));
        }
        respVO.setGradeList(gradeRespVOList);


        TimeSlotDO startTimeSlot = timeSlotMap.get(holidayRule.getStartTimeSlotId());
        respVO.setStartTimeSlot(TimeSlotConvert.INSTANCE.convert(startTimeSlot));

        TimeSlotDO endTimeSlot = timeSlotMap.get(holidayRule.getEndTimeSlotId());
        respVO.setEndTimeSlot(TimeSlotConvert.INSTANCE.convert(endTimeSlot));

        return respVO;
    }


    default PageResult<HolidayRulePageRespVO> convertPage(PageResult<HolidayRuleDO> page,
                                                          List<GradeDO> gradeList,
                                                          List<TimeSlotDO> timeSlotList) {
        PageResult<HolidayRulePageRespVO> pageResult = new PageResult<>();

        List<HolidayRulePageRespVO> respVOList = new ArrayList<>();
        for (HolidayRuleDO holidayRule : page.getList()) {
            HolidayRulePageRespVO respVO = convert(holidayRule,
                    gradeList,
                    timeSlotList);
            respVOList.add(respVO);
        }

        pageResult.setList(respVOList);
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

}
