package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - 课程计划新增/修改 Request VO")
@Data
public class CoursePlanSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7092")
    private Long id;

    @Schema(description = "星期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "星期不能为空")
    private Integer week;

    @Schema(description = "年级", requiredMode = Schema.RequiredMode.REQUIRED, example = "8052")
    @NotNull(message = "年级不能为空")
    private Long gradeId;

    @Schema(description = "课程类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "28293")
    @NotNull(message = "课程类型不能为空")
    private Long courseTypeId;

    @Schema(description = "科目", example = "10343")
    private Long subjectId;

    @Schema(description = "教师", example = "10231")
    private Long teacherId;

    @Schema(description = "课程节次", requiredMode = Schema.RequiredMode.REQUIRED, example = "29002")
    @NotNull(message = "课程节次不能为空")
    private Long timeSlotId;

    @Schema(description = "课程生效日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "课程生效日期不能为空")
    private LocalDate start;

    @Schema(description = "课程失效日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "课程失效日期不能为空")
    private LocalDate end;

}