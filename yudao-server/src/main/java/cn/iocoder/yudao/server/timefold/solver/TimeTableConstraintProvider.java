package cn.iocoder.yudao.server.timefold.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;
import cn.iocoder.yudao.server.timefold.domain.Lesson;

public class TimeTableConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                gradeConflict(constraintFactory),
                teacherConflict(constraintFactory)
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
                        Joiners.equal(item -> item.getTimeSlot().getId()),
                        Joiners.equal(Lesson::getDayOfWeek),
                        Joiners.equal(item -> item.getGrade().getId()))
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
                        Joiners.equal(item -> item.getTimeSlot().getId()),
                        Joiners.equal(Lesson::getDayOfWeek),
                        Joiners.equal(item -> item.getTeacher().getId()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("teacher conflict");
    }

}
