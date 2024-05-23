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
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.TEACHER_NAME_DUPLICATE;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.TEACHER_NOT_EXISTS;
import static cn.iocoder.yudao.module.school.enums.LogRecordConstants.*;

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
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SCHOOL_TEACHER_TYPE, subType = SCHOOL_TEACHER_CREATE_SUB_TYPE, bizNo = "{{#teacher.id}}",
            success = SCHOOL_TEACHER_CREATE_SUCCESS)
    public Long createTeacher(TeacherSaveReqVO reqVO) {
        validateTeacherNameUnique(null, reqVO.getName());

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

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("teacher", teacher);
        return teacher.getId();
    }



    @Override
    @LogRecord(type = SCHOOL_TEACHER_TYPE, subType = SCHOOL_TEACHER_UPDATE_SUB_TYPE, bizNo = "{{#reqVO.id}}",
            success = SCHOOL_TEACHER_UPDATE_SUCCESS)
    public void updateTeacher(TeacherSaveReqVO reqVO) {
        TeacherDO oldTeacher = validateTeacherExists(reqVO.getId());

        validateTeacherNameUnique(reqVO.getId(), reqVO.getName());

        TeacherDO teacher = BeanUtils.toBean(reqVO, TeacherDO.class);
        teacherMapper.updateById(teacher);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldTeacher, TeacherSaveReqVO.class));
        LogRecordContext.putVariable("teacher", oldTeacher);
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

    private void validateTeacherNameUnique(@Nullable Long id, String name) {
        TeacherDO teacher = teacherMapper.selectByName(name);
        if (teacher == null) {
            return;
        }

        if (id == null) {
            // 如果id为空，表示新增，比较时不用排除自身
            throw exception(TEACHER_NAME_DUPLICATE);
        } else {
            // 如果id不为空，表示更新，检查时需要排除自身
            if (!Objects.equals(teacherMapper.selectById(id).getId(), teacher.getId())) {
                throw exception(TEACHER_NAME_DUPLICATE);
            }
        }
    }
}
