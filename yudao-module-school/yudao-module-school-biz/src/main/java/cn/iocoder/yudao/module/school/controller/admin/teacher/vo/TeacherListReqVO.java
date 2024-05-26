package cn.iocoder.yudao.module.school.controller.admin.teacher.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 教师列表 Request VO")
@Data
public class TeacherListReqVO {

    @Schema(description = "教师名称", example = "张三")
    private String name;

    @Schema(description = "教师状态", example = "1")
    private Integer status;

}