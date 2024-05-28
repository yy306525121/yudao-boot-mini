package cn.iocoder.yudao.module.school.controller.admin.course;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseTypePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseTypeRespVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseTypeSaveReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseTypeSimpleRespVO;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherListReqVO;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherSimpleRespVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.service.course.CourseTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 课程类型")
@RestController
@RequestMapping("/school/course-type")
@Validated
public class CourseTypeController {

    @Resource
    private CourseTypeService courseTypeService;

    @PostMapping("/create")
    @Operation(summary = "创建课程类型")
    @PreAuthorize("@ss.hasPermission('school:course-type:create')")
    public CommonResult<Long> createCourseType(@Valid @RequestBody CourseTypeSaveReqVO createReqVO) {
        return success(courseTypeService.createCourseType(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新课程类型")
    @PreAuthorize("@ss.hasPermission('school:course-type:update')")
    public CommonResult<Boolean> updateCourseType(@Valid @RequestBody CourseTypeSaveReqVO updateReqVO) {
        courseTypeService.updateCourseType(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除课程类型")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('school:course-type:delete')")
    public CommonResult<Boolean> deleteCourseType(@RequestParam("id") Long id) {
        courseTypeService.deleteCourseType(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得课程类型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:course-type:query')")
    public CommonResult<CourseTypeRespVO> getCourseType(@RequestParam("id") Long id) {
        CourseTypeDO courseType = courseTypeService.getCourseType(id);
        return success(BeanUtils.toBean(courseType, CourseTypeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得课程类型分页")
    @PreAuthorize("@ss.hasPermission('school:course-type:query')")
    public CommonResult<PageResult<CourseTypeRespVO>> getCourseTypePage(@Valid CourseTypePageReqVO pageReqVO) {
        PageResult<CourseTypeDO> pageResult = courseTypeService.getCourseTypePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CourseTypeRespVO.class));
    }

    @GetMapping(value = {"/list-all-simple", "/simple-list"})
    @Operation(summary = "获取教师精简信息列表")
    public CommonResult<List<CourseTypeSimpleRespVO>> getSimpleCourseTypeList() {
        List<CourseTypeDO> courseTypeList = courseTypeService.getAll();
        return success(BeanUtils.toBean(courseTypeList, CourseTypeSimpleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出课程类型 Excel")
    @PreAuthorize("@ss.hasPermission('school:course-type:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCourseTypeExcel(@Valid CourseTypePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CourseTypeDO> list = courseTypeService.getCourseTypePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "课程类型.xls", "数据", CourseTypeRespVO.class,
                        BeanUtils.toBean(list, CourseTypeRespVO.class));
    }

}