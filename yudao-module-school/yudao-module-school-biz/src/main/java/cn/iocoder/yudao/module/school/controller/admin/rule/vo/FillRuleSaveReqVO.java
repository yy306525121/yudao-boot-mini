package cn.iocoder.yudao.module.school.controller.admin.rule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 补课规则新增/修改 Request VO")
@Data
public class FillRuleSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "7729")
    private Long id;

    @Schema(description = "班级ID")
    private Set<Long> gradeIds;

    @Schema(description = "补课日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "补课日期不能为空")
    private LocalDate date;

    @Schema(description = "补周几的课", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "补周几的课不能为空")
    private Integer week;

    @Schema(description = "开始补课节次", example = "1285")
    private Long startTimeSlotId;

    @Schema(description = "结束补课节次", example = "32210")
    private Long endTimeSlotId;

}