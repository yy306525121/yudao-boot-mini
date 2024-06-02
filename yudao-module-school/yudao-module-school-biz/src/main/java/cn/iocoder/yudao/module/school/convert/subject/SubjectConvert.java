package cn.iocoder.yudao.module.school.convert.subject;

import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectRespVO;
import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectSimpleRespVO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SubjectConvert {
    SubjectConvert INSTANCE = Mappers.getMapper(SubjectConvert.class);

    SubjectRespVO convert(SubjectDO subject);

    SubjectSimpleRespVO convert2(SubjectDO subject);

    List<SubjectRespVO> convertList(List<SubjectDO> subjectList);
}
