package cn.iocoder.yudao.module.school.controller.admin.suject.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 科目信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SubjectRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "22401")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "科目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("科目名称")
    private String name;

    @Schema(description = "排序")
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}