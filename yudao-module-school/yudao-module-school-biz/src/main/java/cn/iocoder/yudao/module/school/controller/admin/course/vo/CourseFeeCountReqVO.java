package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Schema(description = "管理后台 - 课时费明细 Request VO")
@Data
@ToString(callSuper = true)
public class CourseFeeCountReqVO {
    @NotNull(message = "教师不能为空")
    private Long teacherId;

    @NotNull(message = "月份不能为空")
    private LocalDate date;
}
