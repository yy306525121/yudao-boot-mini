package cn.iocoder.yudao.module.school.service.timetable;

import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.result.TimetableResultSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableResultDO;
import cn.iocoder.yudao.module.school.timefold.domain.Lesson;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 排课结果 Service 接口
 *
 * @author yangzy
 */
public interface TimetableResultService {

    /**
     * 创建排课结果
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTimetableResult(@Valid TimetableResultSaveReqVO createReqVO);

    /**
     * 更新排课结果
     *
     * @param updateReqVO 更新信息
     */
    void updateTimetableResult(@Valid TimetableResultSaveReqVO updateReqVO);

    /**
     * 删除排课结果
     *
     * @param id 编号
     */
    void deleteTimetableResult(Long id);

    /**
     * 获得排课结果
     *
     * @param id 编号
     * @return 排课结果
     */
    TimetableResultDO getTimetableResult(Long id);

    void createTimetableResultBatch(Long id, List<Lesson> lessonList);

    List<TimetableResultDO> getTimetableResultList(long timetableId, Long gradeId, Long teacherId, Long courseTypeId, Long subjectId, Integer week);
}
