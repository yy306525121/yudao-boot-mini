package cn.iocoder.yudao.module.school.optplanner.score;

import cn.iocoder.yudao.module.school.optplanner.domain.CoursePlanOpt;
import cn.iocoder.yudao.module.school.optplanner.domain.TimeTableOpt;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

import java.util.List;

public class TimeTableEasyScoreCalculator implements EasyScoreCalculator<TimeTableOpt, HardSoftScore> {
    @Override
    public HardSoftScore calculateScore(TimeTableOpt timeTable) {
        List<CoursePlanOpt> coursePlanList = timeTable.getCoursePlanOptList();
        int hardScore = 0;
        for (CoursePlanOpt coursePlanA : coursePlanList) {
            for (CoursePlanOpt coursePlanB : coursePlanList) {
                if (coursePlanA.getTimeSlot() != null && coursePlanA.getTimeSlot().equals(coursePlanB.getTimeSlot()) &&
                    coursePlanA.getDayOfWeek() != null && coursePlanA.getDayOfWeek().getValue() == coursePlanB.getDayOfWeek().getValue() &&
                    !coursePlanA.getId().equals(coursePlanB.getId())) {

                    if (coursePlanA.getTeacher().getId().equals(coursePlanB.getTeacher().getId())) {
                        // 一个教师同一时间只能上一节课
                        hardScore--;
                    }
                    if (coursePlanA.getGrade().getId().equals(coursePlanB.getGrade().getId())) {
                        // 一个班级同时只能有一节课
                        hardScore--;
                    }
                }
            }
        }

        int softScore = 0;
        return HardSoftScore.of(hardScore, softScore);
    }
}
