package cn.iocoder.yudao.module.school.controller.admin.rule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 考试时间规则新增/修改 Request VO")
@Data
public class ExamRuleSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "7949")
    private Long id;

    @Schema(description = "班级ID")
    @NotEmpty(message = "班级不能为空")
    private Set<Long> gradeIds;

    @Schema(description = "开始日期")
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @Schema(description = "开始节次", example = "3236")
    @NotNull(message = "开始节次不能为空")
    private Long startTimeSlotId;

    @Schema(description = "结束日期")
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @Schema(description = "结束节次", example = "23910")
    @NotNull(message = "结束节次不能为空")
    private Long endTimeSlotId;

}