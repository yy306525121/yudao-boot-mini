package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import cn.iocoder.yudao.module.school.enums.DictTypeConstants;
import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import com.alibaba.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_TIME;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 课程节次 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TimeSlotRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1407")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "节次类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "节次类型", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.TIME_SLOT_TYPE)
    @DiffLogField(name = "节次类型")
    private Integer type;

    @Schema(description = "课程节次顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("课程节次顺序")
    @DiffLogField(name = "时段节次顺序")
    private Integer sort;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("开始时间")
    @DiffLogField(name = "开始时间")
    @DateTimeFormat(pattern = FORMAT_TIME)
    private LocalTime startTime;

    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("结束时间")
    @DiffLogField(name = "结束时间")
    @DateTimeFormat(pattern = FORMAT_TIME)
    private LocalTime endTime;

}