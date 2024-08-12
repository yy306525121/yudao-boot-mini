package cn.iocoder.yudao.module.school.service.timetable;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.result.TimetableResultSaveReqVO;
import cn.iocoder.yudao.module.school.convert.timetable.TimetableResultConvert;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableResultDO;
import cn.iocoder.yudao.module.school.dal.mysql.timetable.TimetableResultMapper;
import cn.iocoder.yudao.module.school.timefold.domain.Lesson;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.TIMETABLE_RESULT_NOT_EXISTS;

/**
 * 排课结果 Service 实现类
 *
 * @author yangzy
 */
@Service
@Validated
public class TimetableResultServiceImpl implements TimetableResultService {

    @Resource
    private TimetableResultMapper timetableResultMapper;

    @Override
    public Long createTimetableResult(TimetableResultSaveReqVO createReqVO) {
        // 插入
        TimetableResultDO timetableResult = BeanUtils.toBean(createReqVO, TimetableResultDO.class);
        timetableResultMapper.insert(timetableResult);
        // 返回
        return timetableResult.getId();
    }

    @Override
    public void updateTimetableResult(TimetableResultSaveReqVO updateReqVO) {
        // 校验存在
        validateTimetableResultExists(updateReqVO.getId());
        // 更新
        TimetableResultDO updateObj = BeanUtils.toBean(updateReqVO, TimetableResultDO.class);
        timetableResultMapper.updateById(updateObj);
    }

    @Override
    public void deleteTimetableResult(Long id) {
        // 校验存在
        validateTimetableResultExists(id);
        // 删除
        timetableResultMapper.deleteById(id);
    }

    private void validateTimetableResultExists(Long id) {
        if (timetableResultMapper.selectById(id) == null) {
            throw exception(TIMETABLE_RESULT_NOT_EXISTS);
        }
    }

    @Override
    public TimetableResultDO getTimetableResult(Long id) {
        return timetableResultMapper.selectById(id);
    }

    @Override
    public void createTimetableResultBatch(Long timetableId, List<Lesson> lessonList) {
        // 先删除之前所有的结果
        timetableResultMapper.deleteByTimetableId(timetableId);

        List<TimetableResultDO> timetableResultList = TimetableResultConvert.INSTANCE.convertList(timetableId, lessonList);
        timetableResultMapper.insertBatch(timetableResultList);
    }

    @Override
    public List<TimetableResultDO> getTimetableResultList(long timetableId, Long gradeId, Long teacherId, Long courseTypeId, Long subjectId, Integer week) {
        return timetableResultMapper.selectList(timetableId, gradeId, teacherId, courseTypeId, subjectId, week);
    }

}