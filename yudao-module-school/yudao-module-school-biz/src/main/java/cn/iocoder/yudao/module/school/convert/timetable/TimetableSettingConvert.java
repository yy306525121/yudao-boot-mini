package cn.iocoder.yudao.module.school.convert.timetable;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting.TimetableSettingPageRespVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableSettingDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

@Mapper
public interface TimetableSettingConvert {
    TimetableSettingConvert INSTANCE = Mappers.getMapper(TimetableSettingConvert.class);

    TimetableSettingPageRespVO convert(TimetableSettingDO timetableSetting);

    default TimetableSettingPageRespVO convert(TimetableSettingDO timetableSetting,
                                               GradeDO grade,
                                               SubjectDO subject,
                                               TeacherDO teacher,
                                               CourseTypeDO courseType) {
        TimetableSettingPageRespVO respVO = convert(timetableSetting);
        respVO.setGrade(grade);
        respVO.setSubject(subject);
        respVO.setTeacher(teacher);
        respVO.setCourseType(courseType);

        return respVO;
    }

    default PageResult<TimetableSettingPageRespVO> convertPage(PageResult<TimetableSettingDO> page,
                                                               List<GradeDO> gradeList,
                                                               List<SubjectDO> subjectList,
                                                               List<TeacherDO> teacherList,
                                                               List<CourseTypeDO> courseTypeList) {
        PageResult<TimetableSettingPageRespVO> pageResult = new PageResult<>();
        List<TimetableSettingPageRespVO> pageList = new ArrayList<>();

        Map<Long, GradeDO> gradeMap = convertMap(gradeList, GradeDO::getId);
        Map<Long, SubjectDO> subjectMap = convertMap(subjectList, SubjectDO::getId);
        Map<Long, TeacherDO> teacherMap = convertMap(teacherList, TeacherDO::getId);
        Map<Long, CourseTypeDO> courseTypeMap = convertMap(courseTypeList, CourseTypeDO::getId);

        List<TimetableSettingDO> list = page.getList();
        for (TimetableSettingDO timetableSetting : list) {
            TimetableSettingPageRespVO respVO = convert(timetableSetting,
                    gradeMap.get(timetableSetting.getGradeId()),
                    subjectMap.get(timetableSetting.getSubjectId()),
                    teacherMap.get(timetableSetting.getTeacherId()),
                    courseTypeMap.get(timetableSetting.getCourseTypeId()));
            pageList.add(respVO);
        }

        pageResult.setList(pageList);
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }
}
