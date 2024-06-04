package cn.iocoder.yudao.module.school.controller.admin.rule.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 考试时间规则 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ExamRuleRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "7949")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "班级ID")
    @ExcelProperty("班级ID")
    private Set<Long> gradeIds;

    @Schema(description = "开始日期")
    @ExcelProperty("开始日期")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN)
    private LocalDate startDate;

    @Schema(description = "开始节次", example = "3236")
    @ExcelProperty("开始节次")
    private Long startTimeSlotId;

    @Schema(description = "结束日期")
    @ExcelProperty("结束日期")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN)
    private LocalDate endDate;

    @Schema(description = "结束节次", example = "23910")
    @ExcelProperty("结束节次")
    private Long endTimeSlotId;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}