package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 课程节次列表 Request VO")
@Data
@ToString(callSuper = true)
public class TimeSlotListReqVO {

    @Schema(description = "课程节次")
    private Integer sort;
}