package cn.iocoder.yudao.module.school.convert.course;

import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseTypeRespVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CourseTypeConvert {
    CourseTypeConvert INSTANCE = Mappers.getMapper(CourseTypeConvert.class);

    CourseTypeRespVO convert(CourseTypeDO courseType);

    List<CourseTypeRespVO> convertList(List<CourseTypeDO> courseTypeList);
}
