package cn.iocoder.yudao.module.school.controller.admin.course;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.*;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.service.course.TimeSlotService;
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

@Tag(name = "管理后台 - 课程节次")
@RestController
@RequestMapping("/school/time-slot")
@Validated
public class TimeSlotController {

    @Resource
    private TimeSlotService timeSlotService;

    @PostMapping("/create")
    @Operation(summary = "创建课程节次")
    @PreAuthorize("@ss.hasPermission('school:time-slot:create')")
    public CommonResult<Long> createTimeSlot(@Valid @RequestBody TimeSlotSaveReqVO createReqVO) {
        return success(timeSlotService.createTimeSlot(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新课程节次")
    @PreAuthorize("@ss.hasPermission('school:time-slot:update')")
    public CommonResult<Boolean> updateTimeSlot(@Valid @RequestBody TimeSlotSaveReqVO updateReqVO) {
        timeSlotService.updateTimeSlot(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除课程节次")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('school:time-slot:delete')")
    public CommonResult<Boolean> deleteTimeSlot(@RequestParam("id") Long id) {
        timeSlotService.deleteTimeSlot(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得课程节次")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:time-slot:query')")
    public CommonResult<TimeSlotRespVO> getTimeSlot(@RequestParam("id") Long id) {
        TimeSlotDO timeSlot = timeSlotService.getTimeSlot(id);
        return success(BeanUtils.toBean(timeSlot, TimeSlotRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得课程节次分页")
    @PreAuthorize("@ss.hasPermission('school:time-slot:query')")
    public CommonResult<List<TimeSlotRespVO>> getTimeSlotList(@Valid TimeSlotListReqVO listReqVO) {
        List<TimeSlotDO> list = timeSlotService.getTimeSlotList(listReqVO);
        return success(BeanUtils.toBean(list, TimeSlotRespVO.class));
    }


    @GetMapping(value = {"/list-all-simple", "/simple-list"})
    @Operation(summary = "获取教师精简信息列表")
    public CommonResult<List<TimeSlotSimpleRespVO>> getSimpleTeacherList() {
        List<TimeSlotDO> list = timeSlotService.getTimeSlotList(new TimeSlotListReqVO());
        return success(BeanUtils.toBean(list, TimeSlotSimpleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出课程节次 Excel")
    @PreAuthorize("@ss.hasPermission('school:time-slot:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTimeSlotExcel(@Valid TimeSlotPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TimeSlotDO> list = timeSlotService.getTimeSlotPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "课程节次.xls", "数据", TimeSlotRespVO.class,
                        BeanUtils.toBean(list, TimeSlotRespVO.class));
    }

}