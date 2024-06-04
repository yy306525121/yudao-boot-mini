package cn.iocoder.yudao.module.school.controller.admin.rule;

import cn.iocoder.yudao.module.school.convert.rule.ExamRuleConvert;
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
import cn.iocoder.yudao.module.school.dal.dataobject.rule.ExamRuleDO;
import cn.iocoder.yudao.module.school.service.rule.ExamRuleService;

@Tag(name = "管理后台 - 考试时间规则")
@RestController
@RequestMapping("/school/exam-rule")
@Validated
@RequiredArgsConstructor
public class ExamRuleController {

    private final ExamRuleService examRuleService;
    private final GradeService gradeService;
    private final TimeSlotService timeSlotService;

    @PostMapping("/create")
    @Operation(summary = "创建考试时间规则")
    @PreAuthorize("@ss.hasPermission('school:exam-rule:create')")
    public CommonResult<Long> createExamRule(@Valid @RequestBody ExamRuleSaveReqVO createReqVO) {
        return success(examRuleService.createExamRule(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新考试时间规则")
    @PreAuthorize("@ss.hasPermission('school:exam-rule:update')")
    public CommonResult<Boolean> updateExamRule(@Valid @RequestBody ExamRuleSaveReqVO updateReqVO) {
        examRuleService.updateExamRule(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除考试时间规则")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('school:exam-rule:delete')")
    public CommonResult<Boolean> deleteExamRule(@RequestParam("id") Long id) {
        examRuleService.deleteExamRule(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得考试时间规则")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:exam-rule:query')")
    public CommonResult<ExamRuleRespVO> getExamRule(@RequestParam("id") Long id) {
        ExamRuleDO examRule = examRuleService.getExamRule(id);
        return success(BeanUtils.toBean(examRule, ExamRuleRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得考试时间规则分页")
    @PreAuthorize("@ss.hasPermission('school:exam-rule:query')")
    public CommonResult<PageResult<ExamRulePageRespVO>> getExamRulePage(@Valid ExamRulePageReqVO pageReqVO) {
        PageResult<ExamRuleDO> pageResult = examRuleService.getExamRulePage(pageReqVO);

        List<GradeDO> gradeList = gradeService.getAll();
        List<TimeSlotDO> timeSlotList = timeSlotService.getAll();

        return success(ExamRuleConvert.INSTANCE.convertPage(pageResult, gradeList, timeSlotList));
    }

}