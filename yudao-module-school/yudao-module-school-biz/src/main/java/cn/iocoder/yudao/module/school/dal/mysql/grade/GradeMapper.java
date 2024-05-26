package cn.iocoder.yudao.module.school.dal.mysql.grade;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeListReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 班级 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface GradeMapper extends BaseMapperX<GradeDO> {

    default List<GradeDO> selectList(GradeListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<GradeDO>()
                .likeIfPresent(GradeDO::getName, reqVO.getName())
                .eqIfPresent(GradeDO::getParentId, reqVO.getParentId())
                .orderByAsc(GradeDO::getSort));
    }

    default GradeDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(GradeDO::getParentId, parentId, GradeDO::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(GradeDO::getParentId, parentId);
    }

    default List<GradeDO> selectListByParentId(Collection<Long> parentIds) {
        return selectList(GradeDO::getParentId, parentIds);
    }

    default GradeDO selectByName(String name) {
        return selectOne(GradeDO::getName, name);
    }
}