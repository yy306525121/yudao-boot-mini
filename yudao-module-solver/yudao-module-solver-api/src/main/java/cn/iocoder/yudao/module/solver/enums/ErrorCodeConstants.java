package cn.iocoder.yudao.module.solver.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Infra 错误码枚举类
 *
 * infra 系统，使用 1-002-000-000 段
 */
public interface ErrorCodeConstants {
    // ========== 排课 TODO 补充编号 ==========
    ErrorCode TIMETABLE_NOT_EXISTS = new ErrorCode(100200100, "排课不存在");
    ErrorCode TIMETABLE_NAME_DUPLICATE = new ErrorCode(100200101, "排课名称已存在");
}
