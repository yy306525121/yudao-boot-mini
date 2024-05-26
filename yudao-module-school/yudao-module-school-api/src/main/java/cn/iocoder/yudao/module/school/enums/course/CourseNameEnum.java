package cn.iocoder.yudao.module.school.enums.course;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 课程名称枚举
 */
@Getter
@AllArgsConstructor
public enum CourseNameEnum {
    ENGLISH("英语"),
    CHINESE("语文");

    private final String name;
}
