package cn.iocoder.yudao.module.school.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Infra 错误码枚举类
 *
 * infra 系统，使用 1-001-000-000 段
 */
public interface ErrorCodeConstants {
    // ========== 科目 100100100==========
    ErrorCode SUBJECT_NOT_EXISTS = new ErrorCode(100100100, "科目信息不存在");

    // ========== 教师 100200100==========
    ErrorCode TEACHER_NOT_EXISTS = new ErrorCode(100200100, "教师不存在");
}
