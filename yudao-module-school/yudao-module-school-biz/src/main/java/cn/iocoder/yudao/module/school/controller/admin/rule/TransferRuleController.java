package cn.iocoder.yudao.module.school.controller.admin.rule;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.TransferRulePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.TransferRuleRespVO;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.TransferRuleSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.TransferRuleDO;
import cn.iocoder.yudao.module.school.service.rule.TransferRuleService;
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

@Tag(name = "管理后台 - 临时调课")
@RestController
@RequestMapping("/school/transfer-rule")
@Validated
public class TransferRuleController {

    @Resource
    private TransferRuleService transferRuleService;

    @PostMapping("/create")
    @Operation(summary = "创建临时调课")
    @PreAuthorize("@ss.hasPermission('school:transfer-rule:create')")
    public CommonResult<Long> createTransferRule(@Valid @RequestBody TransferRuleSaveReqVO createReqVO) {
        return success(transferRuleService.createTransferRule(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新临时调课")
    @PreAuthorize("@ss.hasPermission('school:transfer-rule:update')")
    public CommonResult<Boolean> updateTransferRule(@Valid @RequestBody TransferRuleSaveReqVO updateReqVO) {
        transferRuleService.updateTransferRule(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除临时调课")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('school:transfer-rule:delete')")
    public CommonResult<Boolean> deleteTransferRule(@RequestParam("id") Long id) {
        transferRuleService.deleteTransferRule(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得临时调课")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:transfer-rule:query')")
    public CommonResult<TransferRuleRespVO> getTransferRule(@RequestParam("id") Long id) {
        TransferRuleDO transferRule = transferRuleService.getTransferRule(id);
        return success(BeanUtils.toBean(transferRule, TransferRuleRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得临时调课分页")
    @PreAuthorize("@ss.hasPermission('school:transfer-rule:query')")
    public CommonResult<PageResult<TransferRuleRespVO>> getTransferRulePage(@Valid TransferRulePageReqVO pageReqVO) {
        PageResult<TransferRuleDO> pageResult = transferRuleService.getTransferRulePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, TransferRuleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出临时调课 Excel")
    @PreAuthorize("@ss.hasPermission('school:transfer-rule:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTransferRuleExcel(@Valid TransferRulePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TransferRuleDO> list = transferRuleService.getTransferRulePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "临时调课.xls", "数据", TransferRuleRespVO.class,
                        BeanUtils.toBean(list, TransferRuleRespVO.class));
    }

}