package cn.iocoder.yudao.module.school.dal.dataobject.course;

import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

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

}