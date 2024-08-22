package cn.iocoder.yudao.module.school.convert.timetable;

import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.result.TimetableResultRespVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableResultDO;
import cn.iocoder.yudao.module.school.timetable.domain.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

@Mapper
public interface TimetableResultConvert {
    TimetableResultConvert INSTANCE = Mappers.getMapper(TimetableResultConvert.class);

    TimetableResultRespVO convert(TimetableResultDO timetableResult);

    default TimetableResultRespVO convert(TimetableResultDO timetableResult,
                                     GradeDO grade,
                                     TeacherDO teacher,
                                     SubjectDO subject,
                                     TimeSlotDO timeSlot,
                                     CourseTypeDO courseType) {
        TimetableResultRespVO respVO = convert(timetableResult);
        respVO.setGrade(grade);
        respVO.setTeacher(teacher);
        respVO.setSubject(subject);
        respVO.setTimeSlot(timeSlot);
        respVO.setCourseType(courseType);
        return respVO;
    }


    default List<TimetableResultDO> convertList(Long timetableId, List<Lesson> lessonList) {

        List<TimetableResultDO> timetableResultList = new ArrayList<>();
        for (Lesson lesson : lessonList) {
            TimetableResultDO timetableResult = new TimetableResultDO();
            timetableResult.setTimetableId(timetableId);
            timetableResult.setWeek(lesson.getDayOfWeek().getValue());
            timetableResult.setGradeId(lesson.getGrade().getId());
            timetableResult.setCourseTypeId(lesson.getCourseType().getId());
            timetableResult.setSubjectId(lesson.getSubject().getId());
            timetableResult.setTeacherId(lesson.getTeacher().getId());
            timetableResult.setTimeSlotId(lesson.getTimeSlot().getId());

            timetableResultList.add(timetableResult);
        }

        return timetableResultList;
    }

    default List<TimetableResultRespVO> convertList(List<TimetableResultDO> resultList,
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

        return resultList.stream().map(timetableResult -> {
            GradeDO grade = gradeMap.get(timetableResult.getGradeId());
            TeacherDO teacher = teacherMap.get(timetableResult.getTeacherId());
            SubjectDO subject = subjectMap.get(timetableResult.getSubjectId());
            TimeSlotDO timeSlot = timeSlotMap.get(timetableResult.getTimeSlotId());
            CourseTypeDO courseType = courseTypeMap.get(timetableResult.getCourseTypeId());

            return convert(timetableResult, grade, teacher, subject, timeSlot, courseType);
        }).toList();
    }
}
