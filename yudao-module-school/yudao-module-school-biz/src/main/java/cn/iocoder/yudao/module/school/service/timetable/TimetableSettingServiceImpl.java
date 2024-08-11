package cn.iocoder.yudao.module.school.service.timetable;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting.TimetableSettingPageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting.TimetableSettingSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableSettingDO;
import cn.iocoder.yudao.module.school.dal.mysql.timetable.TimetableSettingMapper;
import cn.iocoder.yudao.module.school.service.subject.SubjectService;
import cn.iocoder.yudao.module.school.service.teacher.TeacherService;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.school.enums.LogRecordConstants.*;

/**
 * 排课计划设置 Service 实现类
 *
 * @author yangzy
 */
@Service
@Validated
@RequiredArgsConstructor
public class TimetableSettingServiceImpl implements TimetableSettingService {
    private final TimetableSettingMapper timetableSettingMapper;
    private final TeacherService teacherService;
    private final SubjectService subjectService;


    @Override
    @LogRecord(type = SCHOOL_TIMETABLE_SETTING_TYPE, subType = SCHOOL_TIMETABLE_SETTING_CREATE_SUB_TYPE, bizNo = "{{#timetableSetting.id}}",
            success = SCHOOL_TIMETABLE_SETTING_CREATE_SUCCESS, extra = "{{timetableSetting.toString()}}")
    public Long createTimetableSetting(TimetableSettingSaveReqVO createReqVO) {
        validateTimetableSettingDuplicate(createReqVO.getTimetableId(), createReqVO.getGradeId(), createReqVO.getSubjectId(), createReqVO.getCourseTypeId());

        // 检查教师是否可以教授此科目
        validateTeacherSubject(createReqVO.getTeacherId(), createReqVO.getSubjectId());

        // 插入
        TimetableSettingDO timetableSetting = BeanUtils.toBean(createReqVO, TimetableSettingDO.class);
        timetableSettingMapper.insert(timetableSetting);

        LogRecordContext.putVariable("timetableSetting", timetableSetting);
        // 返回
        return timetableSetting.getId();
    }

    @Override
    @LogRecord(type = SCHOOL_TIMETABLE_SETTING_TYPE, subType = SCHOOL_TIMETABLE_SETTING_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = SCHOOL_TIMETABLE_SETTING_UPDATE_SUCCESS)
    public void updateTimetableSetting(TimetableSettingSaveReqVO updateReqVO) {
        // 校验存在
        TimetableSettingDO oldTimetableSetting = validateTimetableSettingExists(updateReqVO.getId());

        // 检查教师是否可以教授此科目
        validateTeacherSubject(updateReqVO.getTeacherId(), updateReqVO.getSubjectId());

        // 更新
        TimetableSettingDO updateObj = BeanUtils.toBean(updateReqVO, TimetableSettingDO.class);
        timetableSettingMapper.updateById(updateObj);

        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldTimetableSetting, TimetableSettingSaveReqVO.class));
    }

    @Override
    @LogRecord(type = SCHOOL_TIMETABLE_SETTING_TYPE, subType = SCHOOL_TIMETABLE_SETTING_DELETE_SUB_TYPE, bizNo = "{{#id}}",
        success = SCHOOL_TIMETABLE_SETTING_DELETE_SUCCESS)
    public void deleteTimetableSetting(Long id) {
        // 校验存在
        validateTimetableSettingExists(id);
        // 删除
        timetableSettingMapper.deleteById(id);
    }

    private TimetableSettingDO validateTimetableSettingExists(Long id) {
        TimetableSettingDO timetableSetting = timetableSettingMapper.selectById(id);
        if (timetableSetting == null) {
            throw exception(TIMETABLE_SETTING_NOT_EXISTS);
        }
        return timetableSetting;
    }

    private void validateTimetableSettingDuplicate(Long timetableId, Long gradeId, Long subjectId, Long courseTypeId) {
        if (timetableSettingMapper.selectOneByParams(timetableId, gradeId, subjectId, courseTypeId) != null) {
            throw exception(TIMETABLE_SETTING_DUPLICATE);
        }
    }
    private void validateTeacherSubject(Long teacherId, Long subjectId) {
        TeacherDO teacher = teacherService.getTeacher(teacherId);
        SubjectDO subject = subjectService.getSubject(subjectId);
        if (!teacherService.hasSubject(teacherId, subjectId)) {
            throw exception(TEACHER_SUBJECT_NOT_EXISTS, teacher.getName(), subject.getName());
        }
    }

    @Override
    public TimetableSettingDO getTimetableSetting(Long id) {
        return timetableSettingMapper.selectById(id);
    }

    @Override
    public PageResult<TimetableSettingDO> getTimetableSettingPage(TimetableSettingPageReqVO pageReqVO) {
        return timetableSettingMapper.selectPage(pageReqVO);
    }

}