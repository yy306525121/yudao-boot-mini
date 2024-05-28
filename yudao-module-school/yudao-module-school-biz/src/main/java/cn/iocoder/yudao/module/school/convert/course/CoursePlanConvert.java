package cn.iocoder.yudao.module.school.convert.course;

import cn.iocoder.yudao.module.school.controller.admin.course.vo.CoursePlanRespVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CoursePlanDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

@Mapper
public interface CoursePlanConvert {
    CoursePlanConvert INSTANCE = Mappers.getMapper(CoursePlanConvert.class);

    CoursePlanRespVO convert(CoursePlanDO coursePlan);

    default CoursePlanRespVO convert(CoursePlanDO coursePlan,
                                     GradeDO grade,
                                     TeacherDO teacher,
                                     SubjectDO subject,
                                     TimeSlotDO timeSlot,
                                     CourseTypeDO courseType) {
        CoursePlanRespVO respVO = convert(coursePlan);
        respVO.setGrade(grade);
        respVO.setTeacher(teacher);
        respVO.setSubject(subject);
        respVO.setTimeSlot(timeSlot);
        respVO.setCourseType(courseType);
        return respVO;
    }


    default List<CoursePlanRespVO> convertList(List<CoursePlanDO> coursePlanList,
                                               List<GradeDO> gradeList,
                                               List<TeacherDO> teacherList,
                                               List<SubjectDO> subjectList,
                                               List<TimeSlotDO> timeSlotList,
                                               List<CourseTypeDO> courseTypeList) {
        Map<Long, GradeDO> gradeMap = convertMap(gradeList, GradeDO::getId);
        Map<Long, TeacherDO> teacherMap = convertMap(teacherList, TeacherDO::getId);
        Map<Long, SubjectDO> subjectMap = convertMap(subjectList, SubjectDO::getId);
        Map<Long, TimeSlotDO> timeSlotMap = convertMap(timeSlotList, TimeSlotDO::getId);
        Map<Long, CourseTypeDO> courseTypeMap = convertMap(courseTypeList, CourseTypeDO::getId);

        return coursePlanList.stream().map(coursePlan -> {

            GradeDO grade = gradeMap.get(coursePlan.getGradeId());
            TeacherDO teacher = teacherMap.get(coursePlan.getTeacherId());
            SubjectDO subject = subjectMap.get(coursePlan.getSubjectId());
            TimeSlotDO timeSlot = timeSlotMap.get(coursePlan.getTimeSlotId());
            CourseTypeDO courseType = courseTypeMap.get(coursePlan.getCourseTypeId());

            return convert(coursePlan, grade, teacher, subject, timeSlot, courseType);
        }).toList();
    }
}
