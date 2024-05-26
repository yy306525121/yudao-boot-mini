package cn.iocoder.yudao.module.school.enums.course;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoursePlanQueryTypeEnum {
    GRADE(1, "年级"),
    TEACHER(2, "教师");

    /**
     * 类型值
     */
    private final Integer type;
    /**
     * 状态名
     */
    private final String name;
}
