package cn.iocoder.yudao.module.school.enums.course;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 课程名称枚举
 */
@Getter
@AllArgsConstructor
public enum CourseTypeNameEnum {
    MORNING("早自习"),
    NORMAL("正课"),
    SELF("自习课"),
    EVENING("晚自习");

    private final String name;
}