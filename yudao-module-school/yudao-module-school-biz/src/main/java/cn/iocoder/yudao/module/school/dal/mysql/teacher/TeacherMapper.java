package cn.iocoder.yudao.module.school.dal.mysql.teacher;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherListReqVO;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherPageReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TeacherMapper extends BaseMapperX<TeacherDO>{
    default PageResult<TeacherDO> selectPage(TeacherPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TeacherDO>()
                .likeIfPresent(TeacherDO::getName, reqVO.getName())
                .eqIfPresent(TeacherDO::getId, reqVO.getId())
                .orderByAsc(TeacherDO::getSort)
        );
    }

    default TeacherDO selectByName(String name) {
        return selectOne(TeacherDO::getName, name);
    }

    default List<TeacherDO> selectList(TeacherListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<TeacherDO>()
                .eqIfPresent(TeacherDO::getStatus, reqVO.getStatus()));
    }
}
