package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 课程类型分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CourseTypePageReqVO extends PageParam {

    @Schema(description = "课程类型", example = "早自习")
    private String name;

}