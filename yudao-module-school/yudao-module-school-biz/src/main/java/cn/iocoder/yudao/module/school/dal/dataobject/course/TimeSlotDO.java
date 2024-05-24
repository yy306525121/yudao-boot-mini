package cn.iocoder.yudao.module.school.dal.dataobject.course;

import lombok.*;

import java.time.LocalTime;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 课程节次 DO
 *
 * @author yangzy
 */
@TableName("school_time_slot")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 节次类型
     *
     * 枚举 {@link TODO time_slot_type 对应的类}
     */
    private Integer type;
    /**
     * 课程节次
     */
    private Integer sort;
    /**
     * 开始时间
     */
    private LocalTime startTime;
    /**
     * 结束时间
     */
    private LocalTime endTime;

}