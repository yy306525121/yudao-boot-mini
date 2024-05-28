package cn.iocoder.yudao.module.school.controller.admin.course;

import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherPageReqVO;
import cn.iocoder.yudao.module.school.service.teacher.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.school.controller.admin.course.vo.*;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import cn.iocoder.yudao.module.school.service.course.CourseFeeService;

@Tag(name = "管理后台 - 课时费明细")
@RestController
@RequestMapping("/school/course-fee")
@Validated
@RequiredArgsConstructor
public class CourseFeeController {

    private final CourseFeeService courseFeeService;
    private final TeacherService teacherService;

    @GetMapping("/page")
    @Operation(summary = "获得课时费明细分页")
    @PreAuthorize("@ss.hasPermission('school:course-fee:query')")
    public CommonResult<PageResult<CourseFeeRespVO>> getCourseFeePage(@Valid CourseFeePageReqVO pageReqVO) {
        TeacherPageReqVO teacherPageReqVO = new TeacherPageReqVO();
        teacherService.getTeacherPage(teacherPageReqVO);

        PageResult<CourseFeeDO> pageResult = courseFeeService.getCourseFeePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CourseFeeRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出课时费明细 Excel")
    @PreAuthorize("@ss.hasPermission('school:course-fee:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCourseFeeExcel(@Valid CourseFeePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CourseFeeDO> list = courseFeeService.getCourseFeePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "课时费明细.xls", "数据", CourseFeeRespVO.class,
                        BeanUtils.toBean(list, CourseFeeRespVO.class));
    }

}