package cn.iocoder.yudao.module.school.dal.mysql.course;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CoursePlanDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 课程计划 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface CoursePlanMapper extends BaseMapperX<CoursePlanDO> {

    default List<CoursePlanDO> selectList(Long gradeId, Long teacherId, Long courseTypeId, Long subjectId, LocalDate date, Integer week) {
        return selectList(new LambdaQueryWrapperX<CoursePlanDO>()
                .eqIfPresent(CoursePlanDO::getGradeId, gradeId)
                .eqIfPresent(CoursePlanDO::getTeacherId, teacherId)
                .eqIfPresent(CoursePlanDO::getCourseTypeId, courseTypeId)
                .eqIfPresent(CoursePlanDO::getSubjectId, subjectId)
                .eqIfPresent(CoursePlanDO::getWeek, week)
                .le(CoursePlanDO::getStart, date)
                .ge(CoursePlanDO::getEnd, date));
    };
}