package cn.iocoder.yudao.module.school.controller.admin.timetable.vo.custom;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 排课定制 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TimetableCustomRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21807")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "排课计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "26657")
    @ExcelProperty("排课计划编号")
    private Long timetableId;

    @Schema(description = "是否固定", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否固定")
    private Boolean fixed;

    @Schema(description = "权重", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("权重")
    private Long weight;

    @Schema(description = "星期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("星期")
    private Integer week;

    @Schema(description = "年级", requiredMode = Schema.RequiredMode.REQUIRED, example = "20859")
    @ExcelProperty("年级")
    private Long gradeId;

    @Schema(description = "课程类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "4580")
    @ExcelProperty("课程类型")
    private Long courseTypeId;

    @Schema(description = "科目", example = "16563")
    @ExcelProperty("科目")
    private Long subjectId;

    @Schema(description = "教师", example = "25221")
    @ExcelProperty("教师")
    private Long teacherId;

    @Schema(description = "课程节次", requiredMode = Schema.RequiredMode.REQUIRED, example = "8428")
    @ExcelProperty("课程节次")
    private Long timeSlotId;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}