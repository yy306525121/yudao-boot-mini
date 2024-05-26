package cn.iocoder.yudao.module.school.service.course;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseTypePageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseTypeSaveReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.mysql.course.CourseTypeMapper;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.COURSE_TYPE_NAME_DUPLICATE;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.COURSE_TYPE_NOT_EXISTS;
import static cn.iocoder.yudao.module.school.enums.LogRecordConstants.*;

/**
 * 课程类型 Service 实现类
 *
 * @author yangzy
 */
@Service
@Validated
public class CourseTypeServiceImpl implements CourseTypeService {

    @Resource
    private CourseTypeMapper courseTypeMapper;

    @Override
    @LogRecord(type = SCHOOL_COURSE_TYPE_TYPE, subType = SCHOOL_COURSE_TYPE_CREATE_SUB_TYPE, bizNo = "{{#courseType.id}}", success = SCHOOL_COURSE_TYPE_CREATE_SUCCESS)
    public Long createCourseType(CourseTypeSaveReqVO createReqVO) {
        // 检查课程类型是否已存在
        validateCourseTypeNameUnique(null, createReqVO.getName());

        // 插入
        CourseTypeDO courseType = BeanUtils.toBean(createReqVO, CourseTypeDO.class);
        courseTypeMapper.insert(courseType);

        LogRecordContext.putVariable("courseType", courseType);
        // 返回
        return courseType.getId();
    }

    @Override
    @LogRecord(type = SCHOOL_COURSE_TYPE_TYPE, subType = SCHOOL_COURSE_TYPE_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}", success = SCHOOL_COURSE_TYPE_UPDATE_SUCCESS)
    public void updateCourseType(CourseTypeSaveReqVO updateReqVO) {
        // 校验存在
        CourseTypeDO oldCourseType = validateCourseTypeExists(updateReqVO.getId());
        // 校验课程唯一性
        validateCourseTypeNameUnique(updateReqVO.getId(), updateReqVO.getName());
        // 更新
        CourseTypeDO courseType = BeanUtils.toBean(updateReqVO, CourseTypeDO.class);
        courseTypeMapper.updateById(courseType);

        //记录日志
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldCourseType, CourseTypeSaveReqVO.class));
        LogRecordContext.putVariable("courseType", courseType);
    }

    @Override
    @LogRecord(type = SCHOOL_COURSE_TYPE_TYPE, subType = SCHOOL_COURSE_TYPE_DELETE_SUB_TYPE, bizNo = "{{#id}}", success = SCHOOL_COURSE_TYPE_DELETE_SUCCESS)
    public void deleteCourseType(Long id) {
        // 校验存在
        CourseTypeDO courseType = validateCourseTypeExists(id);
        // 删除
        courseTypeMapper.deleteById(id);

        LogRecordContext.putVariable("courseType", courseType);
    }

    private CourseTypeDO validateCourseTypeExists(Long id) {
        if (id == null) {
            return null;
        }
        CourseTypeDO courseType = courseTypeMapper.selectById(id);
        if (courseType == null) {
            throw exception(COURSE_TYPE_NOT_EXISTS);
        }

        return courseType;
    }

    private void validateCourseTypeNameUnique(@Nullable Long id, String name) {
        CourseTypeDO courseType = courseTypeMapper.selectByName(name);

        if (courseType == null) {
            return;
        }

        if (id == null) {
            throw exception(COURSE_TYPE_NAME_DUPLICATE);
        } else {
            if (!courseTypeMapper.selectById(id).getId().equals(courseType.getId())) {
                throw exception(COURSE_TYPE_NAME_DUPLICATE);
            }
        }
    }

    @Override
    public CourseTypeDO getCourseType(Long id) {
        return courseTypeMapper.selectById(id);
    }

    @Override
    public PageResult<CourseTypeDO> getCourseTypePage(CourseTypePageReqVO pageReqVO) {
        return courseTypeMapper.selectPage(pageReqVO);
    }

    @Override
    public CourseTypeDO getCourseType(String name) {
        return courseTypeMapper.selectByName(name);
    }

    @Override
    public List<CourseTypeDO> getAll() {
        return courseTypeMapper.selectList();
    }

}