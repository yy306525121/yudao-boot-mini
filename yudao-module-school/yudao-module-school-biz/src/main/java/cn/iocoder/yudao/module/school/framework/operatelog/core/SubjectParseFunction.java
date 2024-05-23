package cn.iocoder.yudao.module.school.framework.operatelog.core;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.service.subject.SubjectService;
import com.mzt.logapi.service.IParseFunction;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 科目名字的 {@link IParseFunction} 实现类
 *
 * @author yangzy
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubjectParseFunction implements IParseFunction {
    public static final String NAME = "getSubjectById";

    private final SubjectService subjectService;

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
        SubjectDO subject = subjectService.getSubject(Convert.toLong(value));
        if (subject == null) {
            log.warn("[apply][获取科目{{}}为空", value);
            return "";
        }
        return subject.getName();
    }
}
