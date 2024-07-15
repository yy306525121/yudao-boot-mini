package cn.iocoder.yudao.module.school.optplanner.provider;

import cn.iocoder.yudao.module.school.optplanner.domain.CoursePlanOpt;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class TimeTableConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                gradeConflict(constraintFactory),
                teacherConflict(constraintFactory)
        };
    }

    private Constraint gradeConflict(ConstraintFactory constraintFactory) {
        // 一个班级同时最多只能有一节课
        return constraintFactory
                .forEach(CoursePlanOpt.class)
                .join(CoursePlanOpt.class,
                        Joiners.equal(item -> item.getTimeSlot().getId()),
                        Joiners.equal(CoursePlanOpt::getDayOfWeek),
                        Joiners.equal(item -> item.getGrade().getId()),
                        Joiners.lessThan(CoursePlanOpt::getId)
                ).penalize(HardSoftScore.ONE_HARD)
                .asConstraint("grade conflict");
    }

    private Constraint teacherConflict(ConstraintFactory constraintFactory) {
        // 一个教师同时只能上一节课
        return constraintFactory
                .forEach(CoursePlanOpt.class)
                .join(CoursePlanOpt.class,
                        Joiners.equal(item -> item.getTimeSlot().getId()),
                        Joiners.equal(CoursePlanOpt::getDayOfWeek),
                        Joiners.equal(item -> item.getTeacher().getId()),
                        Joiners.lessThan(CoursePlanOpt::getId)
                )
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("teacher conflict");
    }

}
