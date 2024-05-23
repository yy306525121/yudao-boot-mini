package cn.iocoder.yudao.module.school.controller.admin.classinfo.vo;

import cn.iocoder.yudao.module.school.framework.operatelog.core.ClassInfoParseFunction;
import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 班级新增/修改 Request VO")
@Data
public class ClassInfoSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2793")
    private Long id;

    @Schema(description = "班级名称", example = "张三")
    @DiffLogField(name = "班级名称")
    private String name;

    @Schema(description = "所属年级编号", example = "10019")
    @DiffLogField(name = "父级年级", function = ClassInfoParseFunction.NAME)
    private Long parentId;

    @Schema(description = "显示排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "显示排序不能为空")
    @DiffLogField(name = "显示排序")
    private Integer sort;

}