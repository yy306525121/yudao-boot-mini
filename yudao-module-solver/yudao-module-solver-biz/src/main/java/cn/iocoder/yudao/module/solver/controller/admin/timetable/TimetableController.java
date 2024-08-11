package cn.iocoder.yudao.module.solver.controller.admin.timetable;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.solver.controller.admin.timetable.vo.TimetablePageReqVO;
import cn.iocoder.yudao.module.solver.controller.admin.timetable.vo.TimetableRespVO;
import cn.iocoder.yudao.module.solver.controller.admin.timetable.vo.TimetableSaveReqVO;
import cn.iocoder.yudao.module.solver.dal.dataobject.timetable.TimetableDO;
import cn.iocoder.yudao.module.solver.service.timetable.TimetableService;
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

@Tag(name = "管理后台 - 排课")
@RestController
@RequestMapping("/solver/timetable")
@Validated
@RequiredArgsConstructor
public class TimetableController {
    private final TimetableService timetableService;

    @PostMapping("/create")
    @Operation(summary = "创建排课")
    @PreAuthorize("@ss.hasPermission('solver:timetable:create')")
    public CommonResult<Long> createTimetable(@Valid @RequestBody TimetableSaveReqVO createReqVO) {
        return success(timetableService.createTimetable(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新排课")
    @PreAuthorize("@ss.hasPermission('solver:timetable:update')")
    public CommonResult<Boolean> updateTimetable(@Valid @RequestBody TimetableSaveReqVO updateReqVO) {
        timetableService.updateTimetable(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除排课")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('solver:timetable:delete')")
    public CommonResult<Boolean> deleteTimetable(@RequestParam("id") Long id) {
        timetableService.deleteTimetable(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得排课")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('solver:timetable:query')")
    public CommonResult<TimetableRespVO> getTimetable(@RequestParam("id") Long id) {
        TimetableDO timetable = timetableService.getTimetable(id);
        return success(BeanUtils.toBean(timetable, TimetableRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得排课分页")
    @PreAuthorize("@ss.hasPermission('solver:timetable:query')")
    public CommonResult<PageResult<TimetableRespVO>> getTimetablePage(@Valid TimetablePageReqVO pageReqVO) {
        PageResult<TimetableDO> pageResult = timetableService.getTimetablePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, TimetableRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出排课 Excel")
    @PreAuthorize("@ss.hasPermission('solver:timetable:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTimetableExcel(@Valid TimetablePageReqVO pageReqVO,
                                     HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TimetableDO> list = timetableService.getTimetablePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "排课.xls", "数据", TimetableRespVO.class,
                BeanUtils.toBean(list, TimetableRespVO.class));
    }
}
