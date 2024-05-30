package cn.iocoder.yudao.module.school.dal.dataobject.rule;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * 临时调课 DO
 *
 * @author yangzy
 */
@TableName("school_transfer_rule")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRuleDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 调课时间
     */
    private LocalDate date;
    /**
     * 调课节次
     */
    private Long timeSlotId;
    /**
     * 调课教师ID
     */
    private Long fromTeacherId;
    /**
     * 调课教师ID
     */
    private Long toTeacherId;
    /**
     * 调课课程
     */
    private Long fromSubjectId;
    /**
     * 调课课程
     */
    private Long toSubjectId;
    /**
     * 调课课程类型
     */
    private Long fromCourseTypeId;
    /**
     * 调课课程类型
     */
    private Long toCourseTypeId;

}