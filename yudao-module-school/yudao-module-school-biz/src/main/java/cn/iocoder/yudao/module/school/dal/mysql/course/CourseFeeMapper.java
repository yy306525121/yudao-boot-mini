package cn.iocoder.yudao.module.school.dal.mysql.course;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课时费明细 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface CourseFeeMapper extends BaseMapperX<CourseFeeDO> {

}