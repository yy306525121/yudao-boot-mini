package cn.iocoder.yudao.module.school.controller.admin.rule.vo;

import cn.iocoder.yudao.module.school.controller.admin.course.vo.TimeSlotRespVO;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeRespVO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 考试规则 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ExamRulePageRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "5080")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "班级ID的数组json", example = "25025")
    @ExcelProperty("班级ID的数组json")
    private List<GradeRespVO> gradeList;

    @Schema(description = "开始日期")
    @ExcelProperty("开始日期")
    private LocalDate startDate;

    @Schema(description = "开始节次", example = "29761")
    @ExcelProperty("开始节次")
    private TimeSlotRespVO startTimeSlot;

    @Schema(description = "结束日期")
    @ExcelProperty("结束日期")
    private LocalDate endDate;

    @Schema(description = "结束节次", example = "15540")
    @ExcelProperty("结束节次")
    private TimeSlotRespVO endTimeSlot;

}