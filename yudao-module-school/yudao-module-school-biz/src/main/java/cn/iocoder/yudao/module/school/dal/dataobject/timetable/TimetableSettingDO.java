package cn.iocoder.yudao.module.school.dal.dataobject.timetable;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.type.JsonIntegerSetTypeHandler;
import cn.iocoder.yudao.framework.mybatis.core.type.JsonLongSetTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Set;

/**
 * 排课计划设置 DO
 *
 * @author yangzy
 */
@TableName(value = "school_timetable_setting", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableSettingDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 排课计划外键
     */
    private Long timetableId;
    /**
     * 班级
     */
    private Long gradeId;
    /**
     * 科目
     */
    private Long subjectId;

    /**
     * 课程类型
     */
    private Long courseTypeId;
    /**
     * 教师
     */
    private Long teacherId;
    /**
     * 每周课时数
     */
    private Integer count;
}