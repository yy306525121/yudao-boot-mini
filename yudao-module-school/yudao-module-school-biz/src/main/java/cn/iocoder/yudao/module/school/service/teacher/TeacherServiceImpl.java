package cn.iocoder.yudao.module.school.service.teacher;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherListReqVO;
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
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
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
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SCHOOL_TEACHER_TYPE, subType = SCHOOL_TEACHER_UPDATE_SUB_TYPE, bizNo = "{{#reqVO.id}}",
            success = SCHOOL_TEACHER_UPDATE_SUCCESS)
    public void updateTeacher(TeacherSaveReqVO reqVO) {
        TeacherDO oldTeacher = validateTeacherExists(reqVO.getId());

        validateTeacherNameUnique(reqVO.getId(), reqVO.getName());

        // 更新教师
        TeacherDO teacher = BeanUtils.toBean(reqVO, TeacherDO.class);
        teacherMapper.updateById(teacher);
        // 更新教师科目
        updateTeacherSubject(reqVO, teacher);


        // 3. 记录操作日志上下文
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldTeacher, TeacherSaveReqVO.class));
        LogRecordContext.putVariable("teacher", oldTeacher);
    }

    private void updateTeacherSubject(TeacherSaveReqVO reqVO, TeacherDO teacher) {
        Long teacherId = teacher.getId();
        Set<Long> dbSubjectIds = convertSet(teacherSubjectMapper.selectListByTeacherId(teacherId), TeacherSubjectDO::getSubjectId);
        // 计算新增和删除的科目编号
        Set<Long> subjectIds = CollUtil.emptyIfNull(teacher.getSubjectIds());
        Collection<Long> createSubjectIds = CollUtil.subtract(subjectIds, dbSubjectIds);
        Collection<Long> deleteSubjectIds = CollUtil.subtract(dbSubjectIds, subjectIds);
        // 执行新增和删除。对于已经授权的科目，不用做任何处理
        if (!CollectionUtil.isEmpty(createSubjectIds)) {
            teacherSubjectMapper.insertBatch(convertList(createSubjectIds,
                    subjectId -> new TeacherSubjectDO().setTeacherId(teacherId).setSubjectId(subjectId)));
        }
        if (!CollectionUtil.isEmpty(deleteSubjectIds)) {
            teacherSubjectMapper.deleteByTeacherIdAndSubjectIds(teacherId, deleteSubjectIds);
        }
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

    @Override
    public TeacherDO getTeacher(String name) {
        return teacherMapper.selectByName(name);
    }

    @Override
    public List<TeacherDO> getAll() {
        return teacherMapper.selectList();
    }

    @Override
    public List<TeacherDO> getTeacherList(TeacherListReqVO reqVO) {
        return teacherMapper.selectList(reqVO);
    }

    @Override
    public boolean hasSubject(Long teacherId, Long subjectId) {
        return teacherSubjectMapper.selectOne(teacherId, subjectId) != null;
    }

    @Override
    public List<TeacherDO> getTeacherListBySubjectIds(Collection<Long> subjectIds) {
        if (CollUtil.isEmpty(subjectIds)) {
            return Collections.emptyList();
        }
        Set<Long> teacherIds = convertSet(teacherSubjectMapper.selectListBySubjectIds(subjectIds), TeacherSubjectDO::getTeacherId);
        if (CollUtil.isEmpty(teacherIds)) {
            return Collections.emptyList();
        }
        return teacherMapper.selectBatchIds(teacherIds);
    }

    @Override
    public List<TeacherDO> getTeacherListByIds(Collection<Long> teacherIds) {
        if (CollUtil.isEmpty(teacherIds)) {
            return Collections.emptyList();
        }
        return teacherMapper.selectBatchIds(teacherIds);
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
