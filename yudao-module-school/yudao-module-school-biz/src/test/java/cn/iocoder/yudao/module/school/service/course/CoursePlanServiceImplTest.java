package cn.iocoder.yudao.module.school.service.course;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CoursePlanListReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CoursePlanSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CoursePlanDO;
import cn.iocoder.yudao.module.school.dal.mysql.course.CoursePlanMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static cn.iocoder.yudao.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomLongId;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.COURSE_PLAN_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link CoursePlanServiceImpl} 的单元测试类
 *
 * @author yangzy
 */
@Import(CoursePlanServiceImpl.class)
public class CoursePlanServiceImplTest extends BaseDbUnitTest {

    @Resource
    private CoursePlanServiceImpl coursePlanService;

    @Resource
    private CoursePlanMapper coursePlanMapper;

    @Test
    public void testCreateCoursePlan_success() {
        // 准备参数
        CoursePlanSaveReqVO createReqVO = randomPojo(CoursePlanSaveReqVO.class).setId(null);

        // 调用
        Long coursePlanId = coursePlanService.createCoursePlan(createReqVO);
        // 断言
        assertNotNull(coursePlanId);
        // 校验记录的属性是否正确
        CoursePlanDO coursePlan = coursePlanMapper.selectById(coursePlanId);
        assertPojoEquals(createReqVO, coursePlan, "id");
    }

    @Test
    public void testUpdateCoursePlan_success() {
        // mock 数据
        CoursePlanDO dbCoursePlan = randomPojo(CoursePlanDO.class);
        coursePlanMapper.insert(dbCoursePlan);// @Sql: 先插入出一条存在的数据
        // 准备参数
        CoursePlanSaveReqVO updateReqVO = randomPojo(CoursePlanSaveReqVO.class, o -> {
            o.setId(dbCoursePlan.getId()); // 设置更新的 ID
        });

        // 调用
        coursePlanService.updateCoursePlan(updateReqVO);
        // 校验是否更新正确
        CoursePlanDO coursePlan = coursePlanMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, coursePlan);
    }

    @Test
    public void testUpdateCoursePlan_notExists() {
        // 准备参数
        CoursePlanSaveReqVO updateReqVO = randomPojo(CoursePlanSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> coursePlanService.updateCoursePlan(updateReqVO), COURSE_PLAN_NOT_EXISTS);
    }

    @Test
    public void testDeleteCoursePlan_success() {
        // mock 数据
        CoursePlanDO dbCoursePlan = randomPojo(CoursePlanDO.class);
        coursePlanMapper.insert(dbCoursePlan);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbCoursePlan.getId();

        // 调用
        coursePlanService.deleteCoursePlan(id);
       // 校验数据不存在了
       assertNull(coursePlanMapper.selectById(id));
    }

    @Test
    public void testDeleteCoursePlan_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> coursePlanService.deleteCoursePlan(id), COURSE_PLAN_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetCoursePlanPage() {
       // mock 数据
       CoursePlanDO dbCoursePlan = randomPojo(CoursePlanDO.class, o -> { // 等会查询到
           o.setGradeId(null);
           o.setTeacherId(null);
       });
       coursePlanMapper.insert(dbCoursePlan);
       // 测试 gradeId 不匹配
       coursePlanMapper.insert(cloneIgnoreId(dbCoursePlan, o -> o.setGradeId(null)));
       // 测试 teacherId 不匹配
       coursePlanMapper.insert(cloneIgnoreId(dbCoursePlan, o -> o.setTeacherId(null)));
       // 准备参数
       CoursePlanListReqVO reqVO = new CoursePlanListReqVO();
       reqVO.setGradeId(null);
       reqVO.setTeacherId(null);

       // 调用
       PageResult<CoursePlanDO> pageResult = coursePlanService.getCoursePlanPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbCoursePlan, pageResult.getList().get(0));
    }

}