package cn.iocoder.yudao.module.school.convert.teacher;

import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherRespVO;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherSimpleRespVO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TeacherConvert {
    TeacherConvert INSTANCE = Mappers.getMapper(TeacherConvert.class);

    TeacherRespVO convert(TeacherDO teacher);

    TeacherSimpleRespVO convert2(TeacherDO teacher);

    List<TeacherRespVO> convertList(List<TeacherDO> teachers);
}
