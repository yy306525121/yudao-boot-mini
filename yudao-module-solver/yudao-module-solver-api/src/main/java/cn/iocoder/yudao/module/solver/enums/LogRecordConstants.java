package cn.iocoder.yudao.module.solver.enums;

/**
 * @author yangzy
 */
public interface LogRecordConstants {

    // ======================= SCHOOL_TEACHER 教师 =======================
    String SOLVER_TIMETABLE_TYPE = "Solver 排课";
    String SOLVER_TIMETABLE_CREATE_SUB_TYPE = "创建排课计划";
    String SOLVER_TIMETABLE_CREATE_SUCCESS = "创建了排课计划【{{#timetable.name}}】";
    String SOLVER_TIMETABLE_UPDATE_SUB_TYPE = "更新排课计划";
    String SOLVER_TIMETABLE_UPDATE_SUCCESS = "更新了排课计划【{{#timetable.name}}】: {_DIFF{#reqVO}}";
    String SOLVER_TIMETABLE_DELETE_SUB_TYPE = "删除排课计划";
    String SOLVER_TIMETABLE_DELETE_SUCCESS = "删除了排课计划【{{#timetable.name}}】";
}
