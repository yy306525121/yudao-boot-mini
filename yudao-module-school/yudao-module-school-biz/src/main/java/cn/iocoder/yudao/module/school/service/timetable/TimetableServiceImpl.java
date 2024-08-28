package cn.iocoder.yudao.module.school.service.timetable;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.TimetablePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.TimetableSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableDO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableSettingDO;
import cn.iocoder.yudao.module.school.dal.mysql.course.CourseTypeMapper;
import cn.iocoder.yudao.module.school.dal.mysql.course.TimeSlotMapper;
import cn.iocoder.yudao.module.school.dal.mysql.grade.GradeMapper;
import cn.iocoder.yudao.module.school.dal.mysql.subject.SubjectMapper;
import cn.iocoder.yudao.module.school.dal.mysql.teacher.TeacherMapper;
import cn.iocoder.yudao.module.school.dal.mysql.timetable.TimetableMapper;
import cn.iocoder.yudao.module.school.dal.mysql.timetable.TimetableSettingMapper;
import cn.iocoder.yudao.module.school.enums.course.CourseTypeEnum;
import cn.iocoder.yudao.module.school.timetable.domain.Lesson;
import com.google.ortools.Loader;
import com.google.ortools.sat.*;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.RequiredArgsConstructor;
import org.checkerframework.common.value.qual.IntVal;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.TIMETABLE_NAME_DUPLICATE;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.TIMETABLE_NOT_EXISTS;
import static cn.iocoder.yudao.module.school.enums.LogRecordConstants.*;


/**
 * 排课 Service 实现类
 */
@Service
@Validated
@RequiredArgsConstructor
public class TimetableServiceImpl implements TimetableService {
    private final TimetableMapper timetableMapper;
    private final TimetableSettingMapper timetableSettingMapper;
    private final GradeMapper gradeMapper;
    private final SubjectMapper subjectMapper;
    private final TeacherMapper teacherMapper;
    private final CourseTypeMapper courseTypeMapper;
    private final TimeSlotMapper timeSlotMapper;

    @Override
    @LogRecord(type = SCHOOL_TIMETABLE_TYPE, subType = SCHOOL_TIMETABLE_CREATE_SUB_TYPE, bizNo = "{{#timetable.id}}",
            success = SCHOOL_TIMETABLE_CREATE_SUCCESS)
    public Long createTimetable(TimetableSaveReqVO createReqVO) {
        validateNameDuplicate(createReqVO.getName());
        // 插入
        TimetableDO timetable = BeanUtils.toBean(createReqVO, TimetableDO.class);
        timetableMapper.insert(timetable);

        // 记录日志
        LogRecordContext.putVariable("timetable", timetable);

        // 返回
        return timetable.getId();
    }

