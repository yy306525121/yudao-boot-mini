package cn.iocoder.yudao.module.school.service.classinfo;

import java.util.*;

import jakarta.validation.*;
import cn.iocoder.yudao.module.school.controller.admin.classinfo.vo.*;
import cn.iocoder.yudao.module.school.dal.dataobject.classinfo.ClassInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 班级 Service 接口
 *
 * @author yangzy
 */
public interface ClassInfoService {

    /**
     * 创建班级
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createClassInfo(@Valid ClassInfoSaveReqVO createReqVO);

    /**
     * 更新班级
     *
     * @param updateReqVO 更新信息
     */
    void updateClassInfo(@Valid ClassInfoSaveReqVO updateReqVO);

    /**
     * 删除班级
     *
     * @param id 编号
     */
    void deleteClassInfo(Long id);

    /**
     * 获得班级
     *
     * @param id 编号
     * @return 班级
     */
    ClassInfoDO getClassInfo(Long id);

    /**
     * 获得班级列表
     *
     * @param listReqVO 查询条件
     * @return 班级列表
     */
    List<ClassInfoDO> getClassInfoList(ClassInfoListReqVO listReqVO);

}