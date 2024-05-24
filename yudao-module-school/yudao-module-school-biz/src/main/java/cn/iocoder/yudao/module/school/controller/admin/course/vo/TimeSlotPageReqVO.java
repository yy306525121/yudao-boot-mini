package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

@Schema(description = "管理后台 - 课程节次分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TimeSlotPageReqVO extends PageParam {

    @Schema(description = "课程节次")
    private Integer sort;

}