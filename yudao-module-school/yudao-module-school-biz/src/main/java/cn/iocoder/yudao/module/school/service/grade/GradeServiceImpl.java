package cn.iocoder.yudao.module.school.service.grade;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeListReqVO;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.mysql.grade.GradeMapper;
import cn.iocoder.yudao.module.school.dal.redis.RedisKeyConstants;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.function.Function;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.school.enums.LogRecordConstants.*;

/**
 * 班级 Service 实现类
 *
 * @author yangzy
 */
@Service
@Validated
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {
    private final GradeMapper gradeMapper;

    @Override
    @CacheEvict(cacheNames = {RedisKeyConstants.GRADE_CHILDREN_ID_LIST, RedisKeyConstants.GRADE_TREE_LIST},
            allEntries = true) // allEntries 清空所有缓存，因为操作一个年级，涉及到多个缓存
    @LogRecord(type = SCHOOL_GRADE_TYPE, subType = SCHOOL_GRADE_CREATE_SUB_TYPE, bizNo = "{{#grade.id}}", success = SCHOOL_GRADE_CREATE_SUCCESS)
    public Long createGrade(GradeSaveReqVO createReqVO) {
        // 校验所属年级编号的有效性
        validateParentGrade(null, createReqVO.getParentId());

        // 校验班级名称的唯一性
        validateGradeNameUnique(null, createReqVO.getParentId(), createReqVO.getName());

        // 插入
        GradeDO grade = BeanUtils.toBean(createReqVO, GradeDO.class);
        gradeMapper.insert(grade);

        // 记录日志
        LogRecordContext.putVariable("grade", grade);

        // 返回
        return grade.getId();
    }

    @Override
    @CacheEvict(cacheNames = {RedisKeyConstants.GRADE_CHILDREN_ID_LIST, RedisKeyConstants.GRADE_TREE_LIST},
            allEntries = true) // allEntries 清空所有缓存，因为操作一个年级，涉及到多个缓存
    @LogRecord(type = SCHOOL_GRADE_TYPE, subType = SCHOOL_GRADE_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}", success = SCHOOL_GRADE_UPDATE_SUCCESS)
    public void updateGrade(GradeSaveReqVO updateReqVO) {
        // 校验存在
        GradeDO oldGrade = validateGradeExists(updateReqVO.getId());
        // 校验所属年级编号的有效性
        validateParentGrade(updateReqVO.getId(), updateReqVO.getParentId());
        // 校验班级名称的唯一性
        validateGradeNameUnique(updateReqVO.getId(), updateReqVO.getParentId(), updateReqVO.getName());

        // 更新
        GradeDO grade = BeanUtils.toBean(updateReqVO, GradeDO.class);
        gradeMapper.updateById(grade);

        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldGrade, GradeSaveReqVO.class));
        LogRecordContext.putVariable("grade", grade);
    }

    @Override
    @CacheEvict(cacheNames = {RedisKeyConstants.GRADE_CHILDREN_ID_LIST, RedisKeyConstants.GRADE_TREE_LIST},
            allEntries = true) // allEntries 清空所有缓存，因为操作一个年级，涉及到多个缓存
    @LogRecord(type = SCHOOL_GRADE_TYPE, subType = SCHOOL_GRADE_DELETE_SUB_TYPE, bizNo = "{{#id}}", success = SCHOOL_GRADE_DELETE_SUCCESS)
    public void deleteGrade(Long id) {
        // 校验存在
        GradeDO grade = validateGradeExists(id);
        // 校验是否有子班级
        if (gradeMapper.selectCountByParentId(id) > 0) {
            throw exception(GRADE_EXITS_CHILDREN);
        }
        // 删除
        gradeMapper.deleteById(id);

        LogRecordContext.putVariable("grade", grade);
    }

    private GradeDO validateGradeExists(Long id) {
        if (id == null) {
            return null;
        }
        GradeDO grade = gradeMapper.selectById(id);
        if (grade == null) {
            throw exception(GRADE_NOT_EXISTS);
        }

        return grade;
    }

    private void validateParentGrade(Long id, Long parentId) {
        if (parentId == null || GradeDO.PARENT_ID_ROOT.equals(parentId)) {
            return;
        }
        // 1. 不能设置自己为父班级
        if (Objects.equals(id, parentId)) {
            throw exception(GRADE_PARENT_ERROR);
        }
        // 2. 父班级不存在
        GradeDO parentGrade = gradeMapper.selectById(parentId);
        if (parentGrade == null) {
            throw exception(GRADE_PARENT_NOT_EXITS);
        }
        // 3. 递归校验父班级，如果父班级是自己的子班级，则报错，避免形成环路
        if (id == null) { // id 为空，说明新增，不需要考虑环路
            return;
        }
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            // 3.1 校验环路
            parentId = parentGrade.getParentId();
            if (Objects.equals(id, parentId)) {
                throw exception(GRADE_PARENT_IS_CHILD);
            }
            // 3.2 继续递归下一级父班级
            if (parentId == null || GradeDO.PARENT_ID_ROOT.equals(parentId)) {
                break;
            }
            parentGrade = gradeMapper.selectById(parentId);
            if (parentGrade == null) {
                break;
            }
        }
    }

    private void validateGradeNameUnique(Long id, Long parentId, String name) {
        GradeDO grade = gradeMapper.selectByParentIdAndName(parentId, name);
        if (grade == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的班级
        if (id == null) {
            throw exception(GRADE_NAME_DUPLICATE);
        }
        if (!Objects.equals(grade.getId(), id)) {
            throw exception(GRADE_NAME_DUPLICATE);
        }
    }
    @Override
    public GradeDO getGrade(Long id) {
        return gradeMapper.selectById(id);
    }

    @Override
    public List<GradeDO> getGradeList(GradeListReqVO listReqVO) {
        return gradeMapper.selectList(listReqVO);
    }

    @Override
    @Cacheable(cacheNames = RedisKeyConstants.GRADE_TREE_LIST)
    public List<Tree<Long>> gradeTree() {
        List<GradeDO> gradeList = gradeMapper.selectList();
        gradeList.sort(Comparator.comparing(GradeDO::getSort));

        //构建树形结构
        List<TreeNode<Long>> treeNodeList = gradeList.stream().map(getTreeNodeFunction()).toList();
        return TreeUtil.build(treeNodeList, GradeDO.PARENT_ID_ROOT);
    }

    @Override
    public GradeDO getGrade(String name) {
        return gradeMapper.selectByName(name);
    }

    @Override
    public List<GradeDO> getAll() {
        return gradeMapper.selectList();
    }

    private Function<GradeDO, TreeNode<Long>> getTreeNodeFunction() {
        return grade -> {
            TreeNode<Long> node = new TreeNode<>();
            node.setId(grade.getId());
            node.setName(grade.getName());
            node.setParentId(grade.getParentId());

            // 扩展属性
            Map<String, Object> extra = new HashMap<>();
            extra.put("sort", grade.getSort());
            node.setExtra(extra);
            return node;
        };
    }

}