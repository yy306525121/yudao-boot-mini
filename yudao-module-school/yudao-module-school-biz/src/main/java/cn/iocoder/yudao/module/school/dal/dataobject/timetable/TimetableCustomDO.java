package cn.iocoder.yudao.module.school.dal.dataobject.timetable;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("school_timetable_custom")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableCustomDO extends BaseDO {

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
     * 是否固定
     */
    private Boolean fixed;
    /**
     * 权重
     */
    private Long weight;
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
