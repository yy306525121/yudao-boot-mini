package cn.iocoder.yudao.module.school.convert.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.TransferRulePageRespVO;
import cn.iocoder.yudao.module.school.convert.course.CourseTypeConvert;
import cn.iocoder.yudao.module.school.convert.course.TimeSlotConvert;
import cn.iocoder.yudao.module.school.convert.grade.GradeConvert;
import cn.iocoder.yudao.module.school.convert.subject.SubjectConvert;
import cn.iocoder.yudao.module.school.convert.teacher.TeacherConvert;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.TransferRuleDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;


@Mapper
public interface TransferRuleConvert {
    TransferRuleConvert INSTANCE = Mappers.getMapper(TransferRuleConvert.class);

    default TransferRulePageRespVO convert(TransferRuleDO transferRule,
                                           GradeDO grade,
                                           TimeSlotDO timeSlot,
                                           TeacherDO fromTeacher,
                                           TeacherDO toTeacher,
                                           SubjectDO fromSubject,
                                           SubjectDO toSubject,
                                           CourseTypeDO fromCourseType,
                                           CourseTypeDO toCourseType) {
        TransferRulePageRespVO respVO = new TransferRulePageRespVO();
        respVO.setId(transferRule.getId());
        respVO.setDate(transferRule.getDate());
        respVO.setGrade(GradeConvert.INSTANCE.convert(grade));
        respVO.setTimeSlot(TimeSlotConvert.INSTANCE.convert(timeSlot));

        if (fromTeacher != null) {
            respVO.setFromTeacher(TeacherConvert.INSTANCE.convert(fromTeacher));
        }
        if (toTeacher != null) {
            respVO.setToTeacher(TeacherConvert.INSTANCE.convert(toTeacher));
        }
        if (fromSubject != null) {
            respVO.setFromSubject(SubjectConvert.INSTANCE.convert(fromSubject));
        }
        if (toSubject != null) {
            respVO.setToSubject(SubjectConvert.INSTANCE.convert(toSubject));
        }
        if (fromCourseType != null) {
            respVO.setFromCourseType(CourseTypeConvert.INSTANCE.convert(fromCourseType));
        }
        if (toCourseType != null) {
            respVO.setToCourseType(CourseTypeConvert.INSTANCE.convert(toCourseType));
        }

        return respVO;
    }

    default PageResult<TransferRulePageRespVO> convertPage(PageResult<TransferRuleDO> page,
                                                           List<GradeDO> gradeList,
                                                           List<TimeSlotDO> timeSlotList,
                                                           List<TeacherDO> teacherList,
                                                           List<SubjectDO> subjectList,
                                                           List<CourseTypeDO> courseTypeList) {

        Map<Long, GradeDO> gradeMap = convertMap(gradeList, GradeDO::getId);
        Map<Long, TimeSlotDO> timeSlotMap = convertMap(timeSlotList, TimeSlotDO::getId);
        Map<Long, TeacherDO> teacherMap = convertMap(teacherList, TeacherDO::getId);
        Map<Long, SubjectDO> subjectMap = convertMap(subjectList, SubjectDO::getId);
        Map<Long, CourseTypeDO> courseTypeMap = convertMap(courseTypeList, CourseTypeDO::getId);

        PageResult<TransferRulePageRespVO> pageResult = new PageResult<>();
        List<TransferRulePageRespVO> pageList = new ArrayList<>();

        List<TransferRuleDO> list = page.getList();
        for (TransferRuleDO transferRule : list) {
            TransferRulePageRespVO pageRespVO = convert(transferRule,
                    gradeMap.get(transferRule.getGradeId()),
                    timeSlotMap.get(transferRule.getTimeSlotId()),
                    teacherMap.get(transferRule.getFromTeacherId()),
                    teacherMap.get(transferRule.getToTeacherId()),
                    subjectMap.get(transferRule.getFromSubjectId()),
                    subjectMap.get(transferRule.getToSubjectId()),
                    courseTypeMap.get(transferRule.getFromCourseTypeId()),
                    courseTypeMap.get(transferRule.getToCourseTypeId()));
            pageList.add(pageRespVO);
        }
        pageResult.setList(pageList);
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }
}
