package cn.iocoder.yudao.module.school.dal.dataobject.rule;

import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 放假时间规则 DO
 *
 * @author yangzy
 */
@TableName("school_holiday_rule")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HolidayRuleDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 班级ID的数组json
     */
    private String gradeId;
    /**
     * 开始日期
     */
    private LocalDate startDate;
    /**
     * 开始节次
     */
    private Long startTimeSlotId;
    /**
     * 结束日期
     */
    private LocalDate endDate;
    /**
     * 结束节次
     */
    private Long endTimeSlotId;

}