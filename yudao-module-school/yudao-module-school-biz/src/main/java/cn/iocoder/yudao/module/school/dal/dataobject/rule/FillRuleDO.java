package cn.iocoder.yudao.module.school.dal.dataobject.rule;

import cn.iocoder.yudao.framework.mybatis.core.type.JsonLongSetTypeHandler;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 补课规则 DO
 *
 * @author yangzy
 */
@TableName(value = "school_fill_rule", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FillRuleDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 班级ID
     */
    @TableField(typeHandler = JsonLongSetTypeHandler.class)
    private Set<Long> gradeIds;
    /**
     * 补课日期
     */
    private LocalDate date;
    /**
     * 补周几的课
     */
    private Integer week;
    /**
     * 开始补课节次
     */
    private Long startTimeSlotId;
    /**
     * 结束补课节次
     */
    private Long endTimeSlotId;

}