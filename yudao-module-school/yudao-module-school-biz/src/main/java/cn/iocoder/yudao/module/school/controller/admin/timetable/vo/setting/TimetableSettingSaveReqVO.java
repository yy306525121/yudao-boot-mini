package cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting;

import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Schema(description = "管理后台 - 排课计划设置新增/修改 Request VO")
@Data
public class TimetableSettingSaveReqVO {
    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13896")
    private Long id;

    @Schema(description = "排课计划外键", requiredMode = Schema.RequiredMode.REQUIRED, example = "32278")
    @NotNull(message = "排课计划外键不能为空")
    private Long timetableId;

    @Schema(description = "班级", example = "18382")
    @NotNull(message = "班级不能为空")
    private Long gradeId;

    @Schema(description = "科目", example = "914")
    @NotNull(message = "科目不能为空")
    private Long subjectId;

    @Schema(description = "课程类型", example = "914")
    @NotNull(message = "课程类型不能为空")
    private Long courseTypeId;

    @Schema(description = "教师", example = "22321")
    @NotNull(message = "教师不能为空")
    @DiffLogField(name = "教师")
    private Long teacherId;

    @Schema(description = "普通课时数")
    @NotNull(message = "普通课时数")
    @DiffLogField(name = "普通课时数")
    private Integer ordinaryCount;

    @Schema(description = "连堂课次数")
    @DiffLogField(name = "连堂课次数")
    private Integer continuousCount;


    @Schema(description = "偏好星期")
    @DiffLogField(name = "偏好星期")
    private Set<Integer> preferWeeks;

    @Schema(description = "偏好节次")
    private Set<Long> preferTimeSlotIds;
}
