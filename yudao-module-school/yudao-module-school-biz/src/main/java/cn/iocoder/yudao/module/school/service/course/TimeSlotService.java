package cn.iocoder.yudao.module.school.service.course;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.TimeSlotListReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.TimeSlotPageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.TimeSlotSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import jakarta.validation.Valid;

import java.util.List;

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

    /**
     * 获取课程节次信息
     * @param sort 课程节次序号
     * @return 课程节次信息
     */
    TimeSlotDO getTimeSlotBySort(Integer sort);

    /**
     * 获取课程节次列表数据
     * @param type 课程节次类型
     * @return 课程节次列表数据
     */
    List<TimeSlotDO> getTimeSlotByType(Integer type);

    /**
     * 获取所有课程节次数据
     * @return 所有课程节次信息列表
     */
    List<TimeSlotDO> getAll();

    /**
     * 获取节次信息
     * @param timeSlotIds 节次编号集合
     * @return 节次信息列表
     */
    List<TimeSlotDO> getTimeSlotListByIds(List<Long> timeSlotIds);

    /**
     * 获取最后一节课的节次信息
     * @return 最后一节课的节次信息
     */
    TimeSlotDO getLastTimeSlot();

    /**
     * 获取第一节课的节次信息
     * @return 第一节课的节次信息
     */
    TimeSlotDO getFirstTimeSlot();

}