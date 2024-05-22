package cn.iocoder.yudao.module.school.dal.mysql.subject;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.TeacherSubjectDO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeacherSubjectMapper extends BaseMapperX<TeacherSubjectDO> {
    default void deleteByTeacherId(Long teacherId) {
        delete(Wrappers.lambdaUpdate(TeacherSubjectDO.class).eq(TeacherSubjectDO::getTeacherId, teacherId));
    };
}
