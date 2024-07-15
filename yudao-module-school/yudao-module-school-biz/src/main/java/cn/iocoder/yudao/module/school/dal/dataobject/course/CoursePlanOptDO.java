package cn.iocoder.yudao.module.school.dal.dataobject.course;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 排课安排表
 */
@TableName("school_course_plan_opt")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoursePlanOptDO extends BaseDO {
    @TableId
    private Long id;

    private Long gradeId;
    private Long subjectId;
    private Long teacherId;
    /**
     * 每周几节课
     */
    private Integer countEveryWeek;
}
