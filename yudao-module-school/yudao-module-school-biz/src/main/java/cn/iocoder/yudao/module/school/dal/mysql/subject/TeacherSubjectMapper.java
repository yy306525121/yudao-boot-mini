package cn.iocoder.yudao.module.school.dal.mysql.subject;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.TeacherSubjectDO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface TeacherSubjectMapper extends BaseMapperX<TeacherSubjectDO> {
    default void deleteByTeacherId(Long teacherId) {
        delete(Wrappers.lambdaUpdate(TeacherSubjectDO.class).eq(TeacherSubjectDO::getTeacherId, teacherId));
    };

    default List<TeacherSubjectDO> selectListByTeacherId(Long teacherId) {
        return selectList(new LambdaQueryWrapperX<TeacherSubjectDO>()
                .eq(TeacherSubjectDO::getTeacherId, teacherId));
    }

    default TeacherSubjectDO selectOne(Long teacherId, Long subjectId) {
        return selectOne(new LambdaQueryWrapperX<TeacherSubjectDO>()
                .eq(TeacherSubjectDO::getTeacherId, teacherId)
                .eq(TeacherSubjectDO::getSubjectId, subjectId));
    }

    default List<TeacherSubjectDO> selectListBySubjectIds(Collection<Long> subjectIds) {
        return selectList(TeacherSubjectDO::getSubjectId, subjectIds);
    }

    default void deleteByTeacherIdAndSubjectIds(Long teacherId, Collection<Long> deleteSubjectIds) {
        delete(new LambdaQueryWrapperX<TeacherSubjectDO>()
                .eq(TeacherSubjectDO::getTeacherId, teacherId)
                .in(TeacherSubjectDO::getSubjectId, deleteSubjectIds));
    }
}
