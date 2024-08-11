package cn.iocoder.yudao.module.school.service.timetable;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.custom.TimetableCustomPageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.custom.TimetableCustomSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableCustomDO;
import jakarta.validation.Valid;

/**
 * 排课定制 Service 接口
 *
 * @author yangzy
 */
public interface TimetableCustomService {

    /**
     * 创建排课定制
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTimetableCustom(@Valid TimetableCustomSaveReqVO createReqVO);

    /**
     * 更新排课定制
     *
     * @param updateReqVO 更新信息
     */
    void updateTimetableCustom(@Valid TimetableCustomSaveReqVO updateReqVO);

    /**
     * 删除排课定制
     *
     * @param id 编号
     */
    void deleteTimetableCustom(Long id);

    /**
     * 获得排课定制
     *
     * @param id 编号
     * @return 排课定制
     */
    TimetableCustomDO getTimetableCustom(Long id);

    /**
     * 获得排课定制分页
     *
     * @param pageReqVO 分页查询
     * @return 排课定制分页
     */
    PageResult<TimetableCustomDO> getTimetableCustomPage(TimetableCustomPageReqVO pageReqVO);

}