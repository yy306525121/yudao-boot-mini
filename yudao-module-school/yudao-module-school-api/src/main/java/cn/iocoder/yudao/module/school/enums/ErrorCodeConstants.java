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
    ErrorCode SUBJECT_NAME_NOT_EXISTS = new ErrorCode(100100102, "科目(name={})不存在");

    // ========== 教师 100200100 ==========
    ErrorCode TEACHER_NOT_EXISTS = new ErrorCode(100200100, "教师不存在");
    ErrorCode TEACHER_NAME_DUPLICATE = new ErrorCode(100200101, "教师姓名已存在");
    ErrorCode TEACHER_NAME_NOT_EXISTS = new ErrorCode(100200102, "教师(name={})不存在");
    ErrorCode TEACHER_SUBJECT_NOT_EXISTS = new ErrorCode(100200103, "教师(name={})科目(name={})不存在");


    // ========== 年级 100300100 ==========
    ErrorCode GRADE_NOT_EXISTS = new ErrorCode(100300100, "年级不存在");
    ErrorCode GRADE_NAME_NOT_EXISTS = new ErrorCode(100300110, "年级({})不存在,请检查");
    ErrorCode GRADE_EXITS_CHILDREN = new ErrorCode(100300101, "存在子年级，无法删除");
    ErrorCode GRADE_PARENT_NOT_EXITS = new ErrorCode(100300102,"父年级不存在");
    ErrorCode GRADE_PARENT_ERROR = new ErrorCode(100300103, "不能设置自己为父年级");
    ErrorCode GRADE_NAME_DUPLICATE = new ErrorCode(100300104, "年级名称已存在");
    ErrorCode GRADE_PARENT_IS_CHILD = new ErrorCode(100300105, "不能设置自己的子年级为父年级");


    // ========== 课程类型 100400100 ==========
    ErrorCode COURSE_TYPE_NOT_EXISTS = new ErrorCode(100400100, "课程类型不存在");
    ErrorCode COURSE_TYPE_NAME_DUPLICATE = new ErrorCode(100400101, "课程类型已存在");
    ErrorCode COURSE_TYPE_NAME_NOT_EXISTS = new ErrorCode(100400102, "课程类型(name={})不存在");

    // ========== 课程时段 100500100 ==========
    ErrorCode TIME_SLOT_NOT_EXISTS = new ErrorCode(100500100, "课程节次不存在");
    ErrorCode TIME_SLOT_SORT_DUPLICATE = new ErrorCode(100500104, "课程节次已存在");


    // ========== 课程计划 100600100 ==========
    ErrorCode COURSE_PLAN_NOT_EXISTS = new ErrorCode(100600100, "课程不存在");
    ErrorCode COURSE_PLAN_MORNING_NOT_EXISTS = new ErrorCode(100600200, "早自习课程科目未设置");
    ErrorCode COURSE_PLAN_IMPORT_TIME_SLOT_ERROR = new ErrorCode(100600201, "导入文件中sheet页({})中的第({})行节次信息错误,请检查");
    ErrorCode COURSE_PLAN_IMPORT_TIME_SLOT_SORT_NOT_EXISTS = new ErrorCode(100600202, "导入文件中sheet页({})中的课程节次(sort={})不存在,请检查");
    ErrorCode COURSE_PLAN_IMPORT_TIME_SLOT_WEEK_ERROR = new ErrorCode(100600203, "导入文件中sheet页({})中的第({})列的星期设置错误,请检查");
    ErrorCode COURSE_PLAN_CHANGE_TEACHER_SUBJECT_ERROR = new ErrorCode(100600204, "该教师未任课该门课程,无法调整,请检查");
    ErrorCode COURSE_PLAN_CHANGE_DATE_NOT_BETWEEN_CURRENT_COURSE_ERROR = new ErrorCode(100600205, "当前课程有效范围为:{}~{}, 调整日期超出当前课程的有效范围, 请修改课程查询日期并重新查询之后再修改");
    ErrorCode COURSE_PLAN_SUBJECT_NOT_EXISTS = new ErrorCode(100600206, "科目不能为空");
    ErrorCode COURSE_PLAN_TEACHER_NOT_EXISTS = new ErrorCode(100600206, "教师不能为空");

    // ========== 临时调课规则 100700100 ==========
    ErrorCode TRANSFER_RULE_NOT_EXISTS = new ErrorCode(100700100, "临时调课规则不存在");
}
