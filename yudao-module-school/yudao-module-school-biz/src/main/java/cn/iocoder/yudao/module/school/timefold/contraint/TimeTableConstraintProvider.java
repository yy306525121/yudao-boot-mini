package cn.iocoder.yudao.module.school.timefold.contraint;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.module.school.timefold.domain.Lesson;

import java.util.Arrays;
import java.util.List;

import static ai.timefold.solver.core.api.score.stream.ConstraintCollectors.count;
import static ai.timefold.solver.core.api.score.stream.Joiners.equal;

public class TimeTableConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                gradeConflict(constraintFactory),
                teacherConflict(constraintFactory),
                //                timeSlotNormalTimeSlotConflict(constraintFactory),
                //                timeSlotEveningConflict(constraintFactory),
                continuousConflict(constraintFactory),
                ordinaryAndContinuousConflict(constraintFactory),
                continuousDayConflict(constraintFactory),
                ordinaryConflict(constraintFactory)
                // timeSlotNormalDayConflict(constraintFactory),
                // timeSlotEveningConflict(constraintFactory),
                // continuousConflict(constraintFactory),
                // ordinaryConflict(constraintFactory),
                // ordinaryAndContinuousConflict(constraintFactory),
                // continuousDayConflict(constraintFactory),
        };
    }

    /**
     * 一个班级同时最多只能有一节课
     *
     * @param constraintFactory
     * @return
     */
    public Constraint gradeConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Lesson.class,
                        equal((lesson) -> lesson.getTimeSlot().getId()),
                        equal(Lesson::getGrade))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("grade constraint");
    }

    /**
     * 一个教师同时只能上一节课
     *
     * @param constraintFactory
     * @return
     */
    public Constraint teacherConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Lesson.class,
                        equal((lesson) -> lesson.getTimeSlot().getId()),
                        equal(Lesson::getTeacher))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("teacher conflict");
    }

    /**
     * 连堂课必须是同一天的连续节次
     *
     * @param constraintFactory
     * @return
     */
    public Constraint continuousConflict(ConstraintFactory constraintFactory) {
        List<Integer> avoidSort = Arrays.asList(6, 7);

        //        return constraintFactory
        //                .forEach(Lesson.class)
        //                .filter(Lesson::isContinuousFlag)
        //                .join(
        //                        constraintFactory.forEach(Lesson.class)
        //                                .filter(Lesson::isContinuousFlag),
        //                        Joiners.equal(Lesson::getContinuousUuid)
        //                )
        //                .filter((lesson1, lesson2) -> lesson1.getTimeslot().getDayOfWeek().getValue() == lesson2.getTimeslot().getDayOfWeek().getValue() &&
        //                                Math.abs(lesson1.getTimeslot().getSort() - lesson2.getTimeslot().getSort()) == 1 &&
        //                                CollUtil.isNotEmpty(CollectionUtil.subtract(avoidSort, Arrays.asList(lesson1.getTimeslot().getSort(), lesson2.getTimeslot().getSort())))
        //                        )
        //                .reward(HardSoftScore.ONE_HARD)
        //                .justifyWith((lesson1, lesson2, score) -> new ContinuousJustification(lesson1.getTeacher(), lesson1, lesson2, score))
        //                .asConstraint("continuous conflict");

        return constraintFactory
                .forEachUniquePair(Lesson.class, Joiners.equal(Lesson::getContinuousUuid))
                .filter((lesson1, lesson2) -> lesson1.isContinuousFlag() && lesson2.isContinuousFlag())
                .filter((lesson1, lesson2) -> Math.abs(lesson1.getTimeSlot().getSort() - lesson2.getTimeSlot().getSort()) != 1 ||
                        lesson1.getDayOfWeek().getValue() != lesson2.getDayOfWeek().getValue() ||
                        CollUtil.isEmpty(CollectionUtil.subtract(avoidSort, Arrays.asList(lesson1.getTimeSlot().getSort(), lesson2.getTimeSlot().getSort())))
                )
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("continuous conflict");
    }

    /**
     * 相同科目相同年级的连堂课时和普通课时不能安排在同一天
     *
     * @param constraintFactory
     * @return
     */
    public Constraint ordinaryAndContinuousConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Lesson.class,
                        Joiners.equal(Lesson::getSubject),
                        Joiners.equal(Lesson::getGrade))
                .filter((lesson1, lesson2) ->
                        lesson1.isContinuousFlag() != lesson2.isContinuousFlag() && lesson1.getDayOfWeek().getValue() == lesson2.getDayOfWeek().getValue()
                )
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("ordinary continuous conflict");
    }
    //
    // /**
    //  * 正课每天不能超过2节
    //  * @param constraintFactory
    //  * @return
    //  */
    // private Constraint teacherTimeMaxLimit(ConstraintFactory constraintFactory) {
    //     return constraintFactory
    //             .forEach(Lesson.class)
    //             .filter(lesson -> CourseTypeEnum.NORMAL.getType().equals(lesson.getCourseType().getType()))
    //             .groupBy(Lesson::getGrade, Lesson::getDayOfWeek, Lesson::getTeacher, count())
    //             .filter((week, teacher, courseType, count) -> count > 2)
    //             .penalize(HardSoftScore.ONE_HARD)
    //             .asConstraint("teacher time max limit");
    // }
    //


    /**
     * 一天内不能安排同一个科目的多次连堂课
     *
     * @param constraintFactory
     * @return
     */
    public Constraint continuousDayConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Lesson.class, Joiners.equal(Lesson::getSubject),
                        Joiners.equal(Lesson::getGrade))
                .filter((lesson1, lesson2) -> lesson1.isContinuousFlag() && lesson2.isContinuousFlag() && !lesson1.getContinuousUuid().equals(lesson2.getContinuousUuid()) && lesson1.getDayOfWeek().getValue() == lesson2.getDayOfWeek().getValue())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("continuous day conflict");
    }

    /**
     * 同一个班级一天内不允许出现多节相同科目的普通课时
     *
     * @param constraintFactory
     * @return
     */
    public Constraint ordinaryConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .filter(lesson -> !lesson.isContinuousFlag())
                .groupBy(Lesson::getGrade, Lesson::getDayOfWeek, Lesson::getSubject, count())
                .filter((grade, week, subject, count) -> count > 1)
                .penalize(HardSoftScore.ONE_HARD)
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