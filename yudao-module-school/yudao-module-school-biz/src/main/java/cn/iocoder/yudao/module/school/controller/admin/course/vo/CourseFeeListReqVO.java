package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "管理后台 - 课时费查询 Request VO")
@Data
@ToString(callSuper = true)
public class CourseFeeListReqVO {
    private Long teacherId;

    @NotNull(message = "查询开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "查询结束日期不能为空")
    private LocalDate endDate;

    /**
     * 节次
     */
    private List<Long> timeSlotIdList;

    private List<Long> gradeIdList;
}
