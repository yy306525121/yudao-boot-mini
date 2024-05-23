package cn.iocoder.yudao.module.school.service.grade;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeListReqVO;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.mysql.grade.GradeMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.List;

import static cn.iocoder.yudao.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomLongId;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.GRADE_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link GradeServiceImpl} 的单元测试类
 *
 * @author yangzy
 */
@Import(GradeServiceImpl.class)
public class GradeServiceImplTest extends BaseDbUnitTest {

    @Resource
    private GradeServiceImpl gradeService;

    @Resource
    private GradeMapper gradeMapper;

    @Test
    public void testCreateGrade_success() {
        // 准备参数
        GradeSaveReqVO createReqVO = randomPojo(GradeSaveReqVO.class).setId(null);

        // 调用
        Long gradeId = gradeService.createGrade(createReqVO);
        // 断言
        assertNotNull(gradeId);
        // 校验记录的属性是否正确
        GradeDO grade = gradeMapper.selectById(gradeId);
        assertPojoEquals(createReqVO, grade, "id");
    }

    @Test
    public void testUpdateGrade_success() {
        // mock 数据
        GradeDO dbGrade = randomPojo(GradeDO.class);
        gradeMapper.insert(dbGrade);// @Sql: 先插入出一条存在的数据
        // 准备参数
        GradeSaveReqVO updateReqVO = randomPojo(GradeSaveReqVO.class, o -> {
            o.setId(dbGrade.getId()); // 设置更新的 ID
        });

        // 调用
        gradeService.updateGrade(updateReqVO);
        // 校验是否更新正确
        GradeDO grade = gradeMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, grade);
    }

    @Test
    public void testUpdateGrade_notExists() {
        // 准备参数
        GradeSaveReqVO updateReqVO = randomPojo(GradeSaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> gradeService.updateGrade(updateReqVO), GRADE_NOT_EXISTS);
    }

    @Test
    public void testDeleteGrade_success() {
        // mock 数据
        GradeDO dbGrade = randomPojo(GradeDO.class);
        gradeMapper.insert(dbGrade);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbGrade.getId();

        // 调用
        gradeService.deleteGrade(id);
        // 校验数据不存在了
        assertNull(gradeMapper.selectById(id));
    }

    @Test
    public void testDeleteGrade_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> gradeService.deleteGrade(id), GRADE_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetGradeList() {
        // mock 数据
        GradeDO dbGrade = randomPojo(GradeDO.class, o -> { // 等会查询到
            o.setName(null);
            o.setParentId(null);
        });
        gradeMapper.insert(dbGrade);
        // 测试 name 不匹配
        gradeMapper.insert(cloneIgnoreId(dbGrade, o -> o.setName(null)));
        // 测试 parentId 不匹配
        gradeMapper.insert(cloneIgnoreId(dbGrade, o -> o.setParentId(null)));
        // 准备参数
        GradeListReqVO reqVO = new GradeListReqVO();
        reqVO.setName(null);
        reqVO.setParentId(null);

        // 调用
        List<GradeDO> list = gradeService.getGradeList(reqVO);
        // 断言
        assertEquals(1, list.size());
        assertPojoEquals(dbGrade, list.get(0));
    }

}