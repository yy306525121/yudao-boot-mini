package cn.iocoder.yudao.module.school.dal.dataobject.course;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * 课程计划 DO
 *
 * @author yangzy
 */
@TableName("school_course_plan")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoursePlanDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
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
    /**
     * 课程生效日期
     */
    private LocalDate start;
    /**
     * 课程失效日期
     */
    private LocalDate end;



}