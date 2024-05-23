package cn.iocoder.yudao.module.school.framework.operatelog.core;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.school.dal.dataobject.classinfo.ClassInfoDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.service.classinfo.ClassInfoService;
import cn.iocoder.yudao.module.school.service.subject.SubjectService;
import com.mzt.logapi.service.IParseFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 年级名字的 {@link IParseFunction} 实现类
 *
 * @author yangzy
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClassInfoParseFunction implements IParseFunction {
    public static final String NAME = "getClassInfoById";

    private final ClassInfoService classInfoService;

    @Override
    public String functionName() {
        return NAME;
    }

    @Override
    public String apply(Object value) {
        if (StrUtil.isEmptyIfStr(value)) {
            return "";
        }

        // 获取部门信息
        ClassInfoDO classInfo = classInfoService.getClassInfo(Convert.toLong(value));
        if (classInfo == null) {
            log.warn("[apply][获取年级{{}}为空", value);
            return "";
        }
        return classInfo.getName();
    }
}
