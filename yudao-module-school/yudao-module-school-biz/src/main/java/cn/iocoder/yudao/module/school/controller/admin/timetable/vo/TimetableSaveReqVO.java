package cn.iocoder.yudao.module.school.controller.admin.timetable.vo;

import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 排课新增/修改 Request VO")
@Data
public class TimetableSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13896")
    private Long id;

    @Schema(description = "排课名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "排课名称不能为空")
    @DiffLogField(name = "排课计划名称")
    private String name;

    private Integer status;
}
