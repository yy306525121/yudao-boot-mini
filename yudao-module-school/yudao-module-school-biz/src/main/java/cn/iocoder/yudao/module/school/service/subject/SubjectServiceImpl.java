package cn.iocoder.yudao.module.school.service.subject;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectPageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.mysql.subject.SubjectMapper;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.SUBJECT_NOT_EXISTS;
import static cn.iocoder.yudao.module.school.enums.LogRecordConstants.*;

/**
 * 科目Service实现类
 * @author yangzy
 */
@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final SubjectMapper subjectMapper;


    @Override
    @LogRecord(type = SCHOOL_SUBJECT_TYPE, subType = SCHOOL_SUBJECT_CREATE_SUB_TYPE, bizNo = "{{#subject.id}}",
            success = SCHOOL_SUBJECT_CREATE_SUCCESS)
    public Long createSubject(SubjectSaveReqVO createReqVO) {
        // 插入
        SubjectDO subject = BeanUtils.toBean(createReqVO, SubjectDO.class);
        subjectMapper.insert(subject);

        // 记录操作日志
        LogRecordContext.putVariable("subject", subject);

        // 返回
        return subject.getId();
    }

    @Override
    @LogRecord(type = SCHOOL_SUBJECT_TYPE, subType = SCHOOL_SUBJECT_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}", success = SCHOOL_SUBJECT_UPDATE_SUCCESS)
    public void updateSubject(SubjectSaveReqVO updateReqVO) {
        // 校验存在
        SubjectDO oldSubject = validateSubjectExists(updateReqVO.getId());
        // 更新
        SubjectDO subject = BeanUtils.toBean(updateReqVO, SubjectDO.class);
        subjectMapper.updateById(subject);

        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldSubject, SubjectSaveReqVO.class));
        LogRecordContext.putVariable("subject", subject);
    }

    @Override
    @LogRecord(type = SCHOOL_SUBJECT_TYPE, subType = SCHOOL_SUBJECT_DELETE_SUB_TYPE, bizNo = "{{#id}}", success = SCHOOL_SUBJECT_DELETE_SUCCESS)
    public void deleteSubject(Long id) {
        // 校验存在
        SubjectDO subject = validateSubjectExists(id);
        // 删除
        subjectMapper.deleteById(id);

        LogRecordContext.putVariable("subject", subject);
    }

    private SubjectDO validateSubjectExists(Long id) {
        if (id == null) {
            return null;
        }
        SubjectDO subject = subjectMapper.selectById(id);
        if (subject == null) {
            throw exception(SUBJECT_NOT_EXISTS);
        }

        return subject;
    }

    @Override
    public SubjectDO getSubject(Long id) {
        return subjectMapper.selectById(id);
    }

    @Override
    public PageResult<SubjectDO> getSubjectPage(SubjectPageReqVO pageReqVO) {
        return subjectMapper.selectPage(pageReqVO);
    }

    @Override
    public List<SubjectDO> getSubjectList(Collection<Long> ids) {
        return subjectMapper.selectList(ids);
    }
}
