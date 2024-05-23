package cn.iocoder.yudao.module.school.service.course;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseTypePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseTypeSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.mysql.course.CourseTypeMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static cn.iocoder.yudao.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomLongId;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.COURSE_TYPE_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link CourseTypeServiceImpl} 的单元测试类
 *
 * @author yangzy
 */
@Import(CourseTypeServiceImpl.class)
public class CourseTypeServiceImplTest extends BaseDbUnitTest {

    @Resource
    private CourseTypeServiceImpl courseTypeService;

    @Resource
    private CourseTypeMapper courseTypeMapper;

    @Test
    public void testCreateCourseType_success() {
        // 准备参数
        CourseTypeSaveReqVO createReqVO = randomPojo(CourseTypeSaveReqVO.class).setId(null);

        // 调用
        Long courseTypeId = courseTypeService.createCourseType(createReqVO);
        // 断言
        assertNotNull(courseTypeId);
        // 校验记录的属性是否正确
        CourseTypeDO courseType = courseTypeMapper.selectById(courseTypeId);
        assertPojoEquals(createReqVO, courseType, "id");
    }

    @Test
    public void testUpdateCourseType_success() {
        // mock 数据
        CourseTypeDO dbCourseType = randomPojo(CourseTypeDO.class);
        courseTypeMapper.insert(dbCourseType);// @Sql: 先插入出一条存在的数据
        // 准备参数
        CourseTypeSaveReqVO updateReqVO = randomPojo(CourseTypeSaveReqVO.class, o -> {
            o.setId(dbCourseType.getId()); // 设置更新的 ID
        });

        // 调用
        courseTypeService.updateCourseType(updateReqVO);
        // 校验是否更新正确
        CourseTypeDO courseType = courseTypeMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, courseType);
    }

    @Test
    public void testUpdateCourseType_notExists() {
        // 准备参数
        CourseTypeSaveReqVO updateReqVO = randomPojo(CourseTypeSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> courseTypeService.updateCourseType(updateReqVO), COURSE_TYPE_NOT_EXISTS);
    }

    @Test
    public void testDeleteCourseType_success() {
        // mock 数据
        CourseTypeDO dbCourseType = randomPojo(CourseTypeDO.class);
        courseTypeMapper.insert(dbCourseType);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbCourseType.getId();

        // 调用
        courseTypeService.deleteCourseType(id);
       // 校验数据不存在了
       assertNull(courseTypeMapper.selectById(id));
    }

    @Test
    public void testDeleteCourseType_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> courseTypeService.deleteCourseType(id), COURSE_TYPE_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetCourseTypePage() {
       // mock 数据
       CourseTypeDO dbCourseType = randomPojo(CourseTypeDO.class, o -> { // 等会查询到
           o.setName(null);
       });
       courseTypeMapper.insert(dbCourseType);
       // 测试 name 不匹配
       courseTypeMapper.insert(cloneIgnoreId(dbCourseType, o -> o.setName(null)));
       // 准备参数
       CourseTypePageReqVO reqVO = new CourseTypePageReqVO();
       reqVO.setName(null);

       // 调用
       PageResult<CourseTypeDO> pageResult = courseTypeService.getCourseTypePage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbCourseType, pageResult.getList().get(0));
    }

}