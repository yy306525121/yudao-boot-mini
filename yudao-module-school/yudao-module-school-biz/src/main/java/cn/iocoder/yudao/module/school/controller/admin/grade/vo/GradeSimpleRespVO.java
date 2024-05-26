package cn.iocoder.yudao.module.school.controller.admin.grade.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 年级精简信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeSimpleRespVO {
    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "22713")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "班级名称", example = "张三")
    @ExcelProperty("班级名称")
    private String name;

    @Schema(description = "所属年级编号", example = "30353")
    @ExcelProperty("所属年级编号")
    private Long parentId;
}
