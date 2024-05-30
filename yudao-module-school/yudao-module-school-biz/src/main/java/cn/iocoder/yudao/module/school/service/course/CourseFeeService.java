package cn.iocoder.yudao.module.school.service.course;

import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseFeeListReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * 课时费明细 Service 接口
 *
 * @author yangzy
 */
public interface CourseFeeService {

    /**
     * 获得课时费明细
     *
     * @param id 编号
     * @return 课时费明细
     */
    CourseFeeDO getCourseFee(Long id);

    /**
     * 获取课时费列表
     * @param listReqVO 查询条件
     * @return 课时费列表
     */
    List<CourseFeeDO> getCourseFeeList(CourseFeeListReqVO listReqVO);

    /**
     * 删除课时费
     * @param teacherId 教师编号
     * @param start 删除范围开始日期
     * @param end 删除范围结束日期
     */
    void removeCourseFee(Long teacherId, LocalDate start, LocalDate end);

    /**
     * 批量创建课时费
     * @param courseFeeList 课时费
     */
    void createCourseFeeBatch(Collection<CourseFeeDO> courseFeeList);
}