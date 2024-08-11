package cn.iocoder.yudao.module.school.controller.admin.timetable.vo.result;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 排课结果新增/修改 Request VO")
@Data
public class TimetableResultSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31925")
    private Long id;

    @Schema(description = "排课计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15695")
    @NotNull(message = "排课计划编号不能为空")
    private Long timetableId;

    @Schema(description = "星期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "星期不能为空")
    private Integer week;

    @Schema(description = "年级", requiredMode = Schema.RequiredMode.REQUIRED, example = "6218")
    @NotNull(message = "年级不能为空")
    private Long gradeId;

    @Schema(description = "课程类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "11699")
    @NotNull(message = "课程类型不能为空")
    private Long courseTypeId;

    @Schema(description = "科目", example = "29450")
    private Long subjectId;

    @Schema(description = "教师", example = "4143")
    private Long teacherId;

    @Schema(description = "课程节次", requiredMode = Schema.RequiredMode.REQUIRED, example = "28567")
    @NotNull(message = "课程节次不能为空")
    private Long timeSlotId;

}