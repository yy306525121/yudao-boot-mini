package cn.iocoder.yudao.module.school.controller.admin.suject;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectPageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectRespVO;
import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectSaveReqVO;
import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectSimpleRespVO;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

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

import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.service.subject.SubjectService;

@Tag(name = "管理后台 - 科目信息")
@RestController
@RequestMapping("/school/subject")
@Validated
public class SubjectController {

    @Resource
    private SubjectService subjectService;

    @PostMapping("/create")
    @Operation(summary = "创建科目信息")
    @PreAuthorize("@ss.hasPermission('school:subject:create')")
    public CommonResult<Long> createSubject(@Valid @RequestBody SubjectSaveReqVO createReqVO) {
        return success(subjectService.createSubject(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新科目信息")
    @PreAuthorize("@ss.hasPermission('school:subject:update')")
    public CommonResult<Boolean> updateSubject(@Valid @RequestBody SubjectSaveReqVO updateReqVO) {
        subjectService.updateSubject(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除科目信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('school:subject:delete')")
    public CommonResult<Boolean> deleteSubject(@RequestParam("id") Long id) {
        subjectService.deleteSubject(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得科目信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:subject:query')")
    public CommonResult<SubjectRespVO> getSubject(@RequestParam("id") Long id) {
        SubjectDO subject = subjectService.getSubject(id);
        return success(BeanUtils.toBean(subject, SubjectRespVO.class));
    }

    @GetMapping(value = {"/list-all-simple", "simple-list"})
    @Operation(summary = "获取科目全列表", description = "主要用于前端的下拉选项")
    public CommonResult<List<SubjectSimpleRespVO>> getSimplePostList() {
        // 获得岗位列表，只要开启状态的
        List<SubjectDO> list = subjectService.getSubjectList(null);
        // 排序后，返回给前端
        list.sort(Comparator.comparing(SubjectDO::getSort));
        return success(BeanUtils.toBean(list, SubjectSimpleRespVO.class));
    }


    @GetMapping("/page")
    @Operation(summary = "获得科目信息分页")
    @PreAuthorize("@ss.hasPermission('school:subject:query')")
    public CommonResult<PageResult<SubjectRespVO>> getSubjectPage(@Valid SubjectPageReqVO pageReqVO) {
        PageResult<SubjectDO> pageResult = subjectService.getSubjectPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SubjectRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出科目信息 Excel")
    @PreAuthorize("@ss.hasPermission('school:subject:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSubjectExcel(@Valid SubjectPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SubjectDO> list = subjectService.getSubjectPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "科目信息.xls", "数据", SubjectRespVO.class,
                        BeanUtils.toBean(list, SubjectRespVO.class));
    }

}