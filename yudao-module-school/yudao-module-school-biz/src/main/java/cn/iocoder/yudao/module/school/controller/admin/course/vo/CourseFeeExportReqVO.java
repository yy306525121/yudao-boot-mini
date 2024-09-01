package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Schema(description = "管理后台 - 课时费明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CourseFeeExportReqVO extends PageParam {

    @Schema(description = "月份")
    @NotNull(message = "查询月份不能为空")
    private LocalDate date;
}