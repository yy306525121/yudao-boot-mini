package cn.iocoder.yudao.module.school.timetabling.contraint;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.school.enums.course.CourseTypeEnum;
import cn.iocoder.yudao.module.school.timetabling.domain.Lesson;

import static ai.timefold.solver.core.api.score.stream.ConstraintCollectors.count;
import static ai.timefold.solver.core.api.score.stream.Joiners.equal;

public class TimeTableConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                gradeConflict(constraintFactory),
                teacherConflict(constraintFactory),
                timeSlotNormalConflict(constraintFactory),
                timeSlotEveningConflict(constraintFactory),
                teacherTimeEfficiency(constraintFactory),
                teacherTimeMaxLimit(constraintFactory),
                teacherPreferredWeekLimit(constraintFactory)
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
     * 正课和自习课节次约束
     * @param constraintFactory
     * @return
     */
    private Constraint timeSlotNormalConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .filter(lesson -> CourseTypeEnum.NORMAL.getType().equals(lesson.getCourseType().getType()) ||
                        CourseTypeEnum.SELF.getType().equals(lesson.getCourseType().getType()))
                .filter(lesson -> lesson.getTimeSlot().getSort() < 2 || lesson.getTimeSlot().getSort() > 10)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("timeSlotNormalConflict");
    }

    /**
     * 晚自习节次约束
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
     * 教师更喜欢连堂课
     * @param constraintFactory
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
     * 单科每天不能超过2节
     * @param constraintFactory
     * @return
     */
    private Constraint teacherTimeMaxLimit(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .groupBy(Lesson::getDayOfWeek, Lesson::getTeacher, Lesson::getCourseType, count())
                .filter((week, teacher, courseType, count) -> count > 2)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("teacher time max limit");
    }

    /**
     * 老师喜欢上课的时间
     * https://stackoverflow.com/questions/77970555/how-can-you-code-the-teachers-preferences-constraint-for-a-timetable-applicatio
     */
    private Constraint teacherPreferredWeekLimit(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .filter(lesson -> {
                    if (CollUtil.isNotEmpty(lesson.getPreferWeeks()) && CollUtil.isNotEmpty(lesson.getPreferTimeSlotIds())) {
                        // 同时设置了周和节次
                        return lesson.getPreferWeeks().contains(lesson.getDayOfWeek().getValue()) && lesson.getPreferTimeSlotIds().contains(lesson.getTimeSlot().getId());
                    } else if (CollUtil.isNotEmpty(lesson.getPreferWeeks())) {
                        // 只设置了周
                        return lesson.getPreferWeeks().contains(lesson.getDayOfWeek().getValue());
                    } else if (CollUtil.isNotEmpty(lesson.getPreferTimeSlotIds())) {
                        // 只设置了节次
                        return lesson.getPreferTimeSlotIds().contains(lesson.getTimeSlot().getId());
                    } else {
                        return false;
                    }
                })
                .reward(HardSoftScore.ONE_SOFT)
                .asConstraint("teacher preferred limit");
    }
    // Constraint maximizePreferredTimeslotAssignments(ConstraintFactory constraintFactory) {
    //     return constraintFactory.forEach(Lesson.class)
    //             .join(Teacher.class, Joiners.equal(Lesson::getTeacher, Function.identity()))
    //             .filter((lesson, teacher) -> teacher.preferredTimeslots() != null && !teacher.preferredTimeslots().contains(lesson.getTimeslot()))
    //             .penalize(HardSoftScore.ONE_SOFT)
    //             .asConstraint("Teacher was assigned unpreferred timeslot");
    // }
}
