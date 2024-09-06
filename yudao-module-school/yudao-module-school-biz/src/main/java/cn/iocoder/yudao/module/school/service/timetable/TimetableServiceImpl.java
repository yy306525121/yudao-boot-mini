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
import cn.iocoder.yudao.module.school.enums.timetable.SubjectEnum;
import cn.iocoder.yudao.module.school.timetable.domain.Lesson;
import com.google.ortools.Loader;
import com.google.ortools.sat.*;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.RequiredArgsConstructor;
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
        int dayPerWeek = 6;
        int timeSlotPerDay = 9;

        List<Lesson> lessonList = generateProblem(timetableId);

        List<TeacherDO> teacherList = lessonList.stream().map(Lesson::getTeacher).distinct().toList();
        int teacherSize = teacherList.size();
        List<GradeDO> gradeList = lessonList.stream().map(Lesson::getGrade).distinct().toList();
        int gradeSize = gradeList.size();
        List<SubjectDO> subjectList = lessonList.stream().map(Lesson::getSubject).distinct().toList();
        int subjectSize = subjectList.size();

        SubjectDO chineseSubject = subjectList.stream().filter(item -> item.getName().equals("语文")).findFirst().orElseThrow();
        SubjectDO englishSubject = subjectList.stream().filter(item -> item.getName().equals("英语")).findFirst().orElseThrow();


        //1：建模
        CpModel model = new CpModel();

        //2：创建变量
        Literal[][][][][] x = new Literal[teacherList.size()][gradeList.size()][subjectList.size()][dayPerWeek][timeSlotPerDay];
        //2.1：教师t在年级g上科目s的时间是周w的第y节
        for (int teacherIndex = 0; teacherIndex < teacherSize; teacherIndex++) {
            for (int gradeIndex = 0; gradeIndex < gradeSize; gradeIndex++) {
                for (int subjectIndex = 0; subjectIndex < subjectSize; subjectIndex++) {
                    for (int week = 0; week < dayPerWeek; week++) {
                        for (int sort = 0; sort < timeSlotPerDay; sort++) {
                            x[teacherIndex][gradeIndex][subjectIndex][week][sort] = model.newBoolVar("x[" + teacherList.get(teacherIndex).getName() + "][" +
                                    gradeList.get(gradeIndex).getName() + "][" + subjectList.get(subjectIndex).getName() + "][" +
                                    week + "][" + sort + "]");
                        }
                    }
                }
            }
        }

        //2.2：辅助变量 1、3、5语文课尽量排在最前名，2、4、6英语尽量排在最前面
        Literal[][] topLessonPreferred = new Literal[gradeSize][dayPerWeek];
        for (int gradeIndex = 0; gradeIndex < gradeSize; gradeIndex++) {
            for (int week = 0; week < dayPerWeek; week++) {
                topLessonPreferred[gradeIndex][week] = model.newBoolVar("top_lesson_preferred[" + gradeList.get(gradeIndex).getName() + "][" + week + "]");
            }
        }


        //3：约束条件
        //3.1：每个班级每个科目只能是指定的老师
        Map<GradeDO, List<Lesson>> gradeLessonMap = lessonList.stream().collect(Collectors.groupingBy(Lesson::getGrade));
        for (int gradeIndex = 0; gradeIndex < gradeList.size(); gradeIndex++) {
            for (int subjectIndex = 0; subjectIndex < subjectList.size(); subjectIndex++) {
                // 获取该班级该科目的任课老师
                int finalG = gradeIndex;
                int finalS = subjectIndex;
                Optional<Lesson> opt = lessonList.stream()
                        .filter(item -> item.getGrade().getId().equals(gradeList.get(finalG).getId()) &&
                                item.getSubject().getId().equals(subjectList.get(finalS).getId()))
                        .findFirst();
                if (opt.isEmpty()) {
                    continue;
                }
                Lesson lesson = opt.get();
                for (int week = 0; week < dayPerWeek; week++) {
                    for (int sort = 0; sort < timeSlotPerDay; sort++) {
                        for (int teacherIndex = 0; teacherIndex < teacherList.size(); teacherIndex++) {
                            if (!teacherList.get(teacherIndex).getId().equals(lesson.getTeacher().getId())) {
                                // 如果不是指定的教师，值必须为0
                                model.addEquality(x[teacherIndex][gradeIndex][subjectIndex][week][sort], 0);
                            }
                        }
                    }
                }
            }
        }

        //3.2：每个节次同时只能有一节课
        for (int gradeIndex = 0; gradeIndex < gradeList.size(); gradeIndex++) {
            for (int week = 0; week < dayPerWeek; week++) {
                for (int sort = 0; sort < timeSlotPerDay; sort++) {
                    List<Literal> possibleSubjectList = new ArrayList<>();
                    for (int s = 0; s < subjectList.size(); s++) {
                        for (int t = 0; t < teacherList.size(); t++) {
                            possibleSubjectList.add(x[t][gradeIndex][s][week][sort]);
                        }
                    }
                    model.addAtMostOne(possibleSubjectList);
                }
            }
        }

        //3.3：每个教师同时最多只能上一节课
        for (int teacherIndex = 0; teacherIndex < teacherList.size(); teacherIndex++) {
            for (int week = 0; week < dayPerWeek; week++) {
                for (int sort = 0; sort < timeSlotPerDay; sort++) {
                    List<Literal> possibleTeacherList = new ArrayList<>();
                    for (int g = 0; g < gradeList.size(); g++) {
                        for (int s = 0; s < subjectList.size(); s++) {
                            possibleTeacherList.add(x[teacherIndex][g][s][week][sort]);
                        }
                    }
                    model.addAtMostOne(possibleTeacherList);
                }
            }
        }

        //3.4：班级科目天的最大课程次数（每周上6天课，如果课程数小于6就每天最多一节，如果大于6小于12就每天最多2节，如果大于12就每天最多3节，以此类推）
        for (int gradeIndex = 0; gradeIndex < gradeSize; gradeIndex++) {
            for (int subjectIndex = 0; subjectIndex < subjectSize; subjectIndex++) {
                // 获取当前班级的该科目的课程数
                GradeDO grade = gradeList.get(gradeIndex);
                SubjectDO subject = subjectList.get(subjectIndex);
                long subjectCount = lessonList.stream().filter(item -> item.getGrade().getId().equals(grade.getId()) && item.getSubject().getId().equals(subject.getId())).count();
                long minCountPerDay = subjectCount / dayPerWeek;
                long maxCountPerDay;
                if (subjectCount % dayPerWeek == 0) {
                    maxCountPerDay = minCountPerDay;
                } else {
                    maxCountPerDay = minCountPerDay + 1;
                }
                for (int w = 0; w < dayPerWeek; w++) {
                    LinearExprBuilder countPerDay = LinearExpr.newBuilder();
                    for (int t = 0; t < teacherList.size(); t++) {
                        for (int y = 0; y < timeSlotPerDay; y++) {
                            countPerDay.add(x[t][gradeIndex][subjectIndex][w][y]);
                        }
                    }
                    model.addLinearConstraint(countPerDay, minCountPerDay, maxCountPerDay);
                }
            }
        }
        //3.5：每个班级的每个科目的课时数固定
        gradeLessonMap.forEach((grade, gradeLessonList) -> {
            // 该班级的所有科目
            List<SubjectDO> gradeSubjectList = gradeLessonList.stream().map(Lesson::getSubject).distinct().toList();

            for (SubjectDO subject : gradeSubjectList) {
                LinearExpr sumExpr = LinearExpr.sum(
                        teacherList.stream()
                                .flatMap(teacher -> IntStream.range(0, dayPerWeek).boxed()
                                        .flatMap(day -> Arrays.stream(x[teacherList.indexOf(teacher)][gradeList.indexOf(grade)][subjectList.indexOf(subject)][day], 0, timeSlotPerDay)))
                                .toArray(Literal[]::new)
                );
                // 获取对应课程的数量
                long subjectCount = gradeLessonList.stream().filter(item -> item.getSubject().getId().equals(subject.getId())).count();
                model.addEquality(sumExpr, subjectCount);
            }
        });


        // 3.6 如果当天存在多节相同的课，那么课程必须连续
        gradeLessonMap.forEach((grade, gradeLessonList) -> {
            // 该班级的所有科目
            List<SubjectDO> gradeSubjectList = gradeLessonList.stream().map(Lesson::getSubject).distinct().toList();

            for (SubjectDO subject : gradeSubjectList) {
                //该班级的老师
                Lesson lesson = gradeLessonList.stream().filter(item -> item.getSubject().getId().equals(subject.getId())).findFirst().orElseThrow();
                int teacherIndex = teacherList.indexOf(lesson.getTeacher());
                int gradeIndex = gradeList.indexOf(grade);
                int subjectIndex = subjectList.indexOf(subject);

                for (int week = 0; week < dayPerWeek; week++) {
                    for (int sort1 = 0; sort1 < timeSlotPerDay; sort1++) {
                        for (int sort2 = sort1 + 1; sort2 < timeSlotPerDay; sort2++) {
                            // 连堂课不能安排在第5节和第6节
                            if ((sort1 == 4 && sort2 == 5) || (sort1 == 5 && sort2 == 4)) {
                                model.addBoolOr(new Literal[]{x[teacherIndex][gradeIndex][subjectIndex][week][sort1].not(),
                                        x[teacherIndex][gradeIndex][subjectIndex][week][sort2].not(),
                                });
                            }
                            if (Math.abs(sort1 - sort2) != 1) {
                                model.addBoolOr(new Literal[]{
                                            x[teacherIndex][gradeIndex][subjectIndex][week][sort1].not(),
                                            x[teacherIndex][gradeIndex][subjectIndex][week][sort2].not(),
                                        });
                            }
                        }
                    }
                }
            }
        });

        //3.7：1、3、5语文课尽量往前排，2、4、6英语课尽量往前排
        LinearExprBuilder obj = LinearExpr.newBuilder();
        gradeLessonMap.forEach((grade, gradeLessonList) -> {
            int gradeIndex = gradeList.indexOf(grade);
            // 该班级的所有科目
            List<SubjectDO> gradeSubjectList = gradeLessonList.stream().map(Lesson::getSubject).distinct().toList();
            for (SubjectDO subject : gradeSubjectList) {
                int subjectIndex = subjectList.indexOf(subject);

                //该班级的老师
                Lesson lesson = gradeLessonList.stream().filter(item -> item.getSubject().getId().equals(subject.getId())).findFirst().orElseThrow();
                int teacherIndex = teacherList.indexOf(lesson.getTeacher());
                for (int week = 0; week < dayPerWeek; week++) {
                    if (week % 2 == 0 && subject.getId().equals(chineseSubject.getId())) {
                        // 0、2、4（对应周1、3、5)
                        for (int sort = 0; sort < timeSlotPerDay; sort++) {
                            obj.addTerm(x[teacherIndex][gradeIndex][subjectIndex][week][sort], sort);
                        }
                        model.minimize(obj);
                    } else if (week % 2 == 1 && subject.getId().equals(englishSubject.getId())) {
                        // 1、3、5（对应周2、4、6)
                        for (int sort = 0; sort < timeSlotPerDay; sort++) {
                            obj.addTerm(x[teacherIndex][gradeIndex][subjectIndex][week][sort], sort);
                        }
                    }
                }
            }
        });
        model.minimize(obj);

        //3.8：体育课不能排在上午前两节
        SubjectDO subject = subjectMapper.selectByName(SubjectEnum.SPORTS.getName());
        int subjectIndex = subjectList.indexOf(subject);
        for (int teacherIndex = 0; teacherIndex < teacherSize; teacherIndex++) {
            for (int gradeIndex = 0; gradeIndex < gradeSize; gradeIndex++) {
                for (int week = 0; week < dayPerWeek; week++) {
                    for (int sort = 0; sort < 2; sort++) {
                        model.addEquality(x[teacherIndex][gradeIndex][subjectIndex][week][sort], 0);
                    }
                }
            }
        }


        //固定课程
        // model.addEquality(x[2][0][1][0][0], 1);


        //4：定义求解器
        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(30*60);
        solver.getParameters().setLogSearchProgress(true);
        CpSolverStatus status = solver.solve(model);


        List<Lesson> lessonResolvedList = new ArrayList<>();
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            CourseTypeDO courseType = courseTypeMapper.selectByType(CourseTypeEnum.NORMAL.getType());
            List<TimeSlotDO> timeSlotList = timeSlotMapper.selectList();
            for (int t = 0; t < teacherList.size(); t++) {
                for (int g = 0; g < gradeList.size(); g++) {
                    for (int s = 0; s < subjectList.size(); s++) {
                        for (int w = 0; w < dayPerWeek; w++) {
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
