package cn.iocoder.yudao.module.school.dal.mysql.timetable;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.custom.TimetableCustomPageReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableCustomDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 排课定制 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface TimetableCustomMapper extends BaseMapperX<TimetableCustomDO> {

    default PageResult<TimetableCustomDO> selectPage(TimetableCustomPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TimetableCustomDO>()
                .eqIfPresent(TimetableCustomDO::getTimetableId, reqVO.getTimetableId())
                .eqIfPresent(TimetableCustomDO::getFixed, reqVO.getFixed())
                .eqIfPresent(TimetableCustomDO::getWeight, reqVO.getWeight())
                .eqIfPresent(TimetableCustomDO::getWeek, reqVO.getWeek())
                .eqIfPresent(TimetableCustomDO::getGradeId, reqVO.getGradeId())
                .eqIfPresent(TimetableCustomDO::getCourseTypeId, reqVO.getCourseTypeId())
                .eqIfPresent(TimetableCustomDO::getSubjectId, reqVO.getSubjectId())
                .eqIfPresent(TimetableCustomDO::getTeacherId, reqVO.getTeacherId())
                .eqIfPresent(TimetableCustomDO::getTimeSlotId, reqVO.getTimeSlotId())
                .betweenIfPresent(TimetableCustomDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TimetableCustomDO::getId));
    }

}