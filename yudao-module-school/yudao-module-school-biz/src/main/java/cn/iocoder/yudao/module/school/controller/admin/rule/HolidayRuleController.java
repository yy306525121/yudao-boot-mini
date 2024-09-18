package cn.iocoder.yudao.module.school.controller.admin.rule;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.HolidayRulePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.HolidayRulePageRespVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.HolidayRuleRespVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.HolidayRuleSaveReqVO;
import cn.iocoder.yudao.module.school.convert.rule.HolidayRuleConvert;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.HolidayRuleDO;
import cn.iocoder.yudao.module.school.service.course.TimeSlotService;
import cn.iocoder.yudao.module.school.service.grade.GradeService;
import cn.iocoder.yudao.module.school.service.rule.HolidayRuleService;
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


@Tag(name = "管理后台 - 放假时间规则")
@RestController
@RequestMapping("/rule/holiday-rule")
@Validated
@RequiredArgsConstructor
public class HolidayRuleController {

    private final HolidayRuleService holidayRuleService;
    private final TimeSlotService timeSlotService;
    private final GradeService gradeService;


    @PostMapping("/create")
    @Operation(summary = "创建放假时间规则")
    @PreAuthorize("@ss.hasPermission('school:rule:create')")
    public CommonResult<Long> createHolidayRule(@Valid @RequestBody HolidayRuleSaveReqVO createReqVO) {
        return success(holidayRuleService.createHolidayRule(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新放假时间规则")
    @PreAuthorize("@ss.hasPermission('school:rule:update')")
    public CommonResult<Boolean> updateHolidayRule(@Valid @RequestBody HolidayRuleSaveReqVO updateReqVO) {
        holidayRuleService.updateHolidayRule(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除放假时间规则")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('school:rule:delete')")
    public CommonResult<Boolean> deleteHolidayRule(@RequestParam("id") Long id) {
        holidayRuleService.deleteHolidayRule(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得放假时间规则")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:rule:query')")
    public CommonResult<HolidayRuleRespVO> getHolidayRule(@RequestParam("id") Long id) {
        HolidayRuleDO holidayRule = holidayRuleService.getHolidayRule(id);
        return success(BeanUtils.toBean(holidayRule, HolidayRuleRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得放假时间规则分页")
    @PreAuthorize("@ss.hasPermission('school:rule:query')")
    public CommonResult<PageResult<HolidayRulePageRespVO>> getHolidayRulePage(@Valid HolidayRulePageReqVO pageReqVO) {
        PageResult<HolidayRuleDO> pageResult = holidayRuleService.getHolidayRulePage(pageReqVO);

        List<TimeSlotDO> timeSlotList = timeSlotService.getAll();
        List<GradeDO> gradeList = gradeService.getAll();


        return success(HolidayRuleConvert.INSTANCE.convertPage(pageResult, gradeList, timeSlotList));
    }
}