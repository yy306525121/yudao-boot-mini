package cn.iocoder.yudao.module.school.service.course;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CoursePlanChangeReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CoursePlanSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CoursePlanDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.TeacherSubjectDO;
import cn.iocoder.yudao.module.school.dal.mysql.course.CoursePlanMapper;
import cn.iocoder.yudao.module.school.dal.mysql.course.CourseTypeMapper;
import cn.iocoder.yudao.module.school.dal.mysql.subject.SubjectMapper;
import cn.iocoder.yudao.module.school.dal.mysql.subject.TeacherSubjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.COURSE_PLAN_CHANGE_TEACHER_SUBJECT_ERROR;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.COURSE_PLAN_NOT_EXISTS;

/**
 * 课程计划 Service 实现类
 *
 * @author yangzy
 */
@Service
@Validated
@RequiredArgsConstructor
public class CoursePlanServiceImpl implements CoursePlanService {

    private final CoursePlanMapper coursePlanMapper;
    private final TeacherSubjectMapper teacherSubjectMapper;
    private final SubjectMapper subjectMapper;
    private final CourseTypeMapper courseTypeMapper;


    @Override
    public Long createCoursePlan(CoursePlanSaveReqVO createReqVO) {
        // 插入
        CoursePlanDO coursePlan = BeanUtils.toBean(createReqVO, CoursePlanDO.class);
        coursePlanMapper.insert(coursePlan);
        // 返回
        return coursePlan.getId();
    }

    @Override
    public void updateCoursePlan(CoursePlanSaveReqVO updateReqVO) {
        // 校验存在
        validateCoursePlanExists(updateReqVO.getId());
        // 更新
        CoursePlanDO updateObj = BeanUtils.toBean(updateReqVO, CoursePlanDO.class);
        coursePlanMapper.updateById(updateObj);
    }

    @Override
    public void deleteCoursePlan(Long id) {
        // 校验存在
        validateCoursePlanExists(id);
        // 删除
        coursePlanMapper.deleteById(id);
    }

    private void validateCoursePlanExists(Long id) {
        if (coursePlanMapper.selectById(id) == null) {
            throw exception(COURSE_PLAN_NOT_EXISTS);
        }
    }

    @Override
    public CoursePlanDO getCoursePlan(Long id) {
        return coursePlanMapper.selectById(id);
    }

    @Override
    public void createCoursePlan(List<CoursePlanDO> coursePlanList) {
        coursePlanMapper.insertBatch(coursePlanList);
    }

    @Override
    public List<CoursePlanDO> getCoursePlanList(Long gradeId, Long teacherId, Long courseTypeId, Long subjectId, LocalDate date) {
        return coursePlanMapper.selectList(gradeId, teacherId, courseTypeId, subjectId, date);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeCoursePlan(CoursePlanChangeReqVO reqVO) {
        LocalDate date = reqVO.getDate();

        // 校验教师是否可以任课该门课程
        TeacherSubjectDO teacherSubject = teacherSubjectMapper.selectOne(reqVO.getToTeacherId(), reqVO.getToSubjectId());
        if (teacherSubject == null) {
            throw exception(COURSE_PLAN_CHANGE_TEACHER_SUBJECT_ERROR);
        }

        CoursePlanDO oldCoursePlan = coursePlanMapper.selectById(reqVO.getId());
        oldCoursePlan.setEnd(date.plusDays(-1));
        coursePlanMapper.updateById(oldCoursePlan);

        CoursePlanDO coursePlan = BeanUtils.toBean(oldCoursePlan, CoursePlanDO.class);
        coursePlan.setId(null);
        coursePlan.setTeacherId(reqVO.getToTeacherId());
        coursePlan.setSubjectId(reqVO.getToSubjectId());
        coursePlan.setCourseTypeId(reqVO.getToCourseTypeId());
        coursePlan.setStart(reqVO.getDate());
        coursePlan.setEnd(LocalDate.of(2999, 1, 1));
        coursePlanMapper.insert(coursePlan);
    }
}