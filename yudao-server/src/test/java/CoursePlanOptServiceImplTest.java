import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.school.service.course.CoursePlanOptService;
import cn.iocoder.yudao.server.YudaoServerApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("local")
@SpringBootTest(classes = YudaoServerApplication.class)
public class CoursePlanOptServiceImplTest extends BaseDbUnitTest {
    @Resource
    private CoursePlanOptService coursePlanOptService;

    @Test
    public void testGenerate() {
        TenantContextHolder.setTenantId(1L);
        coursePlanOptService.courseScheduling();
    }
}
