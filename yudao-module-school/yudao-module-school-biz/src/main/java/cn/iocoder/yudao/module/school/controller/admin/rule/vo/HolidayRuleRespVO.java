package cn.iocoder.yudao.module.school.controller.admin.rule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 放假时间规则 Response VO")
@Data
@ExcelIgnoreUnannotated
public class HolidayRuleRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "5080")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "班级ID的数组json", example = "25025")
    @ExcelProperty("班级ID的数组json")
    private String gradeId;

    @Schema(description = "开始日期")
    @ExcelProperty("开始日期")
    private LocalDate startDate;

    @Schema(description = "开始节次", example = "29761")
    @ExcelProperty("开始节次")
    private Long startTimeSlotId;

    @Schema(description = "结束日期")
    @ExcelProperty("结束日期")
    private LocalDate endDate;

    @Schema(description = "结束节次", example = "15540")
    @ExcelProperty("结束节次")
    private Long endTimeSlotId;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}