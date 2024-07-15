package cn.iocoder.yudao.module.school.service.course;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

@Import(CoursePlanServiceImpl.class)
public class LessonServiceImplTest extends BaseDbUnitTest {
    @Resource
    private CoursePlanOptService coursePlanOptService;

    @Test
    public void testGenerate() {
        coursePlanOptService.courseScheduling();
    }
}
