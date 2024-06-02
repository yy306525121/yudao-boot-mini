package cn.iocoder.yudao.module.school.controller.admin.rule.vo;

import cn.hutool.core.date.DatePattern;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 临时调课 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TransferRuleRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7381")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "调课时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("调课时间")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN)
    private LocalDate date;

    @Schema(description = "调课班级", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long gradeId;

    @Schema(description = "调课节次", requiredMode = Schema.RequiredMode.REQUIRED, example = "2789")
    @ExcelProperty("调课节次")
    private Long timeSlotId;

    @Schema(description = "调课教师ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3036")
    @ExcelProperty("调课教师ID")
    private Long fromTeacherId;

    @Schema(description = "调课教师ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "22087")
    @ExcelProperty("调课教师ID")
    private Long toTeacherId;

    @Schema(description = "调课课程", requiredMode = Schema.RequiredMode.REQUIRED, example = "3800")
    @ExcelProperty("调课课程")
    private Long fromSubjectId;

    @Schema(description = "调课课程", requiredMode = Schema.RequiredMode.REQUIRED, example = "22369")
    @ExcelProperty("调课课程")
    private Long toSubjectId;

    @Schema(description = "调课课程类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "14330")
    @ExcelProperty("调课课程类型")
    private Long fromCourseTypeId;

    @Schema(description = "调课课程类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "11973")
    @ExcelProperty("调课课程类型")
    private Long toCourseTypeId;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}