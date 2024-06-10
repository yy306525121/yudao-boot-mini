package cn.iocoder.yudao.module.school.service.course;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.TimeSlotListReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.TimeSlotPageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.TimeSlotSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.mysql.course.TimeSlotMapper;
import com.google.common.annotations.VisibleForTesting;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.TIME_SLOT_NOT_EXISTS;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.TIME_SLOT_SORT_DUPLICATE;
import static cn.iocoder.yudao.module.school.enums.LogRecordConstants.*;

/**
 * 课程节次 Service 实现类
 *
 * @author yangzy
 */
@Service
@Validated
public class TimeSlotServiceImpl implements TimeSlotService {

    @Resource
    private TimeSlotMapper timeSlotMapper;

    @Override
    @LogRecord(type = SCHOOL_TIME_SLOT_TYPE, subType = SCHOOL_TIME_SLOT_CREATE_SUB_TYPE, bizNo = "{{#timeSlot.id}}", success = SCHOOL_TIME_SLOT_CREATE_SUCCESS)
    public Long createTimeSlot(TimeSlotSaveReqVO createReqVO) {
        validateTimeSlotSortUnique(null, createReqVO.getSort());
        // 插入
        TimeSlotDO timeSlot = BeanUtils.toBean(createReqVO, TimeSlotDO.class);
        timeSlotMapper.insert(timeSlot);

        LogRecordContext.putVariable("timeSlot", timeSlot);

        // 返回
        return timeSlot.getId();
    }

    @Override
    @LogRecord(type = SCHOOL_TIME_SLOT_TYPE, subType = SCHOOL_TIME_SLOT_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}", success = SCHOOL_TIME_SLOT_UPDATE_SUCCESS)
    public void updateTimeSlot(TimeSlotSaveReqVO updateReqVO) {
        // 校验存在
        TimeSlotDO oldTimeSlot = validateTimeSlotExists(updateReqVO.getId());
        // 校验节次顺序唯一性
        validateTimeSlotSortUnique(updateReqVO.getId(), updateReqVO.getSort());

        // 更新
        TimeSlotDO timeSlot = BeanUtils.toBean(updateReqVO, TimeSlotDO.class);
        timeSlotMapper.updateById(timeSlot);

        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldTimeSlot, TimeSlotSaveReqVO.class));
        LogRecordContext.putVariable("timeSlot", oldTimeSlot);
    }

    @Override
    @LogRecord(type = SCHOOL_TIME_SLOT_TYPE, subType = SCHOOL_TIME_SLOT_DELETE_SUB_TYPE, bizNo = "{{#id}}", success = SCHOOL_TIME_SLOT_DELETE_SUCCESS)
    public void deleteTimeSlot(Long id) {
        // 校验存在
        TimeSlotDO timeSlot = validateTimeSlotExists(id);
        // 删除
        timeSlotMapper.deleteById(id);

        LogRecordContext.putVariable("timeSlot", timeSlot);
    }

    @VisibleForTesting
    TimeSlotDO validateTimeSlotExists(Long id) {
        if (id == null) {
            return null;
        }
        TimeSlotDO timeSlot = timeSlotMapper.selectById(id);

        if (timeSlot == null) {
            throw exception(TIME_SLOT_NOT_EXISTS);
        }

        return timeSlot;
    }

    @Override
    public TimeSlotDO getTimeSlot(Long id) {
        return timeSlotMapper.selectById(id);
    }

    @Override
    public PageResult<TimeSlotDO> getTimeSlotPage(TimeSlotPageReqVO pageReqVO) {
        return timeSlotMapper.selectPage(pageReqVO);
    }

    @Override
    public List<TimeSlotDO> getTimeSlotList(TimeSlotListReqVO listReqVO) {
        return timeSlotMapper.selectList(listReqVO);
    }

    @Override
    public TimeSlotDO getTimeSlotBySort(Integer sort) {
        return timeSlotMapper.selectOneBySort(sort);
    }

    @Override
    public List<TimeSlotDO> getTimeSlotByType(Integer type) {
        return timeSlotMapper.selectListByType(type);
    }

    @Override
    public List<TimeSlotDO> getAll() {
        return timeSlotMapper.selectList();
    }

    @Override
    public List<TimeSlotDO> getTimeSlotListByIds(List<Long> timeSlotIds) {
        if (CollUtil.isEmpty(timeSlotIds)) {
            return Collections.emptyList();
        }
        return timeSlotMapper.selectBatchIds(timeSlotIds);
    }

    @Override
    public TimeSlotDO getLastTimeSlot() {
        return timeSlotMapper.selectLast();
    }

    @Override
    public TimeSlotDO getFirstTimeSlot() {
        return timeSlotMapper.selectFirst();
    }


    private void validateTimeSlotSortUnique(@Nullable Long id, Integer sort) {
        TimeSlotDO timeSlot = timeSlotMapper.selectBySort(sort);
        if (timeSlot == null) {
            return;
        }

        if (id == null) {
            throw exception(TIME_SLOT_SORT_DUPLICATE);
        } else {
            if (!timeSlot.getSort().equals(timeSlotMapper.selectById(id).getSort())) {
                throw exception(TIME_SLOT_SORT_DUPLICATE);
            }
        }
    }
}