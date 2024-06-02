package cn.iocoder.yudao.module.school.convert.course;

import cn.iocoder.yudao.module.school.controller.admin.course.vo.TimeSlotRespVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TimeSlotConvert {
    TimeSlotConvert INSTANCE = Mappers.getMapper(TimeSlotConvert.class);

    TimeSlotRespVO convert(TimeSlotDO timeSlot);
}
