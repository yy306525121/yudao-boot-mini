package cn.iocoder.yudao.module.school.dal.mysql.timetable;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting.TimetableSettingPageReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableSettingDO;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 排课计划设置 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface TimetableSettingMapper extends BaseMapperX<TimetableSettingDO> {

    default PageResult<TimetableSettingDO> selectPage(TimetableSettingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TimetableSettingDO>()
                .eqIfPresent(TimetableSettingDO::getTimetableId, reqVO.getTimetableId())
                .eqIfPresent(TimetableSettingDO::getGradeId, reqVO.getGradeId())
                .eqIfPresent(TimetableSettingDO::getSubjectId, reqVO.getSubjectId())
                .eqIfPresent(TimetableSettingDO::getTeacherId, reqVO.getTeacherId())
                .orderByDesc(TimetableSettingDO::getCreateTime));
    }

    default TimetableSettingDO selectOneByParams(@NotNull Long timetableId, @NotNull Long gradeId, @NotNull Long subjectId, @NotNull Long courseTypeId) {
        return selectOne(TimetableSettingDO::getTimetableId, timetableId,
                TimetableSettingDO::getGradeId, gradeId,
                TimetableSettingDO::getSubjectId, subjectId,
                TimetableSettingDO::getSubjectId, subjectId);
    }

    default List<TimetableSettingDO> selectListByTimetableId(Long timetableId) {
        return selectList(TimetableSettingDO::getTimetableId, timetableId);
    }
}
