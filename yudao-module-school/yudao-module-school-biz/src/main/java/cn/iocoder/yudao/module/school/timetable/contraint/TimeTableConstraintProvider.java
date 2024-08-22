package cn.iocoder.yudao.module.school.timetable.contraint;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.module.school.enums.course.CourseTypeEnum;
import cn.iocoder.yudao.module.school.timetable.domain.Lesson;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import java.util.Arrays;
import java.util.List;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.count;


public class TimeTableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                gradeConflict(constraintFactory),
                teacherConflict(constraintFactory),
                continuousConflict(constraintFactory),
                ordinaryAndContinuousConflict(constraintFactory),
                timeSlotNormalTimeSlotConflict(constraintFactory),
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
                .forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal((lesson) -> lesson.getTimeSlot().getId()),
                        Joiners.equal(Lesson::getDayOfWeek),
                        Joiners.lessThan(Lesson::getId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("grade conflict");
    }

    /**
     * 一个教师同时只能上一节课
     *
     * @param constraintFactory
     * @return
     */
    public Constraint teacherConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal((lesson) -> lesson.getTimeSlot().getId()),
                        Joiners.equal((lesson) -> lesson.getTeacher().getId()),
                        Joiners.lessThan(Lesson::getId))
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

        return constraintFactory
                .forEach(Lesson.class)
                .filter(Lesson::isContinuousFlag)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getContinuousUuid),
                        Joiners.lessThan(Lesson::getId))
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
                .forEach(Lesson.class)
                .filter(lesson -> !lesson.isContinuousFlag())
                .groupBy(Lesson::getUnionFlag, count())
                .filter((unionFlag, count) -> count > 1)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("ordinary continuous conflict");
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
}