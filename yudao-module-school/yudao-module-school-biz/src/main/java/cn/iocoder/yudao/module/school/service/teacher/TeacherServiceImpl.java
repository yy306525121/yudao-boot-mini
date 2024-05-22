package cn.iocoder.yudao.module.school.service.teacher;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherPageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.TeacherSubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.dal.mysql.subject.SubjectMapper;
import cn.iocoder.yudao.module.school.dal.mysql.subject.TeacherSubjectMapper;
import cn.iocoder.yudao.module.school.dal.mysql.teacher.TeacherMapper;
import com.google.common.annotations.VisibleForTesting;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.TEACHER_NOT_EXISTS;
import static cn.iocoder.yudao.module.system.enums.LogRecordConstants.*;

/**
 * 教师Service实现类
 * @author yangzy
 */
@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final TeacherMapper teacherMapper;
    private final TeacherSubjectMapper teacherSubjectMapper;
    private final SubjectMapper subjectMapper;

    @Override
    public Long createTeacher(TeacherSaveReqVO reqVO) {
        TeacherDO teacher = BeanUtils.toBean(reqVO, TeacherDO.class);
        teacherMapper.insert(teacher);

        Set<Long> subjectIds = reqVO.getSubjectIds();
        List<SubjectDO> subjectList = subjectMapper.selectListByIds(subjectIds);
        if (subjectList.size() != subjectIds.size()) {
            throw exception(TEACHER_NOT_EXISTS);
        }

        List<TeacherSubjectDO> list = new ArrayList<>(subjectIds.size());
        subjectIds.forEach(subjectId -> {
            TeacherSubjectDO teacherSubject = new TeacherSubjectDO();
            teacherSubject.setTeacherId(teacher.getId());
            teacherSubject.setSubjectId(subjectId);
            list.add(teacherSubject);
        });
        teacherSubjectMapper.insertBatch(list);

        return teacher.getId();
    }

    @Override
    public void updateTeacher(TeacherSaveReqVO reqVO) {
        validateTeacherExists(reqVO.getId());

        TeacherDO teacher = BeanUtils.toBean(reqVO, TeacherDO.class);
        teacherMapper.updateById(teacher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SCHOOL_TEACHER_TYPE, subType = SCHOOL_TEACHER_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = SCHOOL_TEACHER_DELETE_SUCCESS)
    public void deleteTeacher(Long id) {
        TeacherDO teacher = validateTeacherExists(id);
        teacherMapper.deleteById(id);
        teacherSubjectMapper.deleteByTeacherId(id);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("teacher", teacher);
    }

    @Override
    public PageResult<TeacherDO> getTeacherPage(TeacherPageReqVO pageReqVO) {
        return teacherMapper.selectPage(pageReqVO);
    }

    @Override
    public TeacherDO getTeacher(Long id) {
        return teacherMapper.selectById(id);
    }

    @VisibleForTesting
    TeacherDO validateTeacherExists(Long id) {
        if (id == null) {
            return null;
        }

        TeacherDO teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw exception(TEACHER_NOT_EXISTS);
        }
        return teacher;
    }
}
