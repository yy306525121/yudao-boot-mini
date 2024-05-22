package cn.iocoder.yudao.module.school.framework.operatelog.core;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.service.teacher.TeacherService;
import com.mzt.logapi.service.IParseFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 教师名字的 {@link IParseFunction} 实现类
 *
 * @author yangzy
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TeacherParseFunction implements IParseFunction {
    public static final String NAME = "getTeacherById";

    private final TeacherService teacherService;

    @Override
    public String functionName() {
        return NAME;
    }

    @Override
    public String apply(Object value) {
        if (StrUtil.isEmptyIfStr(value)) {
            return "";
        }

        // 获取岗位信息
        TeacherDO teacher = teacherService.getTeacher(Convert.toLong(value));
        if (teacher == null) {
            log.warn("[apply][获取科目{{}}为空", value);
            return "";
        }
        return teacher.getName();
    }
}
