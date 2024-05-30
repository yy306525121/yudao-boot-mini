package cn.iocoder.yudao.module.school.controller.admin.rule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - 临时调课新增/修改 Request VO")
@Data
public class TransferRuleSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7381")
    private Long id;

    @Schema(description = "调课时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "调课时间不能为空")
    private LocalDate date;

    @Schema(description = "调课节次", requiredMode = Schema.RequiredMode.REQUIRED, example = "2789")
    @NotNull(message = "调课节次不能为空")
    private Long timeSlotId;

    @Schema(description = "调课教师ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3036")
    @NotNull(message = "调课教师ID不能为空")
    private Long fromTeacherId;

    @Schema(description = "调课教师ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "22087")
    @NotNull(message = "调课教师ID不能为空")
    private Long toTeacherId;

    @Schema(description = "调课课程", requiredMode = Schema.RequiredMode.REQUIRED, example = "3800")
    @NotNull(message = "调课课程不能为空")
    private Long fromSubjectId;

    @Schema(description = "调课课程", requiredMode = Schema.RequiredMode.REQUIRED, example = "22369")
    @NotNull(message = "调课课程不能为空")
    private Long toSubjectId;

    @Schema(description = "调课课程类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "14330")
    @NotNull(message = "调课课程类型不能为空")
    private Long fromCourseTypeId;

    @Schema(description = "调课课程类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "11973")
    @NotNull(message = "调课课程类型不能为空")
    private Long toCourseTypeId;

}