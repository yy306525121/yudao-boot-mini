package cn.iocoder.yudao.module.school.dal.mysql.course;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.TimeSlotListReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.TimeSlotPageReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 课程节次 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface TimeSlotMapper extends BaseMapperX<TimeSlotDO> {

    default PageResult<TimeSlotDO> selectPage(TimeSlotPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TimeSlotDO>()
                .eqIfPresent(TimeSlotDO::getSort, reqVO.getSort())
                .orderByDesc(TimeSlotDO::getId));
    }

    default TimeSlotDO selectBySort(Integer sort) {
        return selectOne(TimeSlotDO::getSort, sort);
    }

    default List<TimeSlotDO> selectList(TimeSlotListReqVO listReqVO) {
        return selectList(new LambdaQueryWrapperX<TimeSlotDO>()
                .eqIfPresent(TimeSlotDO::getSort, listReqVO.getSort())
                .orderByAsc(TimeSlotDO::getSort));
    }

    default TimeSlotDO selectOneBySort(Integer sort) {
        return selectOne(TimeSlotDO::getSort, sort);
    }

    default List<TimeSlotDO> selectListByType(Integer type) {
        return selectList(TimeSlotDO::getType, type);
    }

    default TimeSlotDO selectLast() {
        return selectOne(new LambdaQueryWrapperX<TimeSlotDO>()
                .orderByDesc(TimeSlotDO::getSort)
                .last("limit 1"));
    }

    default TimeSlotDO selectFirst() {
        return selectOne(new LambdaQueryWrapperX<TimeSlotDO>()
                .orderByAsc(TimeSlotDO::getSort)
                .last("limit 1"));
    }
}