    @Override
    @LogRecord(type = SCHOOL_TIMETABLE_TYPE, subType = SCHOOL_TIMETABLE_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
        success = SCHOOL_TIMETABLE_UPDATE_SUCCESS)
    public void updateTimetable(TimetableSaveReqVO updateReqVO) {
        // 校验存在
        TimetableDO oldTimeTable = validateTimetableExists(updateReqVO.getId());
        // 更新
        TimetableDO updateObj = BeanUtils.toBean(updateReqVO, TimetableDO.class);
        timetableMapper.updateById(updateObj);

        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldTimeTable, TimetableSaveReqVO.class));
        LogRecordContext.putVariable("timetable", updateObj);
    }

    @Override
    @LogRecord(type = SCHOOL_TIMETABLE_TYPE, subType = SCHOOL_TIMETABLE_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = SCHOOL_TIMETABLE_DELETE_SUCCESS)
    public void deleteTimetable(Long id) {
        // 校验存在
        TimetableDO timetable = validateTimetableExists(id);
        // 删除
        timetableMapper.deleteById(id);

        LogRecordContext.putVariable("timetable", timetable);
    }

    private void validateNameDuplicate(String name) {
        if (timetableMapper.selectByName(name) != null) {
            throw exception(TIMETABLE_NAME_DUPLICATE);
        }
    }
    private TimetableDO validateTimetableExists(Long id) {
        TimetableDO timetable = timetableMapper.selectById(id);
        if (timetable == null) {
            throw exception(TIMETABLE_NOT_EXISTS);
        }
        return timetable;
    }

    @Override
    public TimetableDO getTimetable(Long id) {
        return timetableMapper.selectById(id);
    }

    @Override
    public PageResult<TimetableDO> getTimetablePage(TimetablePageReqVO pageReqVO) {
        return timetableMapper.selectPage(pageReqVO);
    }

    @Override
    public List<TimetableDO> getTimetableList() {
        return timetableMapper.selectList();
    }

    @Override
    public List<Lesson> generateProblem(Long id) {
        validateTimetableExists(id);

        List<TimetableSettingDO> timetableSettingList = timetableSettingMapper.selectListByTimetableId(id);

        // 1. 生成待排课的课程
        List<Lesson> lessonList = new ArrayList<>();
        Long lessonId = 0L;
        for (TimetableSettingDO timetableSetting : timetableSettingList) {
            Integer ordinaryCount = timetableSetting.getOrdinaryCount();
            Integer continuousCount = timetableSetting.getContinuousCount();
            GradeDO grade = gradeMapper.selectById(timetableSetting.getGradeId());
            TeacherDO teacher = teacherMapper.selectById(timetableSetting.getTeacherId());
            SubjectDO subject = subjectMapper.selectById(timetableSetting.getSubjectId());
            CourseTypeDO courseType = courseTypeMapper.selectById(timetableSetting.getCourseTypeId());

            for (int i = 0; i < ordinaryCount; i++) {
                // 普通课时设置
                Lesson lesson = new Lesson();
                lesson.setId(lessonId++);
                lesson.setGrade(grade);
                lesson.setTeacher(teacher);
                lesson.setSubject(subject);
                lesson.setCourseType(courseType);
                lesson.setContinuousFlag(false);
                lessonList.add(lesson);
            }

            // 连堂课
            for (int i = 0; i < continuousCount; i++) {
                String uuid = IdUtil.simpleUUID();
                for (int j = 0; j < 2; j++) {
                    // 两节连堂课uuid相同
                    Lesson lesson = new Lesson();
                    lesson.setId(lessonId++);
                    lesson.setGrade(grade);
                    lesson.setTeacher(teacher);
                    lesson.setSubject(subject);
                    lesson.setCourseType(courseType);
                    lesson.setContinuousFlag(true);
                    // lesson.setPreferWeeks(timetableSetting.getPreferWeeks());
                    // lesson.setPreferTimeSlotIds(timetableSetting.getPreferTimeSlotIds());

                    lessonList.add(lesson);
                }
            }
        }

        return lessonList;
    }

    @Override
    public List<Lesson> solve(Long timetableId) {
        Loader.loadNativeLibraries();
        int numDays = 6;
        int timeSlotPerDay = 9;

        List<Lesson> lessonList = generateProblem(timetableId);

        List<TeacherDO> teacherList = lessonList.stream().map(Lesson::getTeacher).distinct().toList();
        List<GradeDO> gradeList = lessonList.stream().map(Lesson::getGrade).distinct().toList();
        List<SubjectDO> subjectList = lessonList.stream().map(Lesson::getSubject).distinct().toList();

        //1：建模
        CpModel model = new CpModel();

        //2：创建变量
        Literal[][][][][] x = new Literal[teacherList.size()][gradeList.size()][subjectList.size()][numDays][timeSlotPerDay];
        //2.1：教师t在年级g上科目s的时间是周w的第y节
        for (int t = 0; t < teacherList.size(); t++) {
            for (int g = 0; g < gradeList.size(); g++) {
                for (int s = 0; s < subjectList.size(); s++) {
                    for (int w = 0; w < numDays; w++) {
                        for (int y = 0; y < timeSlotPerDay; y++) {
                            x[t][g][s][w][y] = model.newBoolVar("x[" + teacherList.get(t).getName() + "][" +
                                    gradeList.get(g).getName() + "][" + subjectList.get(s).getName() + "][" +
                                    w + "][" + y + "]");
                        }
                    }
                }
            }
        }
        //2.2：辅助变量，用于存储是否和下一节课连续
        Literal[][][][][] consecutive = new Literal[teacherList.size()][gradeList.size()][subjectList.size()][numDays][timeSlotPerDay];
        for (int t = 0; t < teacherList.size(); t++) {
            for (int g = 0; g < gradeList.size(); g++) {
                for (int s = 0; s < subjectList.size(); s++) {
                    for (int w = 0; w < numDays; w++) {
                        for (int y = 0; y < timeSlotPerDay; y++) {
                            consecutive[t][g][s][w][y] = model.newBoolVar("consecutive[" + teacherList.get(t).getName() + "][" +
                                    gradeList.get(g).getName() + "][" + subjectList.get(s).getName() + "][" +
                                    w + "][" + y + "]");
                        }
                    }
                }
            }
        }

        //3：约束条件
        //3.1：每个班级每个科目只能是指定的老师
        Map<GradeDO, List<Lesson>> gradeLessonMap = lessonList.stream().collect(Collectors.groupingBy(Lesson::getGrade));
        for (int g = 0; g < gradeList.size(); g++) {
            for (int s = 0; s < subjectList.size(); s++) {
                // 获取该班级该科目的任课老师
                int finalG = g;
                int finalS = s;
                Optional<Lesson> opt = lessonList.stream().filter(item -> item.getGrade().getId().equals(gradeList.get(finalG).getId()) && item.getSubject().getId().equals(subjectList.get(finalS).getId())).findFirst();
                if (opt.isEmpty()) {
                    continue;
                }
                Lesson lesson = opt.get();
                for (int w = 0; w < numDays; w++) {
                    for (int y = 0; y < timeSlotPerDay; y++) {
                        for (int t = 0; t < teacherList.size(); t++) {
                            if (!teacherList.get(t).getId().equals(lesson.getTeacher().getId())) {
                                // 如果不是指定的教师，值必须为0
                                model.addEquality(x[t][g][s][w][y], 0);
                            }
                        }
                    }
                }
            }
        }

        //3.2：每个节次同时只能有一节课
        for (int g = 0; g < gradeList.size(); g++) {
            for (int w = 0; w < numDays; w++) {
                for (int y = 0; y < timeSlotPerDay; y++) {
                    List<Literal> subjects = new ArrayList<>();
                    for (int s = 0; s < subjectList.size(); s++) {
                        for (int t = 0; t < teacherList.size(); t++) {
                            subjects.add(x[t][g][s][w][y]);
                        }
                    }
                    model.addAtMostOne(subjects);
                }
            }
        }

        //3.3：每个教师同时最多只能上一节课
        for (int t = 0; t < teacherList.size(); t++) {
            for (int w = 0; w < numDays; w++) {
                for (int y = 0; y < timeSlotPerDay; y++) {
                    List<Literal> teacherConflict = new ArrayList<>();
                    for (int g = 0; g < gradeList.size(); g++) {
                        for (int s = 0; s < subjectList.size(); s++) {
                            teacherConflict.add(x[t][g][s][w][y]);
                        }
                    }
                    model.addAtMostOne(teacherConflict);
                }
            }
        }

        //3.4：班级科目天的最大课程次数（每天上6天课，如果课程数小于6就每天最多一节，如果大于6小于12就每天最多2节，如果大于12就每天最多3节，以此类推）
        for (int g = 0; g < gradeList.size(); g++) {
            for (int s = 0; s < subjectList.size(); s++) {
                // 获取当前班级的该科目的课程数
                GradeDO grade = gradeList.get(g);
                SubjectDO subject = subjectList.get(s);
                long subjectCount = lessonList.stream().filter(item -> item.getGrade().getId().equals(grade.getId()) && item.getSubject().getId().equals(subject.getId())).count();
                long minCountPerDay = subjectCount / numDays;
                long maxCountPerDay;
                if (subjectCount % numDays == 0) {
                    maxCountPerDay = minCountPerDay;
                } else {
                    maxCountPerDay = minCountPerDay + 1;
                }
                for (int w = 0; w < numDays; w++) {
                    LinearExprBuilder countPerDay = LinearExpr.newBuilder();
                    for (int t = 0; t < teacherList.size(); t++) {
                        for (int y = 0; y < timeSlotPerDay; y++) {
                            countPerDay.add(x[t][g][s][w][y]);
                        }
                    }
                    model.addLinearConstraint(countPerDay, minCountPerDay, maxCountPerDay);
                }
            }
        }
        //3.2：每个班级的每个科目的课时数固定
        gradeLessonMap.forEach((grade, gradeLessonList) -> {
            // 该班级的所有科目
            List<SubjectDO> gradeSubjectList = gradeLessonList.stream().map(Lesson::getSubject).distinct().toList();

            for (SubjectDO subject : gradeSubjectList) {
                LinearExpr sumExpr = LinearExpr.sum(
                        teacherList.stream()
                                .flatMap(teacher -> IntStream.range(0, numDays).boxed()
                                        .flatMap(day -> Arrays.stream(x[teacherList.indexOf(teacher)][gradeList.indexOf(grade)][subjectList.indexOf(subject)][day], 0, timeSlotPerDay)))
                                .toArray(Literal[]::new)
                );
                // 获取对应课程的数量
                long subjectCount = gradeLessonList.stream().filter(item -> item.getSubject().getId().equals(subject.getId())).count();
                model.addEquality(sumExpr, subjectCount);
            }
        });
        //3.4：每个老师每天在每个班最多教两节相同的课程，如果有两节相同的课程，课程必须连着上，并且不能在第5节和第6节
        for (int t = 0; t < teacherList.size(); t++) {
            for (int g = 0; g < gradeList.size(); g++) {
                for (int w = 0; w < numDays; w++) {
                    for (int s = 0; s < subjectList.size(); s++) {
                        // 计算这门课在这一天的总课程数
                        List<Literal> subjectCount = new ArrayList<>();
                        for (int y = 0; y < timeSlotPerDay; y++) {
                            subjectCount.add(x[t][g][s][w][y]);
                        }
                        LinearExpr totalSubjectExpr = LinearExpr.sum(subjectCount.toArray(Literal[]::new));

                        LinearExprBuilder builder = LinearExpr.newBuilder();
                        builder.add(totalSubjectExpr).add(-1);

                        // 创建变量，用来判断是否需要连续
                        List<Literal> consecutiveList = new ArrayList<>();
                        for (int y = 0; y < timeSlotPerDay; y++) {
                            consecutiveList.add(consecutive[t][g][s][w][y]);
                        }
                        LinearExpr consecutiveSum = LinearExpr.sum(consecutiveList.toArray(Literal[]::new));

                        //2 节课，consecutive_sum 为 1
                        //1 节课，consecutive_sum 为 0
                        //0 节课，consecutive_sum 为 0
                        model.addGreaterOrEqual(consecutiveSum, builder);

                        // 连续性约束
                        for (int y = 0; y < timeSlotPerDay - 1; y++) {
                            model.addBoolAnd(new Literal[]{x[t][g][s][w][y], x[t][g][s][w][y + 1]})
                                    .onlyEnforceIf(consecutive[t][g][s][w][y]);
                            // 连续性为假时，至少有一节为假
                            model.addBoolOr(new Literal[]{x[t][g][s][w][y].not(), x[t][g][s][w][y + 1].not()})
                                    .onlyEnforceIf(consecutive[t][g][s][w][y].not());
                        }

                        // 3. 不能在第5节和第6节安排连续的两节课
                        model.addAtMostOne(new Literal[]{x[t][g][s][w][4], x[t][g][s][w][5]});
                    }
                }
            }
        }


        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);


        List<Lesson> lessonResolvedList = new ArrayList<>();
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            CourseTypeDO courseType = courseTypeMapper.selectByType(CourseTypeEnum.NORMAL.getType());
            List<TimeSlotDO> timeSlotList = timeSlotMapper.selectList();
            for (int t = 0; t < teacherList.size(); t++) {
                for (int g = 0; g < gradeList.size(); g++) {
                    for (int s = 0; s < subjectList.size(); s++) {
                        for (int w = 0; w < numDays; w++) {
                            for (int y = 0; y < timeSlotPerDay; y++) {
                                if (solver.booleanValue(x[t][g][s][w][y])){
                                    System.out.println("教师：" + teacherList.get(t).getName() + "，班级：" + gradeList.get(g).getName() + "，科目：" + subjectList.get(s).getName() + "，安排在周" + w + "的第" + y + "节");
                                    int sort = y + 2;
                                    TimeSlotDO timeSlot = timeSlotList.stream().filter(item -> item.getSort() == sort).findFirst().orElseThrow();

                                    Lesson lesson = new Lesson();
                                    lesson.setSubject(subjectList.get(s));
                                    lesson.setTeacher(teacherList.get(t));
                                    lesson.setGrade(gradeList.get(g));
                                    lesson.setCourseType(courseType);
                                    lesson.setDayOfWeek(DayOfWeek.of(w + 1));
                                    lesson.setTimeSlot(timeSlot);
                                    lessonResolvedList.add(lesson);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("没有找到答案");
        }

        return lessonResolvedList;
    }
}
