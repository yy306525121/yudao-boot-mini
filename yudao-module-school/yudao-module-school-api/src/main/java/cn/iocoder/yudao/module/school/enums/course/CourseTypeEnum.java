package cn.iocoder.yudao.module.school.enums.course;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 课程类型枚举
 */
@Getter
@AllArgsConstructor
public enum CourseTypeEnum implements IntArrayValuable {
    MORNING(1, "早自习"),
    NORMAL(2, "正课"),
    STUDY_SELF(3, "自习"),
    NIGHT_SELF(4, "晚自习");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(CourseTypeEnum::getType).toArray();


    /**
     * 类型值
     */
    private final Integer type;
    /**
     * 状态名
     */
    private final String name;


    @Override
    public int[] array() {
        return ARRAYS;
    }
}
