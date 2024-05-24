package cn.iocoder.yudao.module.school.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Infra 错误码枚举类
 *
 * infra 系统，使用 1-001-000-000 段
 */
public interface ErrorCodeConstants {
    // ========== 科目 100100100 ==========
    ErrorCode SUBJECT_NOT_EXISTS = new ErrorCode(100100100, "科目信息不存在");
    ErrorCode SUBJECT_NAME_DUPLICATE = new ErrorCode(100100101, "科目信息已存在");

    // ========== 教师 100200100 ==========
    ErrorCode TEACHER_NOT_EXISTS = new ErrorCode(100200100, "教师不存在");
    ErrorCode TEACHER_NAME_DUPLICATE = new ErrorCode(100200101, "教师姓名已存在");

    // ========== 班级 100300100 ==========
    ErrorCode GRADE_NOT_EXISTS = new ErrorCode(100300100, "班级不存在");
    ErrorCode GRADE_EXITS_CHILDREN = new ErrorCode(100300101, "存在子班级，无法删除");
    ErrorCode GRADE_PARENT_NOT_EXITS = new ErrorCode(100300102,"父级班级不存在");
    ErrorCode GRADE_PARENT_ERROR = new ErrorCode(100300103, "不能设置自己为父班级");
    ErrorCode GRADE_NAME_DUPLICATE = new ErrorCode(100300104, "班级名称已存在");
    ErrorCode GRADE_PARENT_IS_CHILD = new ErrorCode(100300105, "不能设置自己的子年级为父年级");


    // ========== 课程类型 100400100 ==========
    ErrorCode COURSE_TYPE_NOT_EXISTS = new ErrorCode(100400100, "课程类型不存在");
    ErrorCode COURSE_TYPE_NAME_DUPLICATE = new ErrorCode(100400101, "课程类型已存在");

    // ========== 课程时段 100500100 ==========
    ErrorCode TIME_SLOT_NOT_EXISTS = new ErrorCode(100500100, "课程节次不存在");
    ErrorCode TIME_SLOT_SORT_DUPLICATE = new ErrorCode(100500104, "课程节次已存在");
}
