package cn.iocoder.yudao.module.school.service.teacher;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherPageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;

/**
 * 教师Service接口
 * @author yangzy
 */
public interface TeacherService {
    /**
     * 创建教师
     * @param reqVO 教师信息
     * @return 教师编号
     */
    Long createTeacher(TeacherSaveReqVO reqVO);

    /**
     * 更新教师
     * @param reqVO 教师信息
     */
    void updateTeacher(TeacherSaveReqVO reqVO);

    /**
     * 删除教师信息
     * @param id 教师编号
     */
    void deleteTeacher(Long id);

    /**
     * 获取教师分页列表
     * @param pageReqVO 分页条件
     * @return 教师分页列表
     */
    PageResult<TeacherDO> getTeacherPage(TeacherPageReqVO pageReqVO);

    /**
     * 获取教师信息
     * @param id 教师编号
     * @return 教师信息
     */
    TeacherDO getTeacher(Long id);
}
