package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 课时费明细新增/修改 Request VO")
@Data
public class CourseFeeSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "12668")
    private Long id;

    @Schema(description = "课时费", example = "7505")
    private BigDecimal count;

    @Schema(description = "教师编号", example = "32196")
    private Long teacherId;

    @Schema(description = "班级编号", example = "16990")
    private Long gradeId;

    @Schema(description = "课程ID", example = "8194")
    private Long subjectId;

    @Schema(description = "星期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "星期不能为空")
    private Integer week;

    @Schema(description = "课程节次编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2920")
    @NotNull(message = "课程节次编号不能为空")
    private Long timeSlotId;

    @Schema(description = "日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "日期不能为空")
    private LocalDate date;

}