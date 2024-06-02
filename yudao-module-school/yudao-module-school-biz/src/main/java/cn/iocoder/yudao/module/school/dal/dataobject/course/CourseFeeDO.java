package cn.iocoder.yudao.module.school.dal.dataobject.course;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 课时费明细 DO
 *
 * @author yangzy
 */
@TableName("school_course_fee")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseFeeDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 课时费
     */
    private BigDecimal count;
    /**
     * 教师编号
     */
    private Long teacherId;
    /**
     * 班级编号
     */
    private Long gradeId;
    /**
     * 课程ID
     */
    private Long subjectId;
    /**
     * 星期
     */
    private Integer week;
    /**
     * 课程节次编号
     */
    private Long timeSlotId;
    /**
     * 日期
     */
    private LocalDate date;

    private String remark;
}