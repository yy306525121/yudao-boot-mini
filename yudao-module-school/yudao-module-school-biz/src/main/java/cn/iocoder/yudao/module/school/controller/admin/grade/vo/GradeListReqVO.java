package cn.iocoder.yudao.module.school.controller.admin.grade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 班级列表 Request VO")
@Data
public class GradeListReqVO {

    @Schema(description = "班级名称", example = "张三")
    private String name;

    @Schema(description = "所属年级编号", example = "30353")
    private Long parentId;

}