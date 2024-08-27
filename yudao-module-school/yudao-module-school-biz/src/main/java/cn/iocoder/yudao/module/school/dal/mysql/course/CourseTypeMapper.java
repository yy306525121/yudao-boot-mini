package cn.iocoder.yudao.module.school.dal.mysql.course;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseTypePageReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 课程类型 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface CourseTypeMapper extends BaseMapperX<CourseTypeDO> {

    default PageResult<CourseTypeDO> selectPage(CourseTypePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CourseTypeDO>()
                .likeIfPresent(CourseTypeDO::getName, reqVO.getName())
                .orderByAsc(CourseTypeDO::getSort));
    }

    default CourseTypeDO selectByName(String name) {
        return selectOne(CourseTypeDO::getName, name);
    };

    default List<CourseTypeDO> selectAll() {
        return selectList(new LambdaQueryWrapperX<CourseTypeDO>()
                .orderByAsc(CourseTypeDO::getSort));
    }

    default CourseTypeDO selectByType(Integer type) {
        return selectOne(CourseTypeDO::getType, type);
    }
}