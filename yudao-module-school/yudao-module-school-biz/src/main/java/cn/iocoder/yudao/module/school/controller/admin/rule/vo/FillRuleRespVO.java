package cn.iocoder.yudao.module.school.controller.admin.rule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 补课规则 Response VO")
@Data
@ExcelIgnoreUnannotated
public class FillRuleRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "7729")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "班级ID")
    @ExcelProperty("班级ID")
    private String gradeIds;

    @Schema(description = "补课日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("补课日期")
    private LocalDate date;

    @Schema(description = "补周几的课", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("补周几的课")
    private Integer week;

    @Schema(description = "开始补课节次", example = "1285")
    @ExcelProperty("开始补课节次")
    private Long startTimeSlotId;

    @Schema(description = "结束补课节次", example = "32210")
    @ExcelProperty("结束补课节次")
    private Long endTimeSlotId;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}