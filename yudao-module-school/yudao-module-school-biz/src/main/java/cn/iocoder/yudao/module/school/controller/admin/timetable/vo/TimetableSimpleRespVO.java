package cn.iocoder.yudao.module.school.controller.admin.timetable.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 排课计划精简 Response VO")
@Data
public class TimetableSimpleRespVO {
    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13896")
    private Long id;

    @Schema(description = "排课名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    private String name;
}
