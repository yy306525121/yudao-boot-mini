// package cn.iocoder.yudao.module.school.service.course;
//
// import ai.timefold.solver.core.api.solver.SolverJob;
// import ai.timefold.solver.core.api.solver.SolverManager;
// import cn.hutool.core.collection.CollUtil;
// import cn.iocoder.yudao.module.school.dal.dataobject.course.CoursePlanDO;
// import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
// import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
// import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
// import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
// import cn.iocoder.yudao.module.school.dal.mysql.course.TimeSlotMapper;
// import cn.iocoder.yudao.module.school.dal.mysql.grade.GradeMapper;
// import cn.iocoder.yudao.module.school.dal.mysql.subject.SubjectMapper;
// import cn.iocoder.yudao.module.school.dal.mysql.teacher.TeacherMapper;
// import cn.iocoder.yudao.module.school.timetabling.domain.Lesson;
// import cn.iocoder.yudao.module.school.timetabling.domain.TimeTable;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.validation.annotation.Validated;
//
// import java.time.DayOfWeek;
// import java.util.*;
// import java.util.concurrent.ExecutionException;
//
// /**
//  * 排课安排 Service 实现类
//  */
// @Service
// @Validated
// @RequiredArgsConstructor
// public class CoursePlanOptServiceImpl implements CoursePlanOptService {
//     private final GradeMapper gradeMapper;
//     private final SubjectMapper subjectMapper;
//     private final TeacherMapper teacherMapper;
//     private final TimeSlotMapper timeSlotMapper;
//
//     private final SolverManager<TimeTable, UUID> solverManager;
//
//     @Override
//     public List<CoursePlanOptDO> getCoursePlanOptList() {
//         return coursePlanOptMapper.selectList();
//     }
//
//     @Override
//     public List<CoursePlanDO> courseScheduling() {
//         List<CoursePlanOptDO> coursePlanOptDOList = this.getCoursePlanOptList();
//
//         List<Lesson> lessonList = new ArrayList<>();
//         for (CoursePlanOptDO coursePlanOpt : coursePlanOptDOList) {
//             Integer countEveryWeek = coursePlanOpt.getCountEveryWeek();
//             Long gradeId = coursePlanOpt.getGradeId();
//             List<GradeDO> gradeList = gradeMapper.selectListByParentId(Collections.singletonList(gradeId));
//             if (CollUtil.isEmpty(gradeList)) {
//                 GradeDO grade = gradeMapper.selectById(gradeId);
//                 gradeList = Collections.singletonList(grade);
//             }
//             SubjectDO subject = subjectMapper.selectById(coursePlanOpt.getSubjectId());
//             TeacherDO teacher = teacherMapper.selectById(coursePlanOpt.getTeacherId());
//
//             lessonList.addAll(generateCoursePlanOptList(countEveryWeek, teacher, subject, gradeList));
//         }
//         long id = 0;
//         for (Lesson lesson : lessonList) {
//             lesson.setId(id++);
//         }
//
//         List<TimeSlotDO> timeSlotList = timeSlotMapper.selectList();
//         List<DayOfWeek> dayOfWeekList = Arrays.asList(DayOfWeek.values());
//
//         TimeTable problem = new TimeTable();
//         problem.setTimeSlotList(timeSlotList);
//         problem.setDayOfWeekList(dayOfWeekList);
//         problem.setLessonList(lessonList);
//
//         UUID problemId = UUID.randomUUID();
//         SolverJob<TimeTable, UUID> solverJob = solverManager.solve(problemId, problem);
//         TimeTable solution;
//         try {
//             // Wait until the solving ends
//             solution = solverJob.getFinalBestSolution();
//         } catch (InterruptedException | ExecutionException e) {
//             throw new IllegalStateException("Solving failed.", e);
//         }
//
//         return null;
//     }
//
//     private List<Lesson> generateCoursePlanOptList(Integer countEveryWeek, TeacherDO teacher, SubjectDO subject, List<GradeDO> gradeList) {
//         List<Lesson> lessonList = new ArrayList<>();
//
//         for (GradeDO grade : gradeList) {
//             for (Integer i = 0; i < countEveryWeek; i++) {
//                 Lesson lesson = new Lesson();
//                 lesson.setTeacher(teacher);
//                 lesson.setSubject(subject);
//                 lesson.setGrade(grade);
//                 lessonList.add(lesson);
//             }
//         }
//
//         return lessonList;
//     }
// }
