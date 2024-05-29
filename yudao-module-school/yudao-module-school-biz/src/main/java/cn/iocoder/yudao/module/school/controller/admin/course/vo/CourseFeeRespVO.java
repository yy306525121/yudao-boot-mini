package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherRespVO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 课时费明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CourseFeeRespVO {
    @Schema(description = "课时费", example = "7505")
    @ExcelProperty("课时费")
    private BigDecimal count;

    @Schema(description = "日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private TeacherRespVO teacher;

}