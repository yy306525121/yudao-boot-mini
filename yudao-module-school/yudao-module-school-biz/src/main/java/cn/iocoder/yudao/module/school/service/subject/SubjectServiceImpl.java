package cn.iocoder.yudao.module.school.service.subject;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectPageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.mysql.subject.SubjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.SUBJECT_NOT_EXISTS;

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
    public Long createSubject(SubjectSaveReqVO createReqVO) {
        // 插入
        SubjectDO subject = BeanUtils.toBean(createReqVO, SubjectDO.class);
        subjectMapper.insert(subject);
        // 返回
        return subject.getId();
    }

    @Override
    public void updateSubject(SubjectSaveReqVO updateReqVO) {
        // 校验存在
        validateSubjectExists(updateReqVO.getId());
        // 更新
        SubjectDO updateObj = BeanUtils.toBean(updateReqVO, SubjectDO.class);
        subjectMapper.updateById(updateObj);
    }

    @Override
    public void deleteSubject(Long id) {
        // 校验存在
        validateSubjectExists(id);
        // 删除
        subjectMapper.deleteById(id);
    }

    private void validateSubjectExists(Long id) {
        if (subjectMapper.selectById(id) == null) {
            throw exception(SUBJECT_NOT_EXISTS);
        }
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
