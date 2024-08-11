package cn.iocoder.yudao.module.school.dal.dataobject.timetable;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 排课结果 DO
 *
 * @author yangzy
 */
@TableName("school_timetable_result")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableResultDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 排课计划编号
     */
    private Long timetableId;
    /**
     * 星期
     */
    private Integer week;
    /**
     * 年级
     */
    private Long gradeId;
    /**
     * 课程类型
     */
    private Long courseTypeId;
    /**
     * 科目
     */
    private Long subjectId;
    /**
     * 教师
     */
    private Long teacherId;
    /**
     * 课程节次
     */
    private Long timeSlotId;
}
