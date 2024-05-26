package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Schema(description = "管理后台 - 课程计划查询 Request VO")
@Data
@ToString(callSuper = true)
public class CoursePlanListReqVO {

    @NotNull(message = "查询类型不能为空")
    private Integer queryType;

    @Schema(description = "年级", example = "8052")
    private Long gradeId;

    @Schema(description = "教师", example = "10231")
    private Long teacherId;

    @NotNull(message = "查询日期不能为空")
    private LocalDate date;

}