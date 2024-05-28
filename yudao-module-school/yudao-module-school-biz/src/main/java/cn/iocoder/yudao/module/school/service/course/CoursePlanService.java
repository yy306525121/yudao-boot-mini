package cn.iocoder.yudao.module.school.service.course;

import cn.iocoder.yudao.module.school.controller.admin.course.vo.CoursePlanChangeReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CoursePlanSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CoursePlanDO;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

/**
 * 课程计划 Service 接口
 *
 * @author yangzy
 */
public interface CoursePlanService {

    /**
     * 创建课程计划
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCoursePlan(@Valid CoursePlanSaveReqVO createReqVO);

    /**
     * 更新课程计划
     *
     * @param updateReqVO 更新信息
     */
    void updateCoursePlan(@Valid CoursePlanSaveReqVO updateReqVO);

    /**
     * 删除课程计划
     *
     * @param id 编号
     */
    void deleteCoursePlan(Long id);

    /**
     * 获得课程计划
     *
     * @param id 编号
     * @return 课程计划
     */
    CoursePlanDO getCoursePlan(Long id);

    /**
     * 批量创建课程计划
     * @param coursePlanList 课程计划列表
     */
    void createCoursePlan(List<CoursePlanDO> coursePlanList);

    /**
     * 查询课程计划
     * @param gradeId 年级
     * @param teacherId 教师
     * @param courseTypeId 课程类型编号
     * @param subjectId 科目编号
     * @param date 日期
     * @return 课程计划列表
     */
    List<CoursePlanDO> getCoursePlanList(Long gradeId, Long teacherId, Long courseTypeId, Long subjectId, LocalDate date);

    /**
     * 课程调整
     * @param reqVO 课程调整信息
     */
    void changeCoursePlan(CoursePlanChangeReqVO reqVO);
}