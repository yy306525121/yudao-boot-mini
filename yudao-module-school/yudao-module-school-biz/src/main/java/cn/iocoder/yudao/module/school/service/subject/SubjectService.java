package cn.iocoder.yudao.module.school.service.subject;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectPageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;

/**
 * 科目信息 Service 接口
 *
 * @author yangzy
 */
public interface SubjectService {
    /**
     * 创建科目信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSubject(@Valid SubjectSaveReqVO createReqVO);

    /**
     * 更新科目信息
     *
     * @param updateReqVO 更新信息
     */
    void updateSubject(@Valid SubjectSaveReqVO updateReqVO);

    /**
     * 删除科目信息
     *
     * @param id 编号
     */
    void deleteSubject(Long id);

    /**
     * 获得科目信息
     *
     * @param id 编号
     * @return 科目信息
     */
    SubjectDO getSubject(Long id);

    /**
     * 获得科目信息
     * @param name 科目名称
     * @return 科目信息
     */
    SubjectDO getSubject(String name);

    /**
     * 获得科目信息分页
     *
     * @param pageReqVO 分页查询
     * @return 科目信息分页
     */
    PageResult<SubjectDO> getSubjectPage(SubjectPageReqVO pageReqVO);

    /**
     * 获得符合条件的科目列表
     *
     * @param ids 科目编号数组。如果为空，不进行筛选
     * @return 科目列表
     */
    List<SubjectDO> getSubjectList(Collection<Long> ids);

    /**
     * 获取所有科目信息
     * @return 所有科目信息列表
     */
    List<SubjectDO> getAll();

}
