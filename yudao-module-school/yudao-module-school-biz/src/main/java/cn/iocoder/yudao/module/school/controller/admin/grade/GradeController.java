package cn.iocoder.yudao.module.school.controller.admin.grade;

import cn.hutool.core.lang.tree.Tree;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeListReqVO;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeRespVO;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.service.grade.GradeService;
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

@Tag(name = "管理后台 - 班级")
@RestController
@RequestMapping("/school/grade")
@Validated
public class GradeController {

    @Resource
    private GradeService gradeService;

    @PostMapping("/create")
    @Operation(summary = "创建班级")
    @PreAuthorize("@ss.hasPermission('school:grade:create')")
    public CommonResult<Long> createGrade(@Valid @RequestBody GradeSaveReqVO createReqVO) {
        return success(gradeService.createGrade(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新班级")
    @PreAuthorize("@ss.hasPermission('school:grade:update')")
    public CommonResult<Boolean> updateGrade(@Valid @RequestBody GradeSaveReqVO updateReqVO) {
        gradeService.updateGrade(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除班级")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('school:grade:delete')")
    public CommonResult<Boolean> deleteGrade(@RequestParam("id") Long id) {
        gradeService.deleteGrade(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得班级")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:grade:query')")
    public CommonResult<GradeRespVO> getGrade(@RequestParam("id") Long id) {
        GradeDO grade = gradeService.getGrade(id);
        return success(BeanUtils.toBean(grade, GradeRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得班级列表")
    @PreAuthorize("@ss.hasPermission('school:grade:query')")
    public CommonResult<List<GradeRespVO>> getGradeList(@Valid GradeListReqVO listReqVO) {
        List<GradeDO> list = gradeService.getGradeList(listReqVO);
        return success(BeanUtils.toBean(list, GradeRespVO.class));
    }

    @GetMapping("/tree")
    @Operation(summary = "获得班级列表")
    @PreAuthorize("@ss.hasPermission('school:grade:query')")
    public CommonResult<List<Tree<Long>>> getGradeTree() {
        return success(gradeService.gradeTree());
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出班级 Excel")
    @PreAuthorize("@ss.hasPermission('school:grade:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportGradeExcel(@Valid GradeListReqVO listReqVO,
                                 HttpServletResponse response) throws IOException {
        List<GradeDO> list = gradeService.getGradeList(listReqVO);
        // 导出 Excel
        ExcelUtils.write(response, "班级.xls", "数据", GradeRespVO.class,
                BeanUtils.toBean(list, GradeRespVO.class));
    }

}