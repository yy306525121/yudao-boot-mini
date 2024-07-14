package cn.iocoder.yudao.module.school.rule.calculate.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.StrPool;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CoursePlanDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.ExamRuleDO;
import cn.iocoder.yudao.module.school.enums.course.CourseTypeEnum;
import cn.iocoder.yudao.module.school.enums.course.TimeSlotTypeEnum;
import cn.iocoder.yudao.module.school.rule.calculate.RuleCalculateHandler;
import cn.iocoder.yudao.module.school.service.course.CoursePlanService;
import cn.iocoder.yudao.module.school.service.course.CourseTypeService;
import cn.iocoder.yudao.module.school.service.course.TimeSlotService;
import cn.iocoder.yudao.module.school.service.grade.GradeService;
import cn.iocoder.yudao.module.school.service.rule.ExamRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Component
@Order(20)
@RequiredArgsConstructor
public class ExamRuleCalculateHandlerImpl implements RuleCalculateHandler {
    private final ExamRuleService examRuleService;
    private final GradeService gradeService;
    private final TimeSlotService timeSlotService;
    private final CoursePlanService coursePlanService;
    private final CourseTypeService courseTypeService;

    @Override
    public List<CourseFeeDO> handleCourseFee(List<CourseFeeDO> courseFeeList, LocalDate startDate, LocalDate endDate) {
        List<ExamRuleDO> examRuleList = examRuleService.getExamRuleList(startDate, endDate);

        Set<String> ignoreItemList = getIgnoreItem(examRuleList);

        for (String ignoreItem : ignoreItemList) {
            String[] ignoreProps = ignoreItem.split(StrPool.UNDERLINE);
            String dateStr = ignoreProps[0];
            String sort = ignoreProps[1];
            String gradeId = ignoreProps[2];

            LocalDate date = LocalDateTimeUtil.parseDate(dateStr, DatePattern.NORM_DATE_PATTERN);

            List<GradeDO> gradeList = gradeService.getGradeListByParentIds(CollUtil.toList(Long.valueOf(gradeId)));
            List<Long> gradeIdList = convertList(gradeList, GradeDO::getId);

            TimeSlotDO timeSlot = timeSlotService.getTimeSlotBySort(Integer.valueOf(sort));
            if (timeSlot.getType().equals(TimeSlotTypeEnum.MORNING.getType())) {
                // 早自习 因为早自习的课程不和班级直接关联，所以删除教师的课时费时，需要判断该教师是否对不在放假范围内的班级也进行了授课
                int week = date.getDayOfWeek().getValue();

                // 获取当前节次下的所有课程计划
                CourseTypeDO courseTypeMorning = courseTypeService.getCourseTypeByType(CourseTypeEnum.MORNING.getType());
                List<CoursePlanDO> coursePlanList = coursePlanService.getCoursePlanList(null, null, courseTypeMorning.getId(), null, date, week);
                coursePlanList = coursePlanList.stream().filter(item -> item.getTimeSlotId().equals(timeSlot.getId())).toList();
                // 获取当前节次下上课的所有老师
                Set<Long> teacherIdList = convertSet(coursePlanList, CoursePlanDO::getTeacherId);

                for (Long teacherId : teacherIdList) {
                    boolean deleteFlag = true;

                    // 获取该教师的当前日期，当前节次的所有课程计划
                    List<CoursePlanDO> coursePlanListCurrentTeacher = coursePlanList.stream().filter(item -> item.getTeacherId().equals(teacherId)).toList();
                    // 判断是否存在不在待删班级中的课程计划
                    List<CoursePlanDO> notDeleteGradeCoursePlanList = coursePlanListCurrentTeacher.stream().filter(item -> !gradeIdList.contains(item.getGradeId())).toList();
                    if (CollUtil.isNotEmpty(notDeleteGradeCoursePlanList)) {
                        // 判断不在待删除班级中的课程计划是否在其他ignoreItem中存在， 如果存在，也删除
                        Set<Long> otherGradeIdList = convertSet(notDeleteGradeCoursePlanList, CoursePlanDO::getGradeId);
                        for (Long otherGradeId : otherGradeIdList) {
                            GradeDO grade = gradeService.getGrade(otherGradeId);
                            if (!ignoreItemList.contains(dateStr + StrPool.UNDERLINE + sort + StrPool.UNDERLINE + grade.getParentId())) {
                                deleteFlag = false;
                                break;
                            }
                        }
                    }
                    if (deleteFlag) {
                        courseFeeList.removeIf(item ->
                                item.getDate().equals(date) &&
                                        gradeIdList.contains(item.getGradeId()) &&
                                        item.getTeacherId().equals(teacherId) &&
                                        item.getTimeSlotId().equals(timeSlot.getId())
                        );
                    }
                }
            } else {
                // 如果不是早自习
                courseFeeList.removeIf(
                        fee ->
                                fee.getDate().equals(date) &&
                                        gradeIdList.contains(fee.getGradeId()) &&
                                        fee.getTimeSlotId().equals(timeSlot.getId())
                );
            }
        }

        return courseFeeList;
    }


    /**
     * 获取考试列表，列表item格式：yyyy-MM-dd_节次sort_班级id
     * @param ruleList
     * @return
     */
    private Set<String> getIgnoreItem(List<ExamRuleDO> ruleList) {
        if (CollUtil.isEmpty(ruleList)) {
            return new HashSet<>();
        }

        Set<String> ignoreItemList = new HashSet<>();

        for (ExamRuleDO rule : ruleList) {
            Set<Long> gradeIds = rule.getGradeIds();
            List<GradeDO> gradeList = gradeService.getGradeListByIds(gradeIds);

            LocalDate startDate = rule.getStartDate();
            TimeSlotDO startTimeSlot = timeSlotService.getTimeSlot(rule.getStartTimeSlotId());
            LocalDate endDate = rule.getEndDate();
            TimeSlotDO endTimeSlot = timeSlotService.getTimeSlot(rule.getEndTimeSlotId());

            LocalDate tmpDate = startDate;
            while (!tmpDate.isAfter(endDate)) {
                for (int sort = startTimeSlot.getSort(); sort <= endTimeSlot.getSort(); sort++) {
                    for (GradeDO grade : gradeList) {
                        String dateStr = LocalDateTimeUtil.format(tmpDate, DatePattern.NORM_DATE_PATTERN);
                        ignoreItemList.add(dateStr + StrPool.UNDERLINE + sort + StrPool.UNDERLINE + grade.getId());
                    }
                }
                tmpDate = tmpDate.plusDays(1);
            }
        }
        return ignoreItemList;
    }
}
