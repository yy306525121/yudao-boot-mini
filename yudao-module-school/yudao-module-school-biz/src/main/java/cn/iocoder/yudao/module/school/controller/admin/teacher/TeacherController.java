package cn.iocoder.yudao.module.school.controller.admin.teacher;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.*;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.service.teacher.TeacherService;
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

/**
 * @author yangzy
 */
@Tag(name = "管理后台 - 教师")
@RestController
@RequestMapping("/school/teacher")
@Validated
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;

    @PostMapping("create")
    @Operation(summary = "创建教师")
    @PreAuthorize("@ss.hasPermission('school:teacher:create')")
    public CommonResult<Long> createTeacher(@Valid @RequestBody TeacherSaveReqVO reqVO) {
        Long teacherId = teacherService.createTeacher(reqVO);
        return CommonResult.success(teacherId);
    }


    @PutMapping("update")
    @Operation(summary = "更新教师")
    @PreAuthorize("@ss.hasPermission('school:teacher:update')")
    public CommonResult<Boolean> updateTeacher(@Valid @RequestBody TeacherSaveReqVO reqVO) {
        teacherService.updateTeacher(reqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("delete")
    @Operation(summary = "删除教师")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:teacher:delete')")
    public CommonResult<Boolean> deleteTeacher(@RequestParam("id") Long id) {
        teacherService.deleteTeacher(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得教师分页列表")
    @PreAuthorize("@ss.hasPermission('school:teacher:query')")
    public CommonResult<PageResult<TeacherRespVO>> getUserPage(@Valid TeacherPageReqVO pageReqVO) {
        PageResult<TeacherDO> pageResult = teacherService.getTeacherPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, TeacherRespVO.class));
    }

    @GetMapping(value = {"/list-all-simple", "/simple-list"})
    @Operation(summary = "获取教师精简信息列表")
    public CommonResult<List<TeacherSimpleRespVO>> getSimpleTeacherList() {
        List<TeacherDO> list = teacherService.getTeacherList(new TeacherListReqVO().setStatus(CommonStatusEnum.ENABLE.getStatus()));
        return success(BeanUtils.toBean(list, TeacherSimpleRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得教师详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:teacher:query')")
    public CommonResult<TeacherRespVO> getUser(@RequestParam("id") Long id) {
        TeacherDO teacher = teacherService.getTeacher(id);

        return success(BeanUtils.toBean(teacher, TeacherRespVO.class));
    }

    @GetMapping("/export")
    @Operation(summary = "教师管理")
    @PreAuthorize("@ss.hasPermission('school:teacher:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void export(HttpServletResponse response, @Validated TeacherPageReqVO reqVO) throws IOException {
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TeacherDO> list = teacherService.getTeacherPage(reqVO).getList();
        // 输出
        ExcelUtils.write(response, "教师数据.xls", "教师列表", TeacherRespVO.class,
                BeanUtils.toBean(list, TeacherRespVO.class));
    }
}
