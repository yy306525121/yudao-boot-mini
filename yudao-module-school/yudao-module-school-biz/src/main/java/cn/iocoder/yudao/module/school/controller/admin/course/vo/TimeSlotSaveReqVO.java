package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;

import java.time.LocalTime;

@Schema(description = "管理后台 - 课程节次新增/修改 Request VO")
@Data
public class TimeSlotSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1407")
    private Long id;

    @Schema(description = "时段类型 1-早自习、2-白天课程、3-晚自习", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "时段类型 1-早自习、2-白天课程、3-晚自习不能为空")
    private Integer type;

    @Schema(description = "课程节次", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "课程节次不能为空")
    private Integer sort;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;

}