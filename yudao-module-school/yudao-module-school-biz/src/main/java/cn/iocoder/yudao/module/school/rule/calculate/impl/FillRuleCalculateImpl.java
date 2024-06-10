package cn.iocoder.yudao.module.school.rule.calculate.impl;

import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.FillRuleDO;
import cn.iocoder.yudao.module.school.enums.course.TimeSlotTypeEnum;
import cn.iocoder.yudao.module.school.rule.calculate.BaseRuleCalculate;
import cn.iocoder.yudao.module.school.service.course.CourseFeeService;
import cn.iocoder.yudao.module.school.service.course.TimeSlotService;
import cn.iocoder.yudao.module.school.service.grade.GradeService;
import cn.iocoder.yudao.module.school.service.rule.FillRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Component
@Order(30)
@RequiredArgsConstructor
public class FillRuleCalculateImpl implements BaseRuleCalculate {
    private final FillRuleService fillRuleService;
    private final TimeSlotService timeSlotService;
    private final GradeService gradeService;
    private final CourseFeeService courseFeeService;

    public static final String REMARK = "补课";

    @Override
    public List<CourseFeeDO> handleCourseFee(List<CourseFeeDO> courseFeeList, LocalDate startDate, LocalDate endDate) {
        List<FillRuleDO> fillRuleList = fillRuleService.getFillRuleList(startDate, endDate);

        for (FillRuleDO rule : fillRuleList) {
            LocalDate date = rule.getDate();
            Integer week = rule.getWeek();
            Set<Long> gradeIds = rule.getGradeIds();
            Long startTimeSlotId = rule.getStartTimeSlotId();
            Long endTimeSlotId = rule.getEndTimeSlotId();

            List<GradeDO> gradeList = gradeService.getGradeListByParentIds(gradeIds);
            Set<Long> sendLevelGradeIdList = convertSet(gradeList, GradeDO::getId);

            TimeSlotDO startTimeSlot = timeSlotService.getTimeSlot(startTimeSlotId);
            TimeSlotDO endTimeSlot = timeSlotService.getTimeSlot(endTimeSlotId);

            // 删除补课前的课时费
            for (int sort = startTimeSlot.getSort(); sort <= endTimeSlot.getSort(); sort++) {
                TimeSlotDO timeSlot = timeSlotService.getTimeSlotBySort(sort);
                if (timeSlot.getType().equals(TimeSlotTypeEnum.MORNING.getType())) {
                    // 如果是早自习
                    // 获取当天该节次的所有课时费
                    List<CourseFeeDO> list = courseFeeList.stream().filter(item -> item.getDate().equals(date) && item.getTimeSlotId().equals(timeSlot.getId())).toList();
                    // 获取教师ID集合
                    Set<Long> teacherIdList = convertSet(list, CourseFeeDO::getTeacherId);
                    for (Long teacherId : teacherIdList) {
                        // 判断该教师是否存在其他班级的当天该节次的课时费
                        long count = courseFeeList.stream().filter(item -> item.getDate().equals(date) && item.getTimeSlotId().equals(timeSlot.getId()) && !sendLevelGradeIdList.contains(item.getGradeId())).count();
                        if (count == 0) {
                            courseFeeList.removeIf(item -> item.getDate().equals(date) && item.getTimeSlotId().equals(timeSlot.getId()) && item.getTeacherId().equals(teacherId));
                        }
                    }
                } else {
                    // 如果不是早自习，直接删除
                    courseFeeList.removeIf(item -> item.getDate().equals(date) && item.getTimeSlotId().equals(timeSlot.getId()));
                }
            }


            List<CourseFeeDO> courseFeePreviewList = courseFeeService.calculateCourseFee(date, gradeIds, startTimeSlotId, endTimeSlotId, null);
            for (CourseFeeDO courseFee : courseFeePreviewList) {
                long count = courseFeeList.stream().filter(item -> item.getTeacherId().equals(courseFee.getTeacherId()) &&
                        item.getWeek().equals(courseFee.getWeek()) &&
                        courseFee.getTimeSlotId().equals(item.getTimeSlotId()) &&
                        courseFee.getDate().equals(item.getDate())).count();
                if (count == 0) {
                    courseFee.setRemark(REMARK);
                    courseFeeList.add(courseFee);
                }
            }

        }
        return courseFeeList;
    }
}
