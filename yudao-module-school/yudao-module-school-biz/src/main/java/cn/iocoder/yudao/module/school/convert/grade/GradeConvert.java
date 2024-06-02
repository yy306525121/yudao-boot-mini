package cn.iocoder.yudao.module.school.convert.grade;

import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeRespVO;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeSimpleRespVO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GradeConvert {
    GradeConvert INSTANCE = Mappers.getMapper(GradeConvert.class);

    GradeRespVO convert(GradeDO grade);

    GradeSimpleRespVO convert2(GradeDO grade);
}
