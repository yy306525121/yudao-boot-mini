package cn.iocoder.yudao.module.school.controller.admin.classinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 班级列表 Request VO")
@Data
public class ClassInfoListReqVO {

    @Schema(description = "班级名称", example = "张三")
    private String name;

}