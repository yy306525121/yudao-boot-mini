package cn.iocoder.yudao.module.school.service.timetable;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.custom.TimetableCustomPageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.custom.TimetableCustomSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableCustomDO;
import cn.iocoder.yudao.module.school.dal.mysql.timetable.TimetableCustomMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.TIMETABLE_CUSTOM_NOT_EXISTS;

/**
 * 排课定制 Service 实现类
 *
 * @author yangzy
 */
@Service
@Validated
public class TimetableCustomServiceImpl implements TimetableCustomService {

    @Resource
    private TimetableCustomMapper timetableCustomMapper;

    @Override
    public Long createTimetableCustom(TimetableCustomSaveReqVO createReqVO) {
        // 插入
        TimetableCustomDO timetableCustom = BeanUtils.toBean(createReqVO, TimetableCustomDO.class);
        timetableCustomMapper.insert(timetableCustom);
        // 返回
        return timetableCustom.getId();
    }

    @Override
    public void updateTimetableCustom(TimetableCustomSaveReqVO updateReqVO) {
        // 校验存在
        validateTimetableCustomExists(updateReqVO.getId());
        // 更新
        TimetableCustomDO updateObj = BeanUtils.toBean(updateReqVO, TimetableCustomDO.class);
        timetableCustomMapper.updateById(updateObj);
    }

    @Override
    public void deleteTimetableCustom(Long id) {
        // 校验存在
        validateTimetableCustomExists(id);
        // 删除
        timetableCustomMapper.deleteById(id);
    }

    private void validateTimetableCustomExists(Long id) {
        if (timetableCustomMapper.selectById(id) == null) {
            throw exception(TIMETABLE_CUSTOM_NOT_EXISTS);
        }
    }

    @Override
    public TimetableCustomDO getTimetableCustom(Long id) {
        return timetableCustomMapper.selectById(id);
    }

    @Override
    public PageResult<TimetableCustomDO> getTimetableCustomPage(TimetableCustomPageReqVO pageReqVO) {
        return timetableCustomMapper.selectPage(pageReqVO);
    }

}