package cn.iocoder.yudao.module.school.controller.admin.rule;

import cn.iocoder.yudao.module.school.convert.rule.FillRuleConvert;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.service.course.TimeSlotService;
import cn.iocoder.yudao.module.school.service.grade.GradeService;
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

import cn.iocoder.yudao.module.school.controller.admin.rule.vo.*;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.FillRuleDO;
import cn.iocoder.yudao.module.school.service.rule.FillRuleService;

@Tag(name = "管理后台 - 补课规则")
@RestController
@RequestMapping("/school/fill-rule")
@Validated
@RequiredArgsConstructor
public class FillRuleController {

    private final FillRuleService fillRuleService;
    private final GradeService gradeService;
    private final TimeSlotService timeSlotService;

    @PostMapping("/create")
    @Operation(summary = "创建补课规则")
    @PreAuthorize("@ss.hasPermission('school:rule:create')")
    public CommonResult<Long> createFillRule(@Valid @RequestBody FillRuleSaveReqVO createReqVO) {
        return success(fillRuleService.createFillRule(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新补课规则")
    @PreAuthorize("@ss.hasPermission('school:rule:update')")
    public CommonResult<Boolean> updateFillRule(@Valid @RequestBody FillRuleSaveReqVO updateReqVO) {
        fillRuleService.updateFillRule(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除补课规则")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('school:rule:delete')")
    public CommonResult<Boolean> deleteFillRule(@RequestParam("id") Long id) {
        fillRuleService.deleteFillRule(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得补课规则")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:rule:query')")
    public CommonResult<FillRuleRespVO> getFillRule(@RequestParam("id") Long id) {
        FillRuleDO fillRule = fillRuleService.getFillRule(id);
        return success(BeanUtils.toBean(fillRule, FillRuleRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得补课规则分页")
    @PreAuthorize("@ss.hasPermission('school:rule:query')")
    public CommonResult<PageResult<FillRulePageRespVO>> getFillRulePage(@Valid FillRulePageReqVO pageReqVO) {
        PageResult<FillRuleDO> pageResult = fillRuleService.getFillRulePage(pageReqVO);

        List<GradeDO> gradeList = gradeService.getAll();
        List<TimeSlotDO> timeSlotList = timeSlotService.getAll();

        return success(FillRuleConvert.INSTANCE.convertPage(pageResult, gradeList, timeSlotList));
    }
}