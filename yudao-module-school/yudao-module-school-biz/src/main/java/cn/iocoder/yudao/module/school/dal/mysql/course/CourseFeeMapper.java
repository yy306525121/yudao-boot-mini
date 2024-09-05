package cn.iocoder.yudao.module.school.dal.mysql.course;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseFeeListReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 课时费明细 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface CourseFeeMapper extends BaseMapperX<CourseFeeDO> {

    default List<CourseFeeDO> selectList(CourseFeeListReqVO listReqVO) {
        return selectList(new LambdaQueryWrapperX<CourseFeeDO>()
                .eqIfPresent(CourseFeeDO::getTeacherId, listReqVO.getTeacherId())
                .inIfPresent(CourseFeeDO::getTimeSlotId, listReqVO.getTimeSlotIdList())
                .inIfPresent(CourseFeeDO::getGradeId, listReqVO.getGradeIdList())
                .between(CourseFeeDO::getDate, listReqVO.getStartDate(), listReqVO.getEndDate())
        );
    }
}