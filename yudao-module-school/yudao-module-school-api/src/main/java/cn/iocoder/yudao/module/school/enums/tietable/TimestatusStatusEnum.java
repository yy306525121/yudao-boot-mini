package cn.iocoder.yudao.module.school.enums.tietable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 课程名称枚举
 */
@Getter
@AllArgsConstructor
public enum TimestatusStatusEnum {
    NOT_SCHEDULING(0, "未排课"),
    SCHEDULING(1, "正在排课"),
    SCHEDULING_SUCCESS(1, "排课完成");

    private final Integer status;

    private final String name;
}
