package cn.iocoder.yudao.module.school.convert.course;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseFeeCountRespVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseFeeRespVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public interface CourseFeeConvert {
    CourseFeeConvert INSTANCE = Mappers.getMapper(CourseFeeConvert.class);

    CourseFeeRespVO convert(CourseFeeDO bean);

    default PageResult<CourseFeeRespVO> convertPage(PageResult<TeacherDO> page,
                                              List<CourseFeeDO> courseFeeList) {
        PageResult<CourseFeeRespVO> pageResult = new PageResult<>();
        List<TeacherDO> list = page.getList();

        List<CourseFeeRespVO> courseFeeRespVOList = new ArrayList<>(list.size());
        for (TeacherDO teacher : list) {
            CourseFeeRespVO courseFeeRespVO = new CourseFeeRespVO();
            courseFeeRespVO.setTeacher(TeacherConvert.INSTANCE.convert(teacher));

            BigDecimal count = courseFeeList.stream().filter(item -> item.getTeacherId().equals(teacher.getId())).map(CourseFeeDO::getCount).reduce(BigDecimal.ZERO, BigDecimal::add);
            courseFeeRespVO.setCount(count);
            courseFeeRespVOList.add(courseFeeRespVO);
        }

        pageResult.setList(courseFeeRespVOList);
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    default List<CourseFeeCountRespVO> convertListGroupByDay(List<CourseFeeDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<CourseFeeCountRespVO> result = new ArrayList<>(list.size());

        Map<LocalDate, BigDecimal> collect = list.stream().collect(Collectors.groupingBy(CourseFeeDO::getDate, Collectors.reducing(BigDecimal.ZERO, CourseFeeDO::getCount, BigDecimal::add)));

        collect.forEach((date, count) -> {
            CourseFeeCountRespVO respVO = new CourseFeeCountRespVO();
            respVO.setDate(date);
            respVO.setCount(count);
            result.add(respVO);
        });

        return result;
    };
}
