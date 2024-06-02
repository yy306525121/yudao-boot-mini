package cn.iocoder.yudao.module.school.convert.course;

import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectSimpleRespVO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SubjectConvert {
    SubjectConvert INSTANCE = Mappers.getMapper(SubjectConvert.class);

    SubjectSimpleRespVO convert(SubjectDO subject);
}
