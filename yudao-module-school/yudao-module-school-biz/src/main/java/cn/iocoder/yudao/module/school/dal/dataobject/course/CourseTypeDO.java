package cn.iocoder.yudao.module.school.dal.dataobject.course;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 课程类型 DO
 *
 * @author yangzy
 */
@TableName("school_course_type")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseTypeDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 课程类型名称
     */
    private String name;
    /**
     * 课程类型值
     */
    private Integer type;
    /**
     * 每一节该类型的课按多少课时算
     */
    private BigDecimal num;

    private Integer sort;

}