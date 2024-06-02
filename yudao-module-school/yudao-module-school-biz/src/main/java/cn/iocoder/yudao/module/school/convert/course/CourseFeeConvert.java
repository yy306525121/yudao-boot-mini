package cn.iocoder.yudao.module.school.convert.course;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseFeeCountRespVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseFeeDetailRespVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseFeeRespVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.TimeSlotRespVO;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeSimpleRespVO;
import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectSimpleRespVO;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherSimpleRespVO;
import cn.iocoder.yudao.module.school.convert.grade.GradeConvert;
import cn.iocoder.yudao.module.school.convert.subject.SubjectConvert;
import cn.iocoder.yudao.module.school.convert.teacher.TeacherConvert;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

@Mapper
public interface CourseFeeConvert {
    CourseFeeConvert INSTANCE = Mappers.getMapper(CourseFeeConvert.class);

    CourseFeeRespVO convert(CourseFeeDO bean);

    default CourseFeeDetailRespVO convert(CourseFeeDO courseFee,
                                          TeacherDO teacher,
                                          GradeDO grade,
                                          SubjectDO subject,
                                          TimeSlotDO timeSlot) {
        CourseFeeDetailRespVO respVO = BeanUtils.toBean(courseFee, CourseFeeDetailRespVO.class);

        if (teacher != null) {
            TeacherSimpleRespVO teacherRespVO = TeacherConvert.INSTANCE.convert2(teacher);
            respVO.setTeacher(teacherRespVO);
        }
        if (grade != null) {
            GradeSimpleRespVO gradeRespVO = GradeConvert.INSTANCE.convert2(grade);
            respVO.setGrade(gradeRespVO);
        }
        if (subject != null) {
            SubjectSimpleRespVO subjectRespVO = SubjectConvert.INSTANCE.convert2(subject);
            respVO.setSubject(subjectRespVO);
        }
        if (timeSlot != null) {
            TimeSlotRespVO timeSlotRespVO = TimeSlotConvert.INSTANCE.convert(timeSlot);
            respVO.setTimeSlot(timeSlotRespVO);
        }

        return respVO;
    }

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

    default List<CourseFeeCountRespVO> convertListGroupByDay(List<CourseFeeDO> list,
                                                             List<TeacherDO> teacherList,
                                                             List<GradeDO> gradeList,
                                                             List<SubjectDO> subjectList,
                                                             List<TimeSlotDO> timeSlotList) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<CourseFeeCountRespVO> result = new ArrayList<>(list.size());

        Map<LocalDate, BigDecimal> collect = list.stream().collect(Collectors.groupingBy(CourseFeeDO::getDate, Collectors.reducing(BigDecimal.ZERO, CourseFeeDO::getCount, BigDecimal::add)));

        collect.forEach((date, count) -> {
            CourseFeeCountRespVO respVO = new CourseFeeCountRespVO();
            respVO.setDate(date);
            respVO.setCount(count);

            List<CourseFeeDO> currentDateList = list.stream().filter(item -> item.getDate().equals(date)).toList();
            List<CourseFeeDetailRespVO> dataList = convertList2(currentDateList, teacherList, gradeList, subjectList, timeSlotList);
            respVO.setCourseFeeList(dataList);

            result.add(respVO);
        });

        return result;
    }

    default List<CourseFeeDetailRespVO> convertList2(List<CourseFeeDO> courseFeeList,
                                                     List<TeacherDO> teacherList,
                                                     List<GradeDO> gradeList,
                                                     List<SubjectDO> subjectList,
                                                     List<TimeSlotDO> timeSlotList) {
        List<CourseFeeDetailRespVO> respList = new ArrayList<>(courseFeeList.size());

        Map<Long, TeacherDO> teacherMap = convertMap(teacherList, TeacherDO::getId);
        Map<Long, GradeDO> gradeMap = convertMap(gradeList, GradeDO::getId);
        Map<Long, SubjectDO> subjectMap = convertMap(subjectList, SubjectDO::getId);
        Map<Long, TimeSlotDO> timeSlotMap = convertMap(timeSlotList, TimeSlotDO::getId);

        for (CourseFeeDO courseFee : courseFeeList) {
            // 教师
            TeacherDO teacher = teacherMap.get(courseFee.getTeacherId());
            // 节次
            TimeSlotDO timeSlot = timeSlotMap.get(courseFee.getTimeSlotId());

            // 年级
            GradeDO grade = null;
            if (courseFee.getGradeId() != null) {
                grade = gradeMap.get(courseFee.getGradeId());
            }

            // 科目
            SubjectDO subject = null;
            if (courseFee.getSubjectId() != null) {
                subject = subjectMap.get(courseFee.getSubjectId());
            }


            CourseFeeDetailRespVO respVO = convert(courseFee, teacher, grade, subject, timeSlot);
            respList.add(respVO);
        }

        return respList.stream()
                .sorted(Comparator.comparing(a -> a.getTimeSlot().getSort()))
                .toList();
    }
}
