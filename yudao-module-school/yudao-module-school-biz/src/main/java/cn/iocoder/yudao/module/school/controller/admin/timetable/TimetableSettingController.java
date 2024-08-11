package cn.iocoder.yudao.module.school.controller.admin.timetable;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting.TimetableSettingPageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting.TimetableSettingPageRespVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting.TimetableSettingRespVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting.TimetableSettingSaveReqVO;
import cn.iocoder.yudao.module.school.convert.timetable.TimetableSettingConvert;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableSettingDO;
import cn.iocoder.yudao.module.school.service.course.CourseTypeService;
import cn.iocoder.yudao.module.school.service.grade.GradeService;
import cn.iocoder.yudao.module.school.service.subject.SubjectService;
import cn.iocoder.yudao.module.school.service.teacher.TeacherService;
import cn.iocoder.yudao.module.school.service.timetable.TimetableSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 排课计划设置")
@RestController
@RequestMapping("/school/timetable-setting")
@Validated
@RequiredArgsConstructor
public class TimetableSettingController {

    private final TimetableSettingService timetableSettingService;
    private final GradeService gradeService;
    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final CourseTypeService courseTypeService;

    @PostMapping("/create")
    @Operation(summary = "创建排课计划设置")
    @PreAuthorize("@ss.hasPermission('school:timetable-setting:create')")
    public CommonResult<Long> createTimetableSetting(@Valid @RequestBody TimetableSettingSaveReqVO createReqVO) {
        return success(timetableSettingService.createTimetableSetting(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新排课计划设置")
    @PreAuthorize("@ss.hasPermission('school:timetable-setting:update')")
    public CommonResult<Boolean> updateTimetableSetting(@Valid @RequestBody TimetableSettingSaveReqVO updateReqVO) {
        timetableSettingService.updateTimetableSetting(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除排课计划设置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('school:timetable-setting:delete')")
    public CommonResult<Boolean> deleteTimetableSetting(@RequestParam("id") Long id) {
        timetableSettingService.deleteTimetableSetting(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得排课计划设置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:timetable-setting:query')")
    public CommonResult<TimetableSettingRespVO> getTimetableSetting(@RequestParam("id") Long id) {
        TimetableSettingDO timetableSetting = timetableSettingService.getTimetableSetting(id);

        return success(BeanUtils.toBean(timetableSetting, TimetableSettingRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得排课计划设置分页")
    @PreAuthorize("@ss.hasPermission('school:timetable-setting:query')")
    public CommonResult<PageResult<TimetableSettingPageRespVO>> getTimetableSettingPage(@Valid TimetableSettingPageReqVO pageReqVO) {
        PageResult<TimetableSettingDO> pageResult = timetableSettingService.getTimetableSettingPage(pageReqVO);

        // 获取所有的班级
        List<GradeDO> gradeList = gradeService.getAll();
        // 获取所有的教师
        List<TeacherDO> teacherList = teacherService.getAll();
        // 获取所有的课程科目
        List<SubjectDO> subjectList = subjectService.getAll();
        List<CourseTypeDO> courseTypeList = courseTypeService.getAll();


        return success(TimetableSettingConvert.INSTANCE.convertPage(pageResult, gradeList, subjectList, teacherList, courseTypeList));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出排课计划设置 Excel")
    @PreAuthorize("@ss.hasPermission('school:timetable-setting:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTimetableSettingExcel(@Valid TimetableSettingPageReqVO pageReqVO,
                                            HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TimetableSettingDO> list = timetableSettingService.getTimetableSettingPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "排课计划设置.xls", "数据", TimetableSettingPageRespVO.class,
                BeanUtils.toBean(list, TimetableSettingPageRespVO.class));
    }

}
