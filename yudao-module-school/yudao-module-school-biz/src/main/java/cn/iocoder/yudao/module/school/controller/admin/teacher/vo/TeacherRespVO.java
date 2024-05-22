package cn.iocoder.yudao.module.school.controller.admin.teacher.vo;

import cn.iocoder.yudao.framework.jackson.core.databind.BigDecimalSerializable;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 教师信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TeacherRespVO {
    @Schema(description = "教师编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("教师编号")
    private Long id;

    @Schema(description = "教师姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("教师姓名")
    private String name;

    /**
     * 0-在职
     * 1-离职
     */
    @Schema(description = "教师状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty("教师状态")
    private Integer status;

    @Schema(description = "教师显示排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty("教师显示排序")
    private Integer sort;

    /**
     * 基础工资
     */
    @Schema(description = "基础工资", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty("基础工资")
    @JsonSerialize(using = BigDecimalSerializable.class)
    private BigDecimal basicSalary;

    /**
     * 课时费单价
     */
    @Schema(description = "课时费单价", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty("课时费单价")
    @JsonSerialize(using = BigDecimalSerializable.class)
    private BigDecimal courseSalary;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
