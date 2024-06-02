package cn.iocoder.yudao.module.school.controller.admin.course;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.*;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherPageReqVO;
import cn.iocoder.yudao.module.school.convert.course.CourseFeeConvert;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CoursePlanDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.enums.course.CourseTypeEnum;
import cn.iocoder.yudao.module.school.service.course.CourseFeeService;
import cn.iocoder.yudao.module.school.service.course.CoursePlanService;
import cn.iocoder.yudao.module.school.service.course.CourseTypeService;
import cn.iocoder.yudao.module.school.service.course.TimeSlotService;
import cn.iocoder.yudao.module.school.service.grade.GradeService;
import cn.iocoder.yudao.module.school.service.subject.SubjectService;
import cn.iocoder.yudao.module.school.service.teacher.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 课时费明细")
@RestController
@RequestMapping("/school/course-fee")
@Validated
@RequiredArgsConstructor
public class CourseFeeController {

    private final CourseFeeService courseFeeService;
    private final TeacherService teacherService;
    private final CoursePlanService coursePlanService;
    private final CourseTypeService courseTypeService;
    private final GradeService gradeService;
    private final SubjectService subjectService;
    private final TimeSlotService timeSlotService;

    @GetMapping("/page")
    @Operation(summary = "获得课时费明细分页")
    @PreAuthorize("@ss.hasPermission('school:course-fee:query')")
    public CommonResult<PageResult<CourseFeeRespVO>> getCourseFeePage(@Valid CourseFeePageReqVO pageReqVO) {
        LocalDate date = pageReqVO.getDate();
        LocalDate startDate = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = date.with(TemporalAdjusters.lastDayOfMonth());

        CourseFeeListReqVO listReqVO = new CourseFeeListReqVO();
        listReqVO.setStartDate(startDate);
        listReqVO.setEndDate(endDate);
        listReqVO.setTeacherId(pageReqVO.getTeacherId());
        List<CourseFeeDO> courseFeeList = courseFeeService.getCourseFeeList(listReqVO);
        if (CollUtil.isEmpty(courseFeeList)) {
            return success(PageResult.empty());
        }

        List<Long> teacherIds = courseFeeList.stream().map(CourseFeeDO::getTeacherId).distinct().toList();
        TeacherPageReqVO teacherPageReqVO = new TeacherPageReqVO();
        teacherPageReqVO.setPageNo(pageReqVO.getPageNo());
        teacherPageReqVO.setPageSize(pageReqVO.getPageSize());
        teacherPageReqVO.setIds(teacherIds);
        PageResult<TeacherDO> pageResult = teacherService.getTeacherPage(teacherPageReqVO);


        return success(CourseFeeConvert.INSTANCE.convertPage(pageResult, courseFeeList));
    }

    @PostMapping("/calculate")
    @Operation(summary = "课时费计算")
    @PreAuthorize("@ss.hasPermission('school:course-fee:calculate')")
    public CommonResult<Boolean> calculateCourseFee(@Valid @RequestBody CourseFeeCalculateReqVO reqVO) {
        LocalDate date = reqVO.getDate();
        LocalDate start = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());

        // 删除原有数据
        courseFeeService.removeCourseFee(reqVO.getTeacherId(), start, end);

        List<CourseFeeDO> courseFeeList = calculate(reqVO, start, end);

        courseFeeService.createCourseFeeBatch(courseFeeList);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "课时费明细")
    @PreAuthorize("@ss.hasPermission('school:course-fee:detail')")
    public CommonResult<List<CourseFeeCountRespVO>> detail(@Valid CourseFeeCountReqVO reqVO) {
        LocalDate date = reqVO.getDate();
        LocalDate start = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());

        CourseFeeListReqVO listReqVO = new CourseFeeListReqVO();
        listReqVO.setStartDate(start);
        listReqVO.setEndDate(end);
        listReqVO.setTeacherId(reqVO.getTeacherId());
        List<CourseFeeDO> list = courseFeeService.getCourseFeeList(listReqVO);

        // 获取所有相关的教师
        List<Long> teacherIds = list.stream().map(CourseFeeDO::getTeacherId).distinct().toList();
        List<TeacherDO> teacherList = teacherService.getTeacherListByIds(teacherIds);

        // 获取所有相关的班级
        List<Long> gradeIds = list.stream().map(CourseFeeDO::getGradeId).distinct().toList();
        List<GradeDO> gradeList = gradeService.getGradeListByIds(gradeIds);

        // 获取所有相关的课程
        List<Long> subjectIds = list.stream().map(CourseFeeDO::getSubjectId).distinct().toList();
        List<SubjectDO> subjectList = subjectService.getSubjectList(subjectIds);

        // 获取所有相关的节次信息
        List<Long> timeSlotIds = list.stream().map(CourseFeeDO::getTimeSlotId).distinct().toList();
        List<TimeSlotDO> timeSlotList = timeSlotService.getTimeSlotListByIds(timeSlotIds);

        return success(CourseFeeConvert.INSTANCE.convertListGroupByDay(list, teacherList, gradeList, subjectList, timeSlotList));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出课时费明细 Excel")
    @PreAuthorize("@ss.hasPermission('school:course-fee:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCourseFeeExcel(@Valid CourseFeePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
    }


    private List<CourseFeeDO> calculate(CourseFeeCalculateReqVO reqVO, LocalDate start, LocalDate end) {
        List<CourseTypeDO> courseTypeList = courseTypeService.getAll();
        // 获取早自习courseType
        CourseTypeDO morningCourseType = courseTypeList.stream().filter(item -> item.getType().equals(CourseTypeEnum.MORNING.getType())).findFirst().orElseThrow();

        List<CourseFeeDO> courseFeeList = new ArrayList<>();
        LocalDate currentDate = start;
        while (!currentDate.isAfter(end)) {
            int week = currentDate.getDayOfWeek().getValue();
            List<CoursePlanDO> coursePlanList = coursePlanService.getCoursePlanList(null, reqVO.getTeacherId(), null, null, currentDate, week);

            // 1. 开始计算非早自习的课时费
            for (CoursePlanDO coursePlan : coursePlanList) {
                if (coursePlan.getCourseTypeId().equals(morningCourseType.getId())) {
                    continue;
                }
                CourseTypeDO courseType = courseTypeList.stream().filter(item -> item.getId().equals(coursePlan.getCourseTypeId())).findFirst().orElseThrow();

                CourseFeeDO courseFee = BeanUtils.toBean(coursePlan, CourseFeeDO.class);
                courseFee.setId(null);
                courseFee.setCount(courseType.getNum());
                courseFee.setDate(currentDate);
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
                List<TeacherDO> teacherList = teacherService.getTeacherListBySubjectIds(Collections.singletonList(subjectId));
                for (TeacherDO teacher : teacherList) {
                    CourseFeeDO courseFee = new CourseFeeDO();
                    courseFee.setCount(morningCourseType.getNum());
                    courseFee.setTeacherId(teacher.getId());
                    courseFee.setSubjectId(subjectId);
                    courseFee.setWeek(coursePlan.getWeek());
                    courseFee.setTimeSlotId(coursePlan.getTimeSlotId());
                    courseFee.setDate(currentDate);
                    courseFeeList.add(courseFee);
                }
            }

            currentDate = currentDate.plusDays(1);
        }

        return courseFeeList;
    }

}