package cn.iocoder.yudao.module.school.service.course;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseFeeListReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CoursePlanDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.TeacherSubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.dal.mysql.course.CourseFeeMapper;
import cn.iocoder.yudao.module.school.dal.mysql.course.CoursePlanMapper;
import cn.iocoder.yudao.module.school.dal.mysql.course.CourseTypeMapper;
import cn.iocoder.yudao.module.school.dal.mysql.course.TimeSlotMapper;
import cn.iocoder.yudao.module.school.dal.mysql.grade.GradeMapper;
import cn.iocoder.yudao.module.school.dal.mysql.subject.TeacherSubjectMapper;
import cn.iocoder.yudao.module.school.dal.mysql.teacher.TeacherMapper;
import cn.iocoder.yudao.module.school.enums.course.CourseTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

/**
 * 课时费明细 Service 实现类
 *
 * @author yangzy
 */
@Service
@Validated
@RequiredArgsConstructor
public class CourseFeeServiceImpl implements CourseFeeService {

    private final CourseFeeMapper courseFeeMapper;
    private final CoursePlanMapper coursePlanMapper;
    private final CourseTypeMapper courseTypeMapper;
    private final TeacherMapper teacherMapper;
    private final TeacherSubjectMapper teacherSubjectMapper;
    private final TimeSlotMapper timeSlotMapper;
    private final GradeMapper gradeMapper;

    @Override
    public CourseFeeDO getCourseFee(Long id) {
        return courseFeeMapper.selectById(id);
    }

    @Override
    public List<CourseFeeDO> getCourseFeeList(CourseFeeListReqVO listReqVO) {
        return courseFeeMapper.selectList(listReqVO);
    }

    @Override
    public void removeCourseFee(Long teacherId, LocalDate start, LocalDate end) {
        courseFeeMapper.delete(new LambdaQueryWrapperX<CourseFeeDO>()
                .eqIfPresent(CourseFeeDO::getTeacherId, teacherId)
                .between(CourseFeeDO::getDate, start, end)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCourseFeeBatch(Collection<CourseFeeDO> courseFeeList) {
        courseFeeMapper.insertBatch(courseFeeList);
    }

    @Override
    public List<CourseFeeDO> calculateCourseFee(LocalDate date, @Nullable Collection<Long> gradeIdList, @Nullable Long startTimeSlotId, @Nullable Long endTimeSlotId, Long teacherId) {
        LocalDate startDate = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = date.with(TemporalAdjusters.lastDayOfMonth());

        List<CourseFeeDO> courseFeeList = new ArrayList<>();

        int week = date.getDayOfWeek().getValue();
        List<CoursePlanDO> coursePlanList = coursePlanMapper.selectList(null, teacherId, null, null, date, week);

        if (startTimeSlotId != null) {
            TimeSlotDO startTimeSlot = timeSlotMapper.selectById(startTimeSlotId);
            coursePlanList = coursePlanList.stream().filter(item -> {
                TimeSlotDO timeSlot = timeSlotMapper.selectById(item.getTimeSlotId());
                if (timeSlot.getSort() >= startTimeSlot.getSort()) {
                    return true;
                }
                return false;
            }).toList();
        }

        if (endTimeSlotId != null) {
            TimeSlotDO endTimeSlot = timeSlotMapper.selectById(endTimeSlotId);
            coursePlanList = coursePlanList.stream().filter(item -> {
                TimeSlotDO timeSlot = timeSlotMapper.selectById(item.getTimeSlotId());
                if (timeSlot.getSort() <= endTimeSlot.getSort()) {
                    return true;
                }
                return false;
            }).toList();
        }

        if (CollUtil.isNotEmpty(gradeIdList)) {
            List<GradeDO> gradeList = gradeMapper.selectListByParentId(gradeIdList);
            Set<Long> secondLevelList = convertSet(gradeList, GradeDO::getId);
            coursePlanList = coursePlanList.stream().filter(item -> secondLevelList.contains(item.getGradeId())).toList();
        }


        List<CourseTypeDO> courseTypeList = courseTypeMapper.selectList();
        // 获取早自习courseType
        CourseTypeDO morningCourseType = courseTypeList.stream().filter(item -> item.getType().equals(CourseTypeEnum.MORNING.getType())).findFirst().orElseThrow();

        // 1. 开始计算非早自习的课时费
        for (CoursePlanDO coursePlan : coursePlanList) {
            if (coursePlan.getCourseTypeId().equals(morningCourseType.getId())) {
                continue;
            }
            CourseTypeDO courseType = courseTypeList.stream().filter(item -> item.getId().equals(coursePlan.getCourseTypeId())).findFirst().orElseThrow();

            CourseFeeDO courseFee = BeanUtils.toBean(coursePlan, CourseFeeDO.class);
            courseFee.setId(null);
            courseFee.setCount(courseType.getNum());
            courseFee.setDate(date);
            courseFeeList.add(courseFee);
        }


        // 2. 开始计算早自习课时费
        // 2.1 获取当天所有的早自习课程计划
        List<CoursePlanDO> morningCoursePlanList = coursePlanList.stream().filter(item -> item.getCourseTypeId().equals(morningCourseType.getId())).toList();
        // 2.2 获取当天上了哪些科目的早自习
        morningCoursePlanList = CollUtil.distinct(morningCoursePlanList, CoursePlanDO::getSubjectId, true);
        for (CoursePlanDO coursePlan : morningCoursePlanList) {
            // 2.3根据早自习的科目获取该科目下所有的教师， 每个教师获取一定量的课时
            Long subjectId = coursePlan.getSubjectId();
            List<TeacherSubjectDO> teacherSubjectList = teacherSubjectMapper.selectListBySubjectIds(Collections.singletonList(subjectId));
            Set<Long> teacherIdList = convertSet(teacherSubjectList, TeacherSubjectDO::getTeacherId);
            List<TeacherDO> teacherList = teacherMapper.selectBatchIds(teacherIdList);
            for (TeacherDO teacher : teacherList) {
                // 查看该教师是否真的上课了
                if (coursePlanMapper.selectList(null, teacher.getId(), null, null, startDate, null).size() == 0) {
                    continue;
                }

                CourseFeeDO courseFee = new CourseFeeDO();
                courseFee.setCount(morningCourseType.getNum());
                courseFee.setTeacherId(teacher.getId());
                courseFee.setSubjectId(subjectId);
                courseFee.setWeek(coursePlan.getWeek());
                courseFee.setTimeSlotId(coursePlan.getTimeSlotId());
                courseFee.setDate(date);
                courseFeeList.add(courseFee);
            }
        }

        return courseFeeList;
    }

}