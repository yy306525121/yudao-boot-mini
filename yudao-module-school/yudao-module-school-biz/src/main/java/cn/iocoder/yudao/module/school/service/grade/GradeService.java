package cn.iocoder.yudao.module.school.service.grade;

import cn.hutool.core.lang.tree.Tree;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeListReqVO;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 班级 Service 接口
 *
 * @author yangzy
 */
public interface GradeService {

    /**
     * 创建班级
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createGrade(@Valid GradeSaveReqVO createReqVO);

    /**
     * 更新班级
     *
     * @param updateReqVO 更新信息
     */
    void updateGrade(@Valid GradeSaveReqVO updateReqVO);

    /**
     * 删除班级
     *
     * @param id 编号
     */
    void deleteGrade(Long id);

    /**
     * 获得班级
     *
     * @param id 编号
     * @return 班级
     */
    GradeDO getGrade(Long id);

    /**
     * 获得班级列表
     *
     * @param listReqVO 查询条件
     * @return 班级列表
     */
    List<GradeDO> getGradeList(GradeListReqVO listReqVO);

    /**
     * 获取年级树
     * @return 年级树
     */
    List<Tree<Long>> gradeTree();

}