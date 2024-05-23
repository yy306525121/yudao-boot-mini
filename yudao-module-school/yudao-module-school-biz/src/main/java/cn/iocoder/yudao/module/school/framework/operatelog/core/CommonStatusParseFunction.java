package cn.iocoder.yudao.module.school.framework.operatelog.core;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import com.mzt.logapi.service.IParseFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 通用状态的 {@link IParseFunction} 实现类
 * @author yangzy
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonStatusParseFunction implements IParseFunction {
    public static final String NAME = "status";

    @Override
    public String functionName() {
        return NAME;
    }

    @Override
    public String apply(Object value) {
        if (StrUtil.isEmptyIfStr(value)) {
            return "";
        }


        if (CommonStatusEnum.isEnable(Convert.toInt(value))) {
            return CommonStatusEnum.ENABLE.getName();
        } else if (CommonStatusEnum.isDisable(Convert.toInt(value))) {
            return CommonStatusEnum.DISABLE.getName();
        } else {
            return "";
        }
    }
}
