package cn.iocoder.yudao.module.school.enums.timetable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 课程名称枚举
 */
@Getter
@AllArgsConstructor
public enum TimetableStatusEnum {
    NO(0, "未排课"),
    YES(1, "已排课");

    private final Integer status;

    private final String name;
}
