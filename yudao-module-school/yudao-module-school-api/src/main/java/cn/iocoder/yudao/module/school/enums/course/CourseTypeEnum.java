package cn.iocoder.yudao.module.school.enums.course;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 课程名称枚举
 */
@Getter
@AllArgsConstructor
public enum CourseTypeEnum {
    MORNING(1,"早自习"),
    NORMAL(2, "正课"),
    SELF(3, "自习课"),
    EVENING(4, "晚自习");

    private final Integer type;
    private final String name;
}