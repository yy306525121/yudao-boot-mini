package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.util.*;
import java.math.BigDecimal;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 课时费明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CourseFeeRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "12668")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "课时费", example = "7505")
    @ExcelProperty("课时费")
    private BigDecimal count;

    @Schema(description = "教师编号", example = "32196")
    @ExcelProperty("教师编号")
    private Long teacherId;

    @Schema(description = "班级编号", example = "16990")
    @ExcelProperty("班级编号")
    private Long gradeId;

    @Schema(description = "课程ID", example = "8194")
    @ExcelProperty("课程ID")
    private Long subjectId;

    @Schema(description = "星期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("星期")
    private Integer week;

    @Schema(description = "课程节次编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2920")
    @ExcelProperty("课程节次编号")
    private Long timeSlotId;

    @Schema(description = "日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("日期")
    private LocalDate date;

}