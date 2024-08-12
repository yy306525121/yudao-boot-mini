package cn.iocoder.yudao.module.school.service.timetable;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.TimetablePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.TimetableSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableDO;
import cn.iocoder.yudao.module.school.timefold.domain.TimeTableProblem;
import jakarta.validation.Valid;

import java.util.List;

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

    List<TimetableDO> getTimetableList();

    TimeTableProblem generateProblem(Long id);
}
