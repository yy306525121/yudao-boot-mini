package cn.iocoder.yudao.module.solver.dal.mysql.timetable;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.solver.dal.dataobject.timetable.TimetableDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 排课表
 */
@Mapper
public interface TimetableMapper extends BaseMapperX<TimetableDO> {
}
