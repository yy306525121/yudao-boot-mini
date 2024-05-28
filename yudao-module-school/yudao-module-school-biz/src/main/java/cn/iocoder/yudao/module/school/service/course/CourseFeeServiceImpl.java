package cn.iocoder.yudao.module.school.service.course;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.*;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.school.dal.mysql.course.CourseFeeMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.*;

/**
 * 课时费明细 Service 实现类
 *
 * @author yangzy
 */
@Service
@Validated
public class CourseFeeServiceImpl implements CourseFeeService {

    @Resource
    private CourseFeeMapper courseFeeMapper;

    @Override
    public Long createCourseFee(CourseFeeSaveReqVO createReqVO) {
        // 插入
        CourseFeeDO courseFee = BeanUtils.toBean(createReqVO, CourseFeeDO.class);
        courseFeeMapper.insert(courseFee);
        // 返回
        return courseFee.getId();
    }

    @Override
    public CourseFeeDO getCourseFee(Long id) {
        return courseFeeMapper.selectById(id);
    }

}