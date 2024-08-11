package cn.iocoder.yudao.module.school.controller.admin.timetable.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 排课分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TimetablePageReqVO extends PageParam {

    @Schema(description = "排课名称", example = "芋艿")
    private String name;

}