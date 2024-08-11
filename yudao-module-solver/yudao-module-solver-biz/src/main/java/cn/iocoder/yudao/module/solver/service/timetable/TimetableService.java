package cn.iocoder.yudao.module.solver.service.timetable;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.solver.controller.admin.timetable.vo.TimetablePageReqVO;
import cn.iocoder.yudao.module.solver.controller.admin.timetable.vo.TimetableSaveReqVO;
import cn.iocoder.yudao.module.solver.dal.dataobject.timetable.TimetableDO;
import jakarta.validation.Valid;

public interface TimetableService {
    /**
     * 创建排课
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTimetable(@Valid TimetableSaveReqVO createReqVO);

    /**
     * 更新排课
     *
     * @param updateReqVO 更新信息
     */
    void updateTimetable(@Valid TimetableSaveReqVO updateReqVO);

    /**
     * 删除排课
     *
     * @param id 编号
     */
    void deleteTimetable(Long id);

    /**
     * 获得排课
     *
     * @param id 编号
     * @return 排课
     */
    TimetableDO getTimetable(Long id);

    /**
     * 获得排课分页
     *
     * @param pageReqVO 分页查询
     * @return 排课分页
     */
    PageResult<TimetableDO> getTimetablePage(TimetablePageReqVO pageReqVO);
}
