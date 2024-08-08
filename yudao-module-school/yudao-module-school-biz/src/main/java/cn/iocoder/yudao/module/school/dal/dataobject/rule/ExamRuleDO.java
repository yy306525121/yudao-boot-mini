package cn.iocoder.yudao.module.school.dal.dataobject.rule;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.type.JsonLongSetTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

/**
 * 考试时间规则 DO
 *
 * @author yangzy
 */
@TableName(value = "school_exam_rule", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamRuleDO extends BaseDO {

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