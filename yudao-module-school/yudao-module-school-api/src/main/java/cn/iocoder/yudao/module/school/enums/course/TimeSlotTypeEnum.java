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
public enum TimeSlotTypeEnum implements IntArrayValuable {
    MORNING(1, "早自习"),
    DAYTIME(2, "白天课程"),
    NIGHT_SELF(3, "晚自习");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(TimeSlotTypeEnum::getType).toArray();


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

    public static String getName(Integer type) {
        TimeSlotTypeEnum[] types = TimeSlotTypeEnum.values();
        for (TimeSlotTypeEnum enumType: types) {
            if (enumType.type.equals(type)) {
                return enumType.name;
            }
        }
        return null;
    }
}
