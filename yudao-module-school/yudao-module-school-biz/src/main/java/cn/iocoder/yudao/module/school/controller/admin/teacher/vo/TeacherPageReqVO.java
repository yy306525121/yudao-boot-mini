package cn.iocoder.yudao.module.school.controller.admin.teacher.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 教师分页 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TeacherPageReqVO extends PageParam {
    @Schema(description = "教师姓名，模糊匹配", example = "张三")
    private String name;

    /**
     * 0-在职
     * 1-离职
     */
    @Schema(description = "教师状态 0-在职,1-离职", example = "0", defaultValue = "0")
    private String status;
}
