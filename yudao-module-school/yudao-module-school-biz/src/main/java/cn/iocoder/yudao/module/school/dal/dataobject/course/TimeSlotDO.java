package cn.iocoder.yudao.module.school.dal.dataobject.course;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalTime;

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
    @PlanningId
    private Long id;
    /**
     * 节次类型
     *
     * 枚举 {@link cn.iocoder.yudao.module.school.enums.course.TimeSlotTypeEnum}
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