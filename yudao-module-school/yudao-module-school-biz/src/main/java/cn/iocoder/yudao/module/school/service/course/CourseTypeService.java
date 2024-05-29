package cn.iocoder.yudao.module.school.service.course;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseTypePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseTypeSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 课程类型 Service 接口
 *
 * @author yangzy
 */
public interface CourseTypeService {

    /**
     * 创建课程类型
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCourseType(@Valid CourseTypeSaveReqVO createReqVO);

    /**
     * 更新课程类型
     *
     * @param updateReqVO 更新信息
     */
    void updateCourseType(@Valid CourseTypeSaveReqVO updateReqVO);

    /**
     * 删除课程类型
     *
     * @param id 编号
     */
    void deleteCourseType(Long id);

    /**
     * 获得课程类型
     *
     * @param id 编号
     * @return 课程类型
     */
    CourseTypeDO getCourseType(Long id);

    /**
     * 获得课程类型分页
     *
     * @param pageReqVO 分页查询
     * @return 课程类型分页
     */
    PageResult<CourseTypeDO> getCourseTypePage(CourseTypePageReqVO pageReqVO);

    /**
     * 获取课程类型信息
     * @param name 课程类型名称
     * @return 课程类型信息
     */
    CourseTypeDO getCourseType(String name);

    /**
     * 获取所有的课程类型列表数据
     * @return 所有的课程类型列表
     */
    List<CourseTypeDO> getAll();

    CourseTypeDO getCourseTypeByType(int type);
}