package cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 排课计划设置分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TimetableSettingPageReqVO extends PageParam {

    @Schema(description = "排课计划外键", example = "32278")
    private Long timetableId;

    @Schema(description = "班级", example = "18382")
    private Long gradeId;

    @Schema(description = "科目", example = "914")
    private Long subjectId;

    @Schema(description = "教师", example = "22321")
    private Long teacherId;

    @Schema(description = "每周几节课")
    private Integer countEveryWeek;

}
