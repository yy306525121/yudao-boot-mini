package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 课程节次 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TimeSlotSimpleRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1407")
    private Long id;

    @Schema(description = "课程节次顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sort;
}