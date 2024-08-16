package cn.iocoder.yudao.module.school.timefold.contraint;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.enums.course.CourseTypeEnum;
import cn.iocoder.yudao.module.school.timefold.domain.Lesson;

import static ai.timefold.solver.core.api.score.stream.ConstraintCollectors.count;
import static ai.timefold.solver.core.api.score.stream.Joiners.equal;

public class TimeTableConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                gradeConflict(constraintFactory),
                teacherConflict(constraintFactory),
                timeSlotNormalTimeSlotConflict(constraintFactory),
                timeSlotNormalDayConflict(constraintFactory),
                // timeSlotEveningConflict(constraintFactory),
                continuousConflict(constraintFactory),
                ordinaryConflict(constraintFactory),
                ordinaryAndContinuousConflict(constraintFactory),
                continuousDayConflict(constraintFactory),
        };
    }

    /**
     * 一个班级同时最多只能有一节课
     * @param constraintFactory
     * @return
     */
    private Constraint gradeConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Lesson.class,
                        equal(item -> item.getTimeSlot().getId()),
                        equal(Lesson::getDayOfWeek),
                        equal(item -> item.getGrade().getId()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("grade constraint");
    }

    /**
     * 一个教师同时只能上一节课
     * @param constraintFactory
     * @return
     */
    private Constraint teacherConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Lesson.class,
                        equal(item -> item.getTimeSlot().getId()),
                        equal(Lesson::getDayOfWeek),
                        equal(item -> item.getTeacher().getId()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("teacher conflict");
    }

    /**
     * 正课节次为周1~6的每天的2~10节次
     * @param constraintFactory
     * @return
     */
    private Constraint timeSlotNormalTimeSlotConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .filter(lesson -> CourseTypeEnum.NORMAL.getType().equals(lesson.getCourseType().getType()))
                .filter(lesson -> lesson.getTimeSlot().getSort() < 2 || lesson.getTimeSlot().getSort() > 10)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("timeSlotNormalConflict");
    }

    /**
     * 周日不上正课的限制
     * @param constraintFactory
     * @return
     */
    private Constraint timeSlotNormalDayConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .filter(lesson -> CourseTypeEnum.NORMAL.getType().equals(lesson.getCourseType().getType()))
                .filter(lesson -> lesson.getDayOfWeek().getValue() == 7)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("timeSlotNormalDayConflict");
    }





    /**
     * 夜自习节次为每天的11~12节次
     * @param constraintFactory
     * @return
     */
    private Constraint timeSlotEveningConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .filter(lesson -> CourseTypeEnum.EVENING.getType().equals(lesson.getCourseType().getType()))
                .filter(lesson -> lesson.getTimeSlot().getSort() < 11)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("timeSlot evening conflict");
    }

    /**
     * 连堂课偏好设置
     * @param constraintFactory constraintFactory
     * @return
     */
    private Constraint teacherTimeEfficiency(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .join(Lesson.class, equal((lesson) -> lesson.getTeacher().getId()),
                        equal((lesson) -> lesson.getDayOfWeek().getValue()))
                .filter((lesson1, lesson2) -> Math.abs(lesson1.getTimeSlot().getSort() - lesson2.getTimeSlot().getSort()) == 1)
                .reward(HardSoftScore.ONE_SOFT)
                .asConstraint("Teacher time efficiency");
    }

    /**
     * 如果当天已经安排了连堂课，就尽量不在当天安排普通课时
     * @param constraintFactory constraintFactory
     * @return
     */
    // private Constraint ordinaryAndContinuousConflict(ConstraintFactory constraintFactory) {
    //     return constraintFactory
    //             .forEach(Lesson.class)
    //             .filter((lesson) -> CourseTypeEnum.NORMAL.getType().equals(lesson.getCourseType().getType()))
    //             .join(Lesson.class, equal((lesson) -> lesson.getDayOfWeek().getValue()),
    //                     equal((lesson) -> lesson.getSubject().getId()))
    //             .filter((lesson1, lesson2) -> lesson1.isContinuousFlag() != lesson2.getContinuousFlag().booleanValue())
    //             .penalize(HardSoftScore.ONE_SOFT)
    //             .asConstraint("ordinary continuous conflict");
    // }

    /**
     * 相同科目相同年级的连堂课时和普通课时不能安排在同一天
     * @param constraintFactory
     * @return
     */
    private Constraint ordinaryAndContinuousConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .filter((lesson) -> CourseTypeEnum.NORMAL.getType().equals(lesson.getCourseType().getType()))
                .join(Lesson.class, equal((lesson) -> lesson.getGrade().getId()),
                        equal((lesson) -> lesson.getSubject().getId()),
                        equal((lesson) -> lesson.getDayOfWeek().getValue()))
                .filter((lesson1, lesson2) -> lesson1.isContinuousFlag() != lesson2.isContinuousFlag())
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("ordinary continuous conflict");
    }

    /**
     * 正课每天不能超过2节
     * @param constraintFactory
     * @return
     */
    private Constraint teacherTimeMaxLimit(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .filter(lesson -> CourseTypeEnum.NORMAL.getType().equals(lesson.getCourseType().getType()))
                .groupBy(Lesson::getGrade, Lesson::getDayOfWeek, Lesson::getTeacher, count())
                .filter((week, teacher, courseType, count) -> count > 2)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("teacher time max limit");
    }

    /**
     * 连堂课是指同一天内课程节次相邻的两节课
     * @param constraintFactory
     * @return
     */
    private Constraint continuousConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .filter(Lesson::isContinuousFlag)
                .join(Lesson.class, equal(Lesson::getContinuousUuid))
                .filter((lesson1, lesson2) ->
                                Math.abs(lesson1.getTimeSlot().getSort() - lesson2.getTimeSlot().getSort()) == 1 &&
                                lesson1.getDayOfWeek().getValue() == lesson2.getDayOfWeek().getValue()
                )
                .reward(HardSoftScore.ONE_HARD)
                .asConstraint("continuous conflict");
    }

    /**
     * 一天内不能安排同一个科目的多次连堂课
     * @param constraintFactory
     * @return
     */
    private Constraint continuousDayConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .filter(Lesson::isContinuousFlag)
                .join(Lesson.class, equal((lesson) -> lesson.getSubject().getId()),
                        equal((lesson) -> lesson.getGrade().getId()))
                .filter((lesson1, lesson2) ->
                                !lesson1.getContinuousUuid().equals(lesson2.getContinuousUuid()) &&
                                lesson1.getDayOfWeek().getValue() == lesson2.getDayOfWeek().getValue()
                )
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("continuous day conflict");
    }

    /**
     * 一天内不允许出现多节相同科目的普通课时
     * @param constraintFactory
     * @return
     */
    private Constraint ordinaryConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .filter(lesson -> !lesson.isContinuousFlag())
                .groupBy(Lesson::getGrade, Lesson::getDayOfWeek, Lesson::getTeacher, count())
                .filter((week, teacher, courseType, count) -> count > 1)
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("ordinary conflict");
    }

    /**
     * 老师喜欢上课的时间
     * https://stackoverflow.com/questions/77970555/how-can-you-code-the-teachers-preferences-constraint-for-a-timetable-applicatio
     */
    // private Constraint teacherPreferredWeekLimit(ConstraintFactory constraintFactory) {
    //     return constraintFactory
    //             .forEach(Lesson.class)
    //             .filter(lesson -> {
    //                 if (CollUtil.isNotEmpty(lesson.getPreferWeeks()) && CollUtil.isNotEmpty(lesson.getPreferTimeSlotIds())) {
    //                     // 同时设置了周和节次
    //                     return lesson.getPreferWeeks().contains(lesson.getDayOfWeek().getValue()) && lesson.getPreferTimeSlotIds().contains(lesson.getTimeSlot().getId());
    //                 } else if (CollUtil.isNotEmpty(lesson.getPreferWeeks())) {
    //                     // 只设置了周
    //                     return lesson.getPreferWeeks().contains(lesson.getDayOfWeek().getValue());
    //                 } else if (CollUtil.isNotEmpty(lesson.getPreferTimeSlotIds())) {
    //                     // 只设置了节次
    //                     return lesson.getPreferTimeSlotIds().contains(lesson.getTimeSlot().getId());
    //                 } else {
    //                     return false;
    //                 }
    //             })
    //             .reward(HardSoftScore.ONE_SOFT)
    //             .asConstraint("teacher preferred limit");
    // }
}
