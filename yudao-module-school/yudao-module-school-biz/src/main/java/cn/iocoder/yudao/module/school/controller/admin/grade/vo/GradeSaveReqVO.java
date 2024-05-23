package cn.iocoder.yudao.module.school.controller.admin.grade.vo;

import cn.iocoder.yudao.module.school.framework.operatelog.core.GradeParseFunction;
import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 班级新增/修改 Request VO")
@Data
public class GradeSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "22713")
    private Long id;

    @Schema(description = "班级名称", example = "张三")
    @NotEmpty(message = "班级名称不能为空")
    @DiffLogField(name = "名称")
    private String name;

    @Schema(description = "所属年级编号", example = "30353")
    @NotNull(message = "所属年级不能为空")
    @DiffLogField(name = "所属阶段", function = GradeParseFunction.NAME)
    private Long parentId;

    @Schema(description = "显示排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "显示排序不能为空")
    @DiffLogField(name = "显示排序")
    private Integer sort;

}