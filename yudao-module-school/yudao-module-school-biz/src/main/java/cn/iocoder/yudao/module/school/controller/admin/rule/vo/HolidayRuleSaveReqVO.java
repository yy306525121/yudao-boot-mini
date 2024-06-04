package cn.iocoder.yudao.module.school.controller.admin.rule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Schema(description = "管理后台 - 放假时间规则新增/修改 Request VO")
@Data
public class HolidayRuleSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "5080")
    private Long id;

    @Schema(description = "班级ID的数组json", example = "25025")
    @NotEmpty(message = "班级不能为空")
    private Set<Long> gradeIds;

    @Schema(description = "开始日期")
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @Schema(description = "开始节次", example = "29761")
    @NotNull(message = "开始节次不能为空")
    private Long startTimeSlotId;

    @Schema(description = "结束日期")
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @Schema(description = "结束节次", example = "15540")
    @NotNull(message = "结束节次不能为空")
    private Long endTimeSlotId;

}