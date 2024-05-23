package cn.iocoder.yudao.module.school.dal.mysql.classinfo;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.school.dal.dataobject.classinfo.ClassInfoDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.school.controller.admin.classinfo.vo.*;

/**
 * 班级 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface ClassInfoMapper extends BaseMapperX<ClassInfoDO> {

    default List<ClassInfoDO> selectList(ClassInfoListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ClassInfoDO>()
                .likeIfPresent(ClassInfoDO::getName, reqVO.getName())
                .orderByDesc(ClassInfoDO::getId));
    }

    default ClassInfoDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(ClassInfoDO::getParentId, parentId, ClassInfoDO::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(ClassInfoDO::getParentId, parentId);
    }

}