package cn.iocoder.yudao.module.school.convert.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeRespVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.ExamRulePageRespVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.HolidayRulePageRespVO;
import cn.iocoder.yudao.module.school.convert.course.TimeSlotConvert;
import cn.iocoder.yudao.module.school.convert.grade.GradeConvert;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.ExamRuleDO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.HolidayRuleDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

@Mapper
public interface ExamRuleConvert {
    ExamRuleConvert INSTANCE = Mappers.getMapper(ExamRuleConvert.class);

    default ExamRulePageRespVO convert(ExamRuleDO examRule,
                                       List<GradeDO> gradeList,
                                       List<TimeSlotDO> timeSlotList) {
        Map<Long, GradeDO> gradeMap = convertMap(gradeList, GradeDO::getId);
        Map<Long, TimeSlotDO> timeSlotMap = convertMap(timeSlotList, TimeSlotDO::getId);

        ExamRulePageRespVO respVO = new ExamRulePageRespVO();
        respVO.setId(examRule.getId());
        respVO.setStartDate(examRule.getStartDate());
        respVO.setEndDate(examRule.getEndDate());

        Set<Long> gradeIds = examRule.getGradeIds();
        List<GradeRespVO> gradeRespVOList = new ArrayList<>();
        for (Long gradeId : gradeIds) {
            GradeDO grade = gradeMap.get(gradeId);
            gradeRespVOList.add(GradeConvert.INSTANCE.convert(grade));
        }
        respVO.setGradeList(gradeRespVOList);


        TimeSlotDO startTimeSlot = timeSlotMap.get(examRule.getStartTimeSlotId());
        respVO.setStartTimeSlot(TimeSlotConvert.INSTANCE.convert(startTimeSlot));

        TimeSlotDO endTimeSlot = timeSlotMap.get(examRule.getEndTimeSlotId());
        respVO.setEndTimeSlot(TimeSlotConvert.INSTANCE.convert(endTimeSlot));

        return respVO;
    }


    default PageResult<ExamRulePageRespVO> convertPage(PageResult<ExamRuleDO> page,
                                                          List<GradeDO> gradeList,
                                                          List<TimeSlotDO> timeSlotList) {
        PageResult<ExamRulePageRespVO> pageResult = new PageResult<>();

        List<ExamRulePageRespVO> respVOList = new ArrayList<>();
        for (ExamRuleDO examRule : page.getList()) {
            ExamRulePageRespVO respVO = convert(examRule,
                    gradeList,
                    timeSlotList);
            respVOList.add(respVO);
        }

        pageResult.setList(respVOList);
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

}
