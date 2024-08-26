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
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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
                    lesson.setContinuousUuid(uuid);
                    // lesson.setPreferWeeks(timetableSetting.getPreferWeeks());
                    // lesson.setPreferTimeSlotIds(timetableSetting.getPreferTimeSlotIds());

                    lessonList.add(lesson);
                }
            }
        }

        return lessonList;
    }
}
