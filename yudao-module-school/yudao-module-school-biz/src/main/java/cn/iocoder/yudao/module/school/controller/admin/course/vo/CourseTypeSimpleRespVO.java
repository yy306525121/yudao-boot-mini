package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author yangzy
 */
@Schema(description = "管理后台 - 课程类型 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CourseTypeSimpleRespVO {
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "4634")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "课程类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("课程类型")
    private String name;

    @Schema(description = "课程类型值", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("课程类型值")
    private Integer type;
}
