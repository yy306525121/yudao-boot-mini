package cn.iocoder.yudao.module.school.controller.admin.timetable.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 排课 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TimetableRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13896")
    private Long id;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "排课名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    private String name;

    @Schema(description = "排课状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    private Integer status;

    @Schema(description = "是否正在运行", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    private Boolean running;

}
