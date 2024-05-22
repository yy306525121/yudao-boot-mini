package cn.iocoder.yudao.module.school.controller.admin.teacher.vo;

import cn.iocoder.yudao.module.school.framework.operatelog.core.TeacherParseFunction;
import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

/**
 * @author yangzy
 */
@Schema(description = "管理后台 - 教师创建/修改 Request VO")
@Data
public class TeacherSaveReqVO {
    @Schema(description = "教师编号", example = "1024")
    private Long id;

    @Schema(description = "教师姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "教师姓名不能为空")
    @Size(max = 10, message = "教师姓名长度不能超过10个字符")
    private String name;

    @Schema(description = "教师状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "教师在职状态不能为空")
    private String status;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    private BigDecimal basicSalary;

    @Schema(description = "授课科目zhujian")
    @NotEmpty(message = "授课科目不能为空")
    @DiffLogField(name = "岗位", function = TeacherParseFunction.NAME)
    private Set<Long> subjectIds;
}
