package cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Schema(description = "管理后台 - 排课计划设置 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TimetableSettingRespVO {
    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "14252")
    private Long id;

    @Schema(description = "排课计划外键", requiredMode = Schema.RequiredMode.REQUIRED, example = "14252")
    private Long timetableId;

    @Schema(description = "班级", example = "18382")
    private Long gradeId;

    @Schema(description = "科目", example = "914")
    private Long subjectId;

    @Schema(description = "教师", example = "22321")
    private Long teacherId;

    @Schema(description = "课程类型", example = "22321")
    private Long courseTypeId;

    @Schema(description = "每周几节课")
    private Integer countEveryWeek;

    @Schema(description = "偏好星期")
    private Set<Integer> preferWeeks;

    @Schema(description = "偏好节次")
    private Set<Long> preferTimeSlotIds;
}
