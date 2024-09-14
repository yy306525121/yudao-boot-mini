package cn.iocoder.yudao.module.school.service.timetable;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting.TimetableSettingPageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting.TimetableSettingSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableSettingDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 排课计划设置 Service 接口
 *
 * @author yangzy
 */
public interface TimetableSettingService {

    /**
     * 创建排课计划设置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTimetableSetting(@Valid TimetableSettingSaveReqVO createReqVO);

    void createTimetableSettingBatch(List<TimetableSettingDO> list);

    /**
     * 更新排课计划设置
     *
     * @param updateReqVO 更新信息
     */
    void updateTimetableSetting(@Valid TimetableSettingSaveReqVO updateReqVO);

    /**
     * 删除排课计划设置
     *
     * @param id 编号
     */
    void deleteTimetableSetting(Long id);

    /**
     * 获得排课计划设置
     *
     * @param id 编号
     * @return 排课计划设置
     */
    TimetableSettingDO getTimetableSetting(Long id);

    /**
     * 获得排课计划设置分页
     *
     * @param pageReqVO 分页查询
     * @return 排课计划设置分页
     */
    PageResult<TimetableSettingDO> getTimetableSettingPage(TimetableSettingPageReqVO pageReqVO);

}