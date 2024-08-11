package cn.iocoder.yudao.module.school.controller.admin.timetable.vo.result;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 排课结果查询 Request VO")
@Data
@ToString(callSuper = true)
public class TimetableResultListReqVO {

    @NotNull(message = "排课编号不能为空")
    private Long timetableId;

    @NotNull(message = "查询类型不能为空")
    private Integer queryType;

    @Schema(description = "年级", example = "8052")
    private Long gradeId;

    @Schema(description = "教师", example = "10231")
    private Long teacherId;

}