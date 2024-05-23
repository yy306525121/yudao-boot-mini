package cn.iocoder.yudao.module.school.controller.admin.suject.vo;

import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 科目信息新增/修改 Request VO")
@Data
public class SubjectSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "22401")
    private Long id;

    @Schema(description = "科目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "科目名称不能为空")
    @DiffLogField(name = "科目名称")
    private String name;

    @Schema(description = "显示排序")
    @DiffLogField(name = "显示排序")
    private Integer sort;

}