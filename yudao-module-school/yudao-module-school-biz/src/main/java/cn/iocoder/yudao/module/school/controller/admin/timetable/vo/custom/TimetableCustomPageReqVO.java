package cn.iocoder.yudao.module.school.controller.admin.timetable.vo.custom;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 排课定制分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TimetableCustomPageReqVO extends PageParam {

    @Schema(description = "排课计划编号", example = "26657")
    private Long timetableId;

    @Schema(description = "是否固定")
    private Boolean fixed;

    @Schema(description = "权重")
    private Long weight;

    @Schema(description = "星期")
    private Integer week;

    @Schema(description = "年级", example = "20859")
    private Long gradeId;

    @Schema(description = "课程类型", example = "4580")
    private Long courseTypeId;

    @Schema(description = "科目", example = "16563")
    private Long subjectId;

    @Schema(description = "教师", example = "25221")
    private Long teacherId;

    @Schema(description = "课程节次", example = "8428")
    private Long timeSlotId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}