package cn.iocoder.yudao.module.school.framework.operatelog.core;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.service.grade.GradeService;
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
public class GradeParseFunction implements IParseFunction {
    public static final String NAME = "getGradeById";

    private final GradeService gradeService;

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
        GradeDO grade = gradeService.getGrade(Convert.toLong(value));
        if (grade == null) {
            log.warn("[apply][获取年级{{}}为空", value);
            return "";
        }
        return grade.getName();
    }
}
