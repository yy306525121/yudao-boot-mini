package cn.iocoder.yudao.module.school.controller.admin.classinfo;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.school.controller.admin.classinfo.vo.ClassInfoListReqVO;
import cn.iocoder.yudao.module.school.controller.admin.classinfo.vo.ClassInfoRespVO;
import cn.iocoder.yudao.module.school.controller.admin.classinfo.vo.ClassInfoSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.classinfo.ClassInfoDO;
import cn.iocoder.yudao.module.school.service.classinfo.ClassInfoService;
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
@RequestMapping("/school/class-info")
@Validated
public class ClassInfoController {

    @Resource
    private ClassInfoService classInfoService;

    @PostMapping("/create")
    @Operation(summary = "创建班级")
    @PreAuthorize("@ss.hasPermission('school:class-info:create')")
    public CommonResult<Long> createClassInfo(@Valid @RequestBody ClassInfoSaveReqVO createReqVO) {
        return success(classInfoService.createClassInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新班级")
    @PreAuthorize("@ss.hasPermission('school:class-info:update')")
    public CommonResult<Boolean> updateClassInfo(@Valid @RequestBody ClassInfoSaveReqVO updateReqVO) {
        classInfoService.updateClassInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除班级")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('school:class-info:delete')")
    public CommonResult<Boolean> deleteClassInfo(@RequestParam("id") Long id) {
        classInfoService.deleteClassInfo(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得班级")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:class-info:query')")
    public CommonResult<ClassInfoRespVO> getClassInfo(@RequestParam("id") Long id) {
        ClassInfoDO classInfo = classInfoService.getClassInfo(id);
        return success(BeanUtils.toBean(classInfo, ClassInfoRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得班级列表")
    @PreAuthorize("@ss.hasPermission('school:class-info:query')")
    public CommonResult<List<ClassInfoRespVO>> getClassInfoList(@Valid ClassInfoListReqVO listReqVO) {
        List<ClassInfoDO> list = classInfoService.getClassInfoList(listReqVO);
        return success(BeanUtils.toBean(list, ClassInfoRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出班级 Excel")
    @PreAuthorize("@ss.hasPermission('school:class-info:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportClassInfoExcel(@Valid ClassInfoListReqVO listReqVO,
                                     HttpServletResponse response) throws IOException {
        List<ClassInfoDO> list = classInfoService.getClassInfoList(listReqVO);
        // 导出 Excel
        ExcelUtils.write(response, "班级.xls", "数据", ClassInfoRespVO.class,
                BeanUtils.toBean(list, ClassInfoRespVO.class));
    }

}
