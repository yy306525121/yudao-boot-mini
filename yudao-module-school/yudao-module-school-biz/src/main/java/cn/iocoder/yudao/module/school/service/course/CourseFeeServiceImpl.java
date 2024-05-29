package cn.iocoder.yudao.module.school.service.course;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseFeeListReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseFeeSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import cn.iocoder.yudao.module.school.dal.mysql.course.CourseFeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

/**
 * 课时费明细 Service 实现类
 *
 * @author yangzy
 */
@Service
@Validated
@RequiredArgsConstructor
public class CourseFeeServiceImpl implements CourseFeeService {

    private final CourseFeeMapper courseFeeMapper;

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

    @Override
    public List<CourseFeeDO> getCourseFeeList(CourseFeeListReqVO listReqVO) {
        return courseFeeMapper.selectList(listReqVO);
    }

    @Override
    public void removeCourseFee(Long teacherId, LocalDate start, LocalDate end) {
        courseFeeMapper.delete(new LambdaQueryWrapperX<CourseFeeDO>()
                .eqIfPresent(CourseFeeDO::getTeacherId, teacherId)
                .between(CourseFeeDO::getDate, start, end)
        );
    }

}