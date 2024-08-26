package cn.iocoder.yudao.module.school.service.timetable;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.TimetablePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.TimetableSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
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

import java.util.*;
import java.util.stream.Collectors;

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
    public void solve(Long timetableId) {
        Loader.loadNativeLibraries();
        int numDays = 6;
        int periodsPerDay = 9;
        int totalTimeSlots = numDays * periodsPerDay;

        List<Lesson> lessonList = generateProblem(timetableId);

        List<TeacherDO> teacherList = lessonList.stream().map(Lesson::getTeacher).distinct().collect(Collectors.toList());
        List<GradeDO> gradeList = lessonList.stream().map(Lesson::getGrade).distinct().collect(Collectors.toList());
        List<SubjectDO> subjectList = lessonList.stream().map(Lesson::getSubject).distinct().collect(Collectors.toList());

        //1：建模
        CpModel model = new CpModel();

        //2：准备数据
        //2.1：每个班级的课程
        List<IntVar> lessonTimeSlot = new ArrayList<>();
        Map<Lesson, IntVar> lessonToTimeSlot = new HashMap<>();
        for (Lesson lesson : lessonList) {
            // 创建课程的时间槽变量
            IntVar timeSlot = model.newIntVar(0, totalTimeSlots - 1, "lesson：" + lesson.toString());
            lessonToTimeSlot.put(lesson, timeSlot);
            lessonTimeSlot.add(timeSlot);

            // 如果是连堂课，创建第二个时间槽变量并添加连堂约束
            if (lesson.isContinuousFlag()) {
                IntVar nextTimeSlot = model.newIntVar(0, totalTimeSlots - 1, "lesson_next：" + lesson.toString());
                lessonToTimeSlot.put(lesson, nextTimeSlot);
                lessonTimeSlot.add(nextTimeSlot);
                model.addEquality(LinearExpr.weightedSum(new IntVar[]{nextTimeSlot, timeSlot}, new long[]{1, -1}), 1);
                // TODO 判断 timeSlot % periodsPerDay != periodsPerDay - 1;
            }
        }

        Map<GradeDO, List<Lesson>> gradeLessonMap = lessonList.stream().collect(Collectors.groupingBy(Lesson::getGrade));
        //4：约束条件
        //4.1：同一个班级的课程不能在同一个时间槽上
        for (GradeDO grade : gradeList) {
            List<IntVar> gradeTimeSlot = new ArrayList<>();
            List<Lesson> lessons = gradeLessonMap.get(grade);
            for (Lesson lesson : lessons) {
                gradeTimeSlot.add(lessonToTimeSlot.get(lesson));
            }
            model.addAllDifferent(gradeTimeSlot);
        }
        //4.2：同一个教师，课程时间槽不能相同
        Map<Long, List<IntVar>> teacherTimeSlots = new HashMap<>();
        for (Lesson lesson : lessonList) {
            Long teacherId = lesson.getTeacher().getId();
            teacherTimeSlots.computeIfAbsent(teacherId, k -> new ArrayList<>()).add(lessonToTimeSlot.get(lesson));
        }
        // 为每个教师添加约束，确保他们的课程不会在同一时间槽上
        for (Map.Entry<Long, List<IntVar>> entry : teacherTimeSlots.entrySet()) {
            List<IntVar> timeSlot = entry.getValue();
            model.addAllDifferent(timeSlot);
        }

        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);
        // 处理结果
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            for (Lesson lesson : lessonList) {
                int timeSlot = (int) solver.value(lessonToTimeSlot.get(lesson));
                int day = timeSlot / periodsPerDay;
                int period = timeSlot % periodsPerDay;
                System.out.println("课程 " + lesson.getSubject().getName() + " (教师 " + lesson.getTeacher().getName() + ", 班级 " + lesson.getGrade().getName() + "): 星期 " + (day + 1) + ", 第 " + (period + 1) + " 节");
            }
        } else {
            System.out.println("未找到可行解。");
        }
    }
}
