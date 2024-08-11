package cn.iocoder.yudao.module.school.controller.admin.timetable.vo.custom;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 排课定制新增/修改 Request VO")
@Data
public class TimetableCustomSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21807")
    private Long id;

    @Schema(description = "排课计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "26657")
    @NotNull(message = "排课计划编号不能为空")
    private Long timetableId;

    @Schema(description = "是否固定", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否固定不能为空")
    private Boolean fixed;

    @Schema(description = "权重", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "权重不能为空")
    private Long weight;

    @Schema(description = "星期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "星期不能为空")
    private Integer week;

    @Schema(description = "年级", requiredMode = Schema.RequiredMode.REQUIRED, example = "20859")
    @NotNull(message = "年级不能为空")
    private Long gradeId;

    @Schema(description = "课程类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "4580")
    @NotNull(message = "课程类型不能为空")
    private Long courseTypeId;

    @Schema(description = "科目", example = "16563")
    private Long subjectId;

    @Schema(description = "教师", example = "25221")
    private Long teacherId;

    @Schema(description = "课程节次", requiredMode = Schema.RequiredMode.REQUIRED, example = "8428")
    @NotNull(message = "课程节次不能为空")
    private Long timeSlotId;

}