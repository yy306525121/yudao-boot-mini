package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 课程类型新增/修改 Request VO")
@Data
public class CourseTypeSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "4634")
    private Long id;

    @Schema(description = "课程类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "课程类型不能为空")
    @DiffLogField(name = "课程类型")
    private String name;

    @Schema(description = "课程类型值", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "课程类型值不能为空")
    @DiffLogField(name = "课程类型值")
    private Integer type;

    @Schema(description = "课时数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "课时数不能为空")
    @DiffLogField(name = "课时数")
    private BigDecimal num;

    @Schema(description = "显示排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "显示排序不能为空")
    @DiffLogField(name = "显示排序")
    private Integer sort;

}