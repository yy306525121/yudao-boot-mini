package cn.iocoder.yudao.module.school.framework.operatelog.core;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.school.enums.course.TimeSlotTypeEnum;
import com.mzt.logapi.service.IParseFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 课程节次类型的 {@link IParseFunction} 实现类
 *
 * @author yangzy
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TimeSlotTypeParseFunction implements IParseFunction {
    public static final String NAME = "TimeSlotType";


    @Override
    public String functionName() {
        return NAME;
    }

    @Override
    public String apply(Object value) {
        if (StrUtil.isEmptyIfStr(value)) {
            return "";
        }

        return TimeSlotTypeEnum.getName(Convert.toInt(value));
    }
}
