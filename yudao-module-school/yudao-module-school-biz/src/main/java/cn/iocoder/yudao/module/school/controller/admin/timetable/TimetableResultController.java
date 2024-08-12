package cn.iocoder.yudao.module.school.controller.admin.timetable;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.result.TimetableResultListReqVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.result.TimetableResultRespVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.result.TimetableResultSaveReqVO;
import cn.iocoder.yudao.module.school.convert.timetable.TimetableResultConvert;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableResultDO;
import cn.iocoder.yudao.module.school.enums.course.CourseNameEnum;
import cn.iocoder.yudao.module.school.enums.course.CoursePlanQueryTypeEnum;
import cn.iocoder.yudao.module.school.enums.course.CourseTypeEnum;
import cn.iocoder.yudao.module.school.service.course.CourseTypeService;
import cn.iocoder.yudao.module.school.service.course.TimeSlotService;
import cn.iocoder.yudao.module.school.service.grade.GradeService;
import cn.iocoder.yudao.module.school.service.subject.SubjectService;
import cn.iocoder.yudao.module.school.service.teacher.TeacherService;
import cn.iocoder.yudao.module.school.service.timetable.TimetableResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.GRADE_NOT_EXISTS;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.TEACHER_NOT_EXISTS;

@Tag(name = "管理后台 - 排课结果")
@RestController
@RequestMapping("/school/timetable-result")
@Validated
@RequiredArgsConstructor
public class TimetableResultController {

    private final TimetableResultService timetableResultService;
    private final SubjectService subjectService;
    private final CourseTypeService courseTypeService;
    private final TeacherService teacherService;
    private final GradeService gradeService;
    private final TimeSlotService timeSlotService;



    @PostMapping("/create")
    @Operation(summary = "创建排课结果")
    @PreAuthorize("@ss.hasPermission('school:timetable-result:create')")
    public CommonResult<Long> createTimetableResult(@Valid @RequestBody TimetableResultSaveReqVO createReqVO) {
        return success(timetableResultService.createTimetableResult(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新排课结果")
    @PreAuthorize("@ss.hasPermission('school:timetable-result:update')")
    public CommonResult<Boolean> updateTimetableResult(@Valid @RequestBody TimetableResultSaveReqVO updateReqVO) {
        timetableResultService.updateTimetableResult(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除排课结果")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('school:timetable-result:delete')")
    public CommonResult<Boolean> deleteTimetableResult(@RequestParam("id") Long id) {
        timetableResultService.deleteTimetableResult(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得排课结果")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:timetable-result:query')")
    public CommonResult<TimetableResultRespVO> getTimetableResult(@RequestParam("id") Long id) {
        TimetableResultDO timetableResult = timetableResultService.getTimetableResult(id);
        return success(BeanUtils.toBean(timetableResult, TimetableResultRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得课程计划")
    @PreAuthorize("@ss.hasPermission('school:timetable-result:query')")
    public CommonResult<List<TimetableResultRespVO>> getTimetableResultList(@Valid TimetableResultListReqVO reqVO) {
        Integer queryType = reqVO.getQueryType();
        List<TimetableResultDO> resultList = null;

        if (CoursePlanQueryTypeEnum.GRADE.getType().equals(queryType)) {
            if (reqVO.getGradeId() == null) {
                throw exception(GRADE_NOT_EXISTS);
            }
            resultList = timetableResultService.getTimetableResultList(reqVO.getTimetableId(), reqVO.getGradeId(), null, null, null, null);
        } else {
            // 按教师查询课程表
            if (reqVO.getTeacherId() == null) {
                throw exception(TEACHER_NOT_EXISTS);
            }
            resultList = timetableResultService.getTimetableResultList(reqVO.getTimetableId(), null, reqVO.getTeacherId(), null, null, null);

            // 当按教师查询时，由于早自习无法直接挂教师Id, 所以正常查询无法查询到早自习课程，需要单独查询
            List<TimetableResultDO> morningResultList = new ArrayList<>();
            SubjectDO subjectChinese = subjectService.getSubject(CourseNameEnum.CHINESE.getName());
            SubjectDO subjectEnglish = subjectService.getSubject(CourseNameEnum.ENGLISH.getName());
            CourseTypeDO courseType = courseTypeService.getCourseType(CourseTypeEnum.MORNING.getName());
            if (teacherService.hasSubject(reqVO.getTeacherId(), subjectChinese.getId())) {
                morningResultList.addAll(timetableResultService.getTimetableResultList(reqVO.getTimetableId(), null, null, courseType.getId(), subjectChinese.getId(), null));
            }
            if (teacherService.hasSubject(reqVO.getTeacherId(), subjectEnglish.getId())) {
                morningResultList.addAll(timetableResultService.getTimetableResultList(reqVO.getTimetableId(), null, null, courseType.getId(), subjectEnglish.getId(), null));
            }
            // 由于这里早自习查询时没有指定年级， 所以需要去重
            morningResultList = CollUtil.distinct(morningResultList, e -> StrUtil.format("{}_{}_{}_{}", e.getWeek(), e.getCourseTypeId(), e.getSubjectId(), e.getTimeSlotId()), true);
            resultList.addAll(morningResultList);
        }

        List<GradeDO> gradeList = gradeService.getAll();
        List<TeacherDO> teacherList = teacherService.getAll();
        List<SubjectDO> subjectList = subjectService.getAll();
        List<TimeSlotDO> timeSlotList = timeSlotService.getAll();
        List<CourseTypeDO> courseTypeList = courseTypeService.getAll();

        return success(TimetableResultConvert.INSTANCE.convertList(resultList, gradeList, teacherList, subjectList, timeSlotList, courseTypeList));
    }

    /**
     * 生成早自习
     */
    private void generateMorning() {

    }
}