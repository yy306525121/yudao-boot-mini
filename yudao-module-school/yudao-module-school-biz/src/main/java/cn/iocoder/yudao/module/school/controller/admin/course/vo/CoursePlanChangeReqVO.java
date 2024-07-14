package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Schema(description = "管理后台 - 课程计划调整 Request VO")
@Data
@ToString(callSuper = true)
public class CoursePlanChangeReqVO {
    @NotNull(message = "课程编号不能为空")
    private Long id;

    @NotNull(message = "班级不能为空")
    private Long gradeId;

    @NotNull(message = "开始调整日期不能为空")
    private LocalDate date;

    @NotNull(message = "周不能为空")
    private Integer week;

    @NotNull(message = "课程节次不能为空")
    private Long timeSlotId;

    /**
     * 原有教师
     */
    @NotNull(message = "原有教师不能为空")
    private Long fromTeacherId;

    /**
     * 替换教师
     */
    private Long toTeacherId;

    /**
     * 原有课程，为空时表示课程不变
     */
    private Long fromSubjectId;

    /**
     * 替换课程
     */
    private Long toSubjectId;

    /**
     * 原有课程类型， 为空时表示不替换
     */
    private Long fromCourseTypeId;

    /**
     * 替换课程类型
     */
    private Long toCourseTypeId;
}
