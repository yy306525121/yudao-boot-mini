package cn.iocoder.yudao.module.school.controller.admin.suject.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 岗位信息的精简 Response VO")
@Data
public class SubjectSimpleRespVO {
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "22401")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "科目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("科目名称")
    private String name;
}
