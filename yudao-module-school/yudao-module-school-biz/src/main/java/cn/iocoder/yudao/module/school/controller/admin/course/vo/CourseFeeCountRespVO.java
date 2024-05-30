package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 课时费明细 Response VO")
@Data
@ToString(callSuper = true)
public class CourseFeeCountRespVO {
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN)
    private LocalDate date;

    private BigDecimal count;

}
