package cn.iocoder.yudao.module.school.service.timetable;

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
import cn.iocoder.yudao.module.school.framework.timetable.core.utils.TimetableConstraintUtils;
import cn.iocoder.yudao.module.school.timetable.domain.Lesson;
import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.Literal;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

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
            Integer count = timetableSetting.getCount();
            GradeDO grade = gradeMapper.selectById(timetableSetting.getGradeId());
            TeacherDO teacher = teacherMapper.selectById(timetableSetting.getTeacherId());
            SubjectDO subject = subjectMapper.selectById(timetableSetting.getSubjectId());
            CourseTypeDO courseType = courseTypeMapper.selectById(timetableSetting.getCourseTypeId());

            for (int i = 0; i < count; i++) {
                // 普通课时设置
                Lesson lesson = new Lesson();
                lesson.setId(lessonId++);
                lesson.setGrade(grade);
                lesson.setTeacher(teacher);
                lesson.setSubject(subject);
                lesson.setCourseType(courseType);
                lessonList.add(lesson);
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


        //3：约束条件
        //3.1：每个班级每个科目只能是指定的老师

        TimetableConstraintUtils.gradeTeacherUniqueConstraint(model, x, lessonList, dayPerWeek, timeSlotPerDay);

        //3.2：每个节次同时只能有一节课
        TimetableConstraintUtils.timeSlotUniqueConstraint(model, x, lessonList, dayPerWeek, timeSlotPerDay);

        //3.3：每个教师同时最多只能上一节课
        TimetableConstraintUtils.teacherTimeConstraint(model, x, lessonList, dayPerWeek, timeSlotPerDay);

        //3.4：班级科目天的最大课程次数（每周上6天课，如果课程数小于6就每天最多一节，如果大于6小于12就每天最多2节，如果大于12就每天最多3节，以此类推）
        TimetableConstraintUtils.gradeSubjectMaxPerDayConstraint(model, x, lessonList, dayPerWeek, timeSlotPerDay);

        //3.5：每个班级的每个科目的课时数固定
        TimetableConstraintUtils.gradeSubjectMaxPerWeekConstraint(model, x, lessonList, dayPerWeek, timeSlotPerDay);


        // 3.6 如果当天存在多节相同的课，那么课程必须连续
        TimetableConstraintUtils.continuousConstraint(model, x, lessonList, dayPerWeek, timeSlotPerDay);

        //3.7：1、3、5语文课尽量往前排，2、4、6英语课尽量往前排
        TimetableConstraintUtils.englishAndChinesePreferenceConstraint(model, x, lessonList, dayPerWeek, timeSlotPerDay);

        //3.8：体育课不能排在上午前两节
        TimetableConstraintUtils.sportsTimeSlotLimitConstraint(model, x, lessonList, dayPerWeek);

        //3.9：体育课尽量排在周4，5，6
        TimetableConstraintUtils.sportsWeekLimitConstraint(model, x, lessonList, dayPerWeek, timeSlotPerDay);

        //3.10 每天的第1、2、6节课必须排课
        TimetableConstraintUtils.timeSlotMustConstraint(model, x, lessonList, dayPerWeek);


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
