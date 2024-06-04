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

/**
 * @author yangzy
 */
@Schema(description = "管理后台 - 补课规则 Page Response VO")
@Data
@ExcelIgnoreUnannotated
public class FillRulePageRespVO {
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "7729")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "班级ID")
    @ExcelProperty("班级ID")
    private List<GradeRespVO> gradeList;

    @Schema(description = "补课日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("补课日期")
    private LocalDate date;

    @Schema(description = "补周几的课", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("补周几的课")
    private Integer week;

    @Schema(description = "开始补课节次", example = "1285")
    @ExcelProperty("开始补课节次")
    private TimeSlotRespVO startTimeSlot;

    @Schema(description = "结束补课节次", example = "32210")
    @ExcelProperty("结束补课节次")
    private TimeSlotRespVO endTimeSlot;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;


}
