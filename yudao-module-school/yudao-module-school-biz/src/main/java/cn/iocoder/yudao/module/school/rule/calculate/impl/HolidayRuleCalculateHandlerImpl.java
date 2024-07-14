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
import cn.iocoder.yudao.module.school.dal.dataobject.rule.HolidayRuleDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.enums.course.CourseTypeEnum;
import cn.iocoder.yudao.module.school.enums.course.TimeSlotTypeEnum;
import cn.iocoder.yudao.module.school.rule.calculate.RuleCalculateHandler;
import cn.iocoder.yudao.module.school.service.course.CoursePlanService;
import cn.iocoder.yudao.module.school.service.course.CourseTypeService;
import cn.iocoder.yudao.module.school.service.course.TimeSlotService;
import cn.iocoder.yudao.module.school.service.grade.GradeService;
import cn.iocoder.yudao.module.school.service.rule.HolidayRuleService;
import cn.iocoder.yudao.module.school.service.teacher.TeacherService;
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
@Order(10)
@RequiredArgsConstructor
public class HolidayRuleCalculateHandlerImpl implements RuleCalculateHandler {
    private final HolidayRuleService holidayRuleService;
    private final GradeService gradeService;
    private final TimeSlotService timeSlotService;
    private final CoursePlanService coursePlanService;
    private final CourseTypeService courseTypeService;
    private final TeacherService teacherService;

    @Override
    public List<CourseFeeDO> handleCourseFee(List<CourseFeeDO> courseFeeList, LocalDate startDate, LocalDate endDate) {
        List<HolidayRuleDO> holidayRuleList = holidayRuleService.getHolidayRuleList(startDate, endDate);

        Set<String> ignoreItemList = getIgnoreItem(holidayRuleList, startDate, endDate);

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
                List<CoursePlanDO> coursePlanList = coursePlanService.getCoursePlanList(null, null, null, null, date, week);
                // 获取早自习的课程ID
                List<CoursePlanDO> coursePlanMorningList = coursePlanList.stream().filter(item -> item.getTimeSlotId().equals(timeSlot.getId())).toList();
                Set<Long> subjectIdSet = convertSet(coursePlanMorningList, CoursePlanDO::getSubjectId);
                // 获取当前节次下需要上早自习的所有老师
                List<TeacherDO> teacherList = teacherService.getTeacherListBySubjectIds(subjectIdSet);
                Set<Long> teacherIdList = convertSet(teacherList, TeacherDO::getId);

                for (Long teacherId : teacherIdList) {
                    boolean deleteFlag = true;

                    // 获取该教师的当前日期，是否存在不在当前阶段的课程计划
                    List<CoursePlanDO> coursePlanListCurrentTeacher = coursePlanList.stream().filter(item -> item.getTeacherId() != null && item.getTeacherId().equals(teacherId)).toList();
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
                                        item.getGradeId() == null &&
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
     * 获取放假列表，列表item格式：yyyy-MM-dd_节次sort_班级id
     * @param ruleList
     * @param calculateStartDate 规则生成的最小日期
     * @param calculateEndDate 规则生成的最大日期
     * @return
     */
    private Set<String> getIgnoreItem(List<HolidayRuleDO> ruleList, LocalDate calculateStartDate, LocalDate calculateEndDate) {
        if (CollUtil.isEmpty(ruleList)) {
            return new HashSet<>();
        }

        Set<String> ignoreItemList = new HashSet<>();

        for (HolidayRuleDO rule : ruleList) {
            Set<Long> gradeIds = rule.getGradeIds();
            List<GradeDO> gradeList = gradeService.getGradeListByIds(gradeIds);

            LocalDate startDate = rule.getStartDate().isBefore(calculateStartDate) ? calculateStartDate : rule.getStartDate();
            LocalDate endDate = rule.getEndDate().isAfter(calculateEndDate) ? calculateEndDate : rule.getEndDate();
            TimeSlotDO ruleStartTimeSlot = timeSlotService.getTimeSlot(rule.getStartTimeSlotId());
            TimeSlotDO ruleEndTimeSlot = timeSlotService.getTimeSlot(rule.getEndTimeSlotId());

            TimeSlotDO firstTimeSlot = timeSlotService.getFirstTimeSlot();
            TimeSlotDO lastTimeSlot = timeSlotService.getLastTimeSlot();

            LocalDate tmpDate = startDate;
            while (!tmpDate.isAfter(endDate)) {
                TimeSlotDO startTimeSlot = tmpDate.equals(startDate) ? ruleStartTimeSlot : firstTimeSlot;
                TimeSlotDO endTimeSlot = tmpDate.equals(endDate) ? ruleEndTimeSlot : lastTimeSlot;

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
