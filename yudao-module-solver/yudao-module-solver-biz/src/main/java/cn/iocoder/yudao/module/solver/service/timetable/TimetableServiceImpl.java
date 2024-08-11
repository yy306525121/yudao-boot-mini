package cn.iocoder.yudao.module.solver.service.timetable;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.solver.controller.admin.timetable.vo.TimetablePageReqVO;
import cn.iocoder.yudao.module.solver.controller.admin.timetable.vo.TimetableSaveReqVO;
import cn.iocoder.yudao.module.solver.dal.dataobject.timetable.TimetableDO;
import cn.iocoder.yudao.module.solver.dal.mysql.timetable.TimetableMapper;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.solver.enums.ErrorCodeConstants.TIMETABLE_NAME_DUPLICATE;
import static cn.iocoder.yudao.module.solver.enums.ErrorCodeConstants.TIMETABLE_NOT_EXISTS;
import static cn.iocoder.yudao.module.solver.enums.LogRecordConstants.*;

/**
 * 排课 Service 实现类
 */
@Service
@Validated
@RequiredArgsConstructor
public class TimetableServiceImpl implements TimetableService {
    private final TimetableMapper timetableMapper;

    @Override
    @LogRecord(type = SOLVER_TIMETABLE_TYPE, subType = SOLVER_TIMETABLE_CREATE_SUB_TYPE, bizNo = "{{#timetable.id}}", success = SOLVER_TIMETABLE_CREATE_SUCCESS)
    public Long createTimetable(TimetableSaveReqVO createReqVO) {
        validateNameDuplicate(createReqVO.getName());
        // 插入
        TimetableDO timetable = BeanUtils.toBean(createReqVO, TimetableDO.class);
        timetableMapper.insert(timetable);

        // 记录日志
        LogRecordContext.putVariable("timetable", timetable);

        // 返回
        return timetable.getId();
    }

    @Override
    @LogRecord(type = SOLVER_TIMETABLE_TYPE, subType = SOLVER_TIMETABLE_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
        success = SOLVER_TIMETABLE_UPDATE_SUCCESS)
    public void updateTimetable(TimetableSaveReqVO updateReqVO) {
        // 校验存在
        TimetableDO oldTimeTable = validateTimetableExists(updateReqVO.getId());
        // 更新
        TimetableDO updateObj = BeanUtils.toBean(updateReqVO, TimetableDO.class);
        timetableMapper.updateById(updateObj);

        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldTimeTable, TimetableSaveReqVO.class));
        LogRecordContext.putVariable("timetable", updateObj);
    }

    @Override
    @LogRecord(type = SOLVER_TIMETABLE_TYPE, subType = SOLVER_TIMETABLE_DELETE_SUB_TYPE, bizNo = "{{#id}}", success = SOLVER_TIMETABLE_DELETE_SUCCESS)
    public void deleteTimetable(Long id) {
        // 校验存在
        TimetableDO timetable = validateTimetableExists(id);
        // 删除
        timetableMapper.deleteById(id);

        LogRecordContext.putVariable("timetable", timetable);
    }

    private void validateNameDuplicate(String name) {
        if (timetableMapper.selectByName(name) != null) {
            throw exception(TIMETABLE_NAME_DUPLICATE);
        }
    }
    private TimetableDO validateTimetableExists(Long id) {
        TimetableDO timetable = timetableMapper.selectById(id);
        if (timetable == null) {
            throw exception(TIMETABLE_NOT_EXISTS);
        }
        return timetable;
    }

    @Override
    public TimetableDO getTimetable(Long id) {
        return timetableMapper.selectById(id);
    }

    @Override
    public PageResult<TimetableDO> getTimetablePage(TimetablePageReqVO pageReqVO) {
        return timetableMapper.selectPage(pageReqVO);
    }
}
