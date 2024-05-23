package cn.iocoder.yudao.module.school.enums;

/**
 * @author yangzy
 */
public interface LogRecordConstants {

    // ======================= SCHOOL_TEACHER 教师 =======================
    String SCHOOL_TEACHER_TYPE = "SCHOOL 教师";
    String SCHOOL_TEACHER_CREATE_SUB_TYPE = "创建教师";
    String SCHOOL_TEACHER_CREATE_SUCCESS = "创建了用户【{{#teacher.name}}】";
    String SCHOOL_TEACHER_UPDATE_SUB_TYPE = "更新教师";
    String SCHOOL_TEACHER_UPDATE_SUCCESS = "更新了教师【{{#teacher.name}}】: {_DIFF{#reqVO}}";
    String SCHOOL_TEACHER_DELETE_SUB_TYPE = "删除教师";
    String SCHOOL_TEACHER_DELETE_SUCCESS = "删除了教师【{{#teacher.name}}】: {_DIFF{#reqVO}}";
}
