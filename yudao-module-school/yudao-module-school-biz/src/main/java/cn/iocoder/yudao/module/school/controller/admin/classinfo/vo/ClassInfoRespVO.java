package cn.iocoder.yudao.module.school.controller.admin.classinfo.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 班级 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ClassInfoRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2793")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "班级名称", example = "张三")
    @ExcelProperty("班级名称")
    private String name;

    @Schema(description = "显示排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("显示排序")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}