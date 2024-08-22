package cn.iocoder.yudao.module.school.controller.admin.timetable;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.TimetablePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.TimetableRespVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.TimetableSaveReqVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.TimetableSimpleRespVO;
import cn.iocoder.yudao.module.school.convert.timetable.TimetableConvert;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableDO;
import cn.iocoder.yudao.module.school.service.timetable.TimetableResultService;
import cn.iocoder.yudao.module.school.service.timetable.TimetableService;
import cn.iocoder.yudao.module.school.timetable.domain.TimeTableProblem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.*;

@Slf4j
@Tag(name = "管理后台 - 排课")
@RestController
@RequestMapping("/school/timetable")
@Validated
@RequiredArgsConstructor
public class TimetableController {
    private final TimetableService timetableService;
    private final TimetableResultService timetableResultService;
    private final SolverManager<TimeTableProblem, Long> solverManager;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${yudao.timefold.solver.redisStatusKey}")
    private String runningJobKey;

    @GetMapping("/solve/{timetableId}")
    @Operation(summary = "开始排课")
    @PreAuthorize("@ss.hasPermission('school:timetable:solve')")
    public CommonResult<Boolean> solve(@PathVariable("timetableId") Long timetableId) {
        if (timetableService.getTimetable(timetableId) == null) {
            throw exception(TIMETABLE_NOT_EXISTS);
        }

        String key = String.format(runningJobKey, timetableId);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key)) && Boolean.TRUE.equals(redisTemplate.opsForValue().get(key))) {
            throw exception(TIMETABLE_JOB_RUNNING);
        }
        // 设置任务正在运行的标识
        redisTemplate.opsForValue().set(key, true);
        solverManager.solveAndListen(timetableId, id -> timetableService.generateProblem(timetableId), solution-> {
            // 最终获得最优解
            redisTemplate.opsForValue().set(key, false);
            timetableResultService.createTimetableResultBatch(timetableId, solution.getLessonList());
        });

        // SolverJob<TimeTableProblem, Long> solverJob = solverManager.solve(id, problem);
        // TimeTableProblem solution;
        // try {
        //     solution = solverJob.getFinalBestSolution();
        //     timetableResultService.createTimetableResultBatch(id, solution.getLessonList());
        // } catch (InterruptedException | ExecutionException e) {
        //     throw new IllegalStateException("Solving failed.", e);
        // }
        return success(true);
    }

    @GetMapping("/status/{timetableId}")
    public CommonResult<SolverStatus> getStatus(@PathVariable("timetableId") Long timetableId){
        String key = String.format(runningJobKey, timetableId);

        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            // 说明没有该任务
            throw exception(TIMETABLE_JOB_NOT_EXISTS);
        }

        SolverStatus solverStatus = solverManager.getSolverStatus(timetableId);

        return success(solverStatus);
    }

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
        return success(TimetableConvert.INSTANCE.convertPage(pageResult, redisTemplate, runningJobKey));
    }

    @GetMapping(value = {"/list-all-simple", "simple-list"})
    @Operation(summary = "获得全部排课计划数据列表", description = "一般用于管理后台缓存字典数据在本地")
    public CommonResult<List<TimetableSimpleRespVO>> getSimpleTimetableList() {
        List<TimetableDO> timetableList = timetableService.getTimetableList();
        return success(BeanUtils.toBean(timetableList, TimetableSimpleRespVO.class));
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
