package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.module.school.enums.DictTypeConstants;
import cn.iocoder.yudao.module.school.framework.operatelog.core.TimeSlotTypeParseFunction;
import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Schema(description = "管理后台 - 课程节次新增/修改 Request VO")
@Data
public class TimeSlotSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1407")
    private Long id;

    @Schema(description = "时段类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "时段类型")
    @DictFormat(DictTypeConstants.TIME_SLOT_TYPE)
    @DiffLogField(name = "节次类型", function = TimeSlotTypeParseFunction.NAME)
    private Integer type;

    @Schema(description = "课程节次", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "课程节次不能为空")
    @DiffLogField(name = "时段节次顺序")
    private Integer sort;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "开始时间不能为空")
    @DiffLogField(name = "开始时间")
    private LocalTime startTime;

    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "结束时间不能为空")
    @DiffLogField(name = "结束时间")
    private LocalTime endTime;

}