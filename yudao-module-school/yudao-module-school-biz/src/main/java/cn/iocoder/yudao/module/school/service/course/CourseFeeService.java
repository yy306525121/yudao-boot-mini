package cn.iocoder.yudao.module.school.service.course;

import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseFeeSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import jakarta.validation.Valid;

/**
 * 课时费明细 Service 接口
 *
 * @author yangzy
 */
public interface CourseFeeService {

    /**
     * 创建课时费明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCourseFee(@Valid CourseFeeSaveReqVO createReqVO);


    /**
     * 获得课时费明细
     *
     * @param id 编号
     * @return 课时费明细
     */
    CourseFeeDO getCourseFee(Long id);

}