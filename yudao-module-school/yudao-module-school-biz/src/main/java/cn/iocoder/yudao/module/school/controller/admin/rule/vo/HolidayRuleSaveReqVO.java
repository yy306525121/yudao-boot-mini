package cn.iocoder.yudao.module.school.controller.admin.rule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 放假时间规则新增/修改 Request VO")
@Data
public class HolidayRuleSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "5080")
    private Long id;

    @Schema(description = "班级ID的数组json", example = "25025")
    private String gradeId;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "开始节次", example = "29761")
    private Long startTimeSlotId;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "结束节次", example = "15540")
    private Long endTimeSlotId;

}