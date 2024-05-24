package cn.iocoder.yudao.module.school.service.course;

import java.util.*;

import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import jakarta.validation.*;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.*;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 课程节次 Service 接口
 *
 * @author yangzy
 */
public interface TimeSlotService {

    /**
     * 创建课程节次
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTimeSlot(@Valid TimeSlotSaveReqVO createReqVO);

    /**
     * 更新课程节次
     *
     * @param updateReqVO 更新信息
     */
    void updateTimeSlot(@Valid TimeSlotSaveReqVO updateReqVO);

    /**
     * 删除课程节次
     *
     * @param id 编号
     */
    void deleteTimeSlot(Long id);

    /**
     * 获得课程节次
     *
     * @param id 编号
     * @return 课程节次
     */
    TimeSlotDO getTimeSlot(Long id);

    /**
     * 获得课程节次分页
     *
     * @param pageReqVO 分页查询
     * @return 课程节次分页
     */
    PageResult<TimeSlotDO> getTimeSlotPage(TimeSlotPageReqVO pageReqVO);

    /**
     * 获取课程节次列表
     * @param listReqVO 查询条件
     * @return 课程节次列表
     */
    List<TimeSlotDO> getTimeSlotList(TimeSlotListReqVO listReqVO);
}