package cn.iocoder.yudao.module.school.controller.admin.teacher.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 教师信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TeacherSimpleRespVO {
    @Schema(description = "教师编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("教师编号")
    private Long id;

    @Schema(description = "教师姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("教师姓名")
    private String name;
}
