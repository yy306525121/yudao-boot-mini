package cn.iocoder.yudao.module.school.dal.mysql.timetable;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableResultDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 排课结果 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface TimetableResultMapper extends BaseMapperX<TimetableResultDO> {

    default List<TimetableResultDO> selectList(long timetableId, Long gradeId, Long teacherId, Long courseTypeId, Long subjectId, Integer week){
        return selectList(new LambdaQueryWrapperX<TimetableResultDO>()
                .eq(TimetableResultDO::getTimetableId, timetableId)
                .eqIfPresent(TimetableResultDO::getGradeId, gradeId)
                .eqIfPresent(TimetableResultDO::getTeacherId, teacherId)
                .eqIfPresent(TimetableResultDO::getCourseTypeId, courseTypeId)
                .eqIfPresent(TimetableResultDO::getSubjectId, subjectId)
                .eqIfPresent(TimetableResultDO::getWeek, week));
    }

    default void deleteByTimetableId(Long timetableId) {
        delete(TimetableResultDO::getTimetableId, timetableId);
    }
}