package cn.iocoder.yudao.module.school.convert.course;

import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherRespVO;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherSimpleRespVO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TeacherConvert {
    TeacherConvert INSTANCE = Mappers.getMapper(TeacherConvert.class);

    TeacherRespVO convert(TeacherDO teacher);

    TeacherSimpleRespVO convert2(TeacherDO teacher);
}
