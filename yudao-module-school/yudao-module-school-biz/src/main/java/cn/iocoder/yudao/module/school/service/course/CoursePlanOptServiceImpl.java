package cn.iocoder.yudao.module.school.service.course;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CoursePlanDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CoursePlanOptDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.dal.mysql.course.CoursePlanOptMapper;
import cn.iocoder.yudao.module.school.dal.mysql.course.TimeSlotMapper;
import cn.iocoder.yudao.module.school.dal.mysql.grade.GradeMapper;
import cn.iocoder.yudao.module.school.dal.mysql.subject.SubjectMapper;
import cn.iocoder.yudao.module.school.dal.mysql.teacher.TeacherMapper;
import cn.iocoder.yudao.module.school.optplanner.domain.CoursePlanOpt;
import cn.iocoder.yudao.module.school.optplanner.domain.TimeTableOpt;
import cn.iocoder.yudao.module.school.optplanner.provider.TimeTableConstraintProvider;
import lombok.RequiredArgsConstructor;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 排课安排 Service 实现类
 */
@Service
@Validated
@RequiredArgsConstructor
public class CoursePlanOptServiceImpl implements CoursePlanOptService {
    private final CoursePlanOptMapper coursePlanOptMapper;
    private final GradeMapper gradeMapper;
    private final SubjectMapper subjectMapper;
    private final TeacherMapper teacherMapper;
    private final TimeSlotMapper timeSlotMapper;

    @Override
    public List<CoursePlanOptDO> getCoursePlanOptList() {
        return coursePlanOptMapper.selectList();
    }

    @Override
    public List<CoursePlanDO> courseScheduling() {
        List<CoursePlanOptDO> coursePlanOptDOList = this.getCoursePlanOptList();


        List<CoursePlanOpt> coursePlanOptList = new ArrayList<>();
        for (CoursePlanOptDO coursePlanOptDO : coursePlanOptDOList) {
            Long gradeId = coursePlanOptDO.getGradeId();
            List<GradeDO> gradeList = gradeMapper.selectListByParentId(Collections.singletonList(gradeId));
            if (CollUtil.isEmpty(gradeList)) {
                GradeDO grade = gradeMapper.selectById(gradeId);
                gradeList = Collections.singletonList(grade);
            }
            SubjectDO subject = subjectMapper.selectById(coursePlanOptDO.getSubjectId());
            TeacherDO teacher = teacherMapper.selectById(coursePlanOptDO.getTeacherId());

            coursePlanOptList.addAll(generateCoursePlanOptList(teacher, subject, gradeList));
        }
        long id = 0;
        for (CoursePlanOpt coursePlanOpt : coursePlanOptList) {
            coursePlanOpt.setId(id++);
        }

        List<TimeSlotDO> timeSlotList = timeSlotMapper.selectList();
        List<DayOfWeek> dayOfWeekList = Arrays.asList(DayOfWeek.values());

        TimeTableOpt timeTable = new TimeTableOpt();
        timeTable.setTimeSlotList(timeSlotList);
        timeTable.setDayOfWeekList(dayOfWeekList);
        timeTable.setCoursePlanOptList(coursePlanOptList);

        SolverFactory<TimeTableOpt> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(TimeTableOpt.class)
                .withEntityClasses(CoursePlanOpt.class)
                .withConstraintProviderClass(TimeTableConstraintProvider.class)
                .withTerminationSpentLimit(Duration.ofSeconds(10))
        );

        Solver<TimeTableOpt> solver = solverFactory.buildSolver();
        TimeTableOpt solve = solver.solve(timeTable);


        return null;
    }

    private List<CoursePlanOpt> generateCoursePlanOptList(TeacherDO teacher, SubjectDO subject, List<GradeDO> gradeList) {
        List<CoursePlanOpt> coursePlanOptList = new ArrayList<>();

        for (GradeDO grade : gradeList) {
            CoursePlanOpt coursePlanOpt = new CoursePlanOpt();
            coursePlanOpt.setTeacher(teacher);
            coursePlanOpt.setSubject(subject);
            coursePlanOpt.setGrade(grade);
            coursePlanOptList.add(coursePlanOpt);
        }

        return coursePlanOptList;
    }
}
