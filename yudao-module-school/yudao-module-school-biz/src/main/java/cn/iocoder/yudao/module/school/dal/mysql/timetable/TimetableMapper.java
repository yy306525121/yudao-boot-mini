package cn.iocoder.yudao.module.school.dal.mysql.timetable;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.TimetablePageReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableDO;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;

/**
 * 排课表
 */
@Mapper
public interface TimetableMapper extends BaseMapperX<TimetableDO> {
    default PageResult<TimetableDO> selectPage(TimetablePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TimetableDO>()
                .likeIfPresent(TimetableDO::getName, reqVO.getName())
                .orderByDesc(TimetableDO::getCreateTime));
    }

    default TimetableDO selectByName(@NotNull String name) {
        return selectOne(TimetableDO::getName, name);
    }
}
