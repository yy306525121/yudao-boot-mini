package cn.iocoder.yudao.module.school.enums;

/**
 * @author yangzy
 */
public interface LogRecordConstants {

    // ======================= SCHOOL_TEACHER 教师 =======================
    String SCHOOL_TEACHER_TYPE = "SCHOOL 教师";
    String SCHOOL_TEACHER_CREATE_SUB_TYPE = "创建教师";
    String SCHOOL_TEACHER_CREATE_SUCCESS = "创建了教师【{{#teacher.name}}】";
    String SCHOOL_TEACHER_UPDATE_SUB_TYPE = "更新教师";
    String SCHOOL_TEACHER_UPDATE_SUCCESS = "更新了教师【{{#teacher.name}}】: {_DIFF{#reqVO}}";
    String SCHOOL_TEACHER_DELETE_SUB_TYPE = "删除教师";
    String SCHOOL_TEACHER_DELETE_SUCCESS = "删除了教师【{{#teacher.name}}】";


    // ======================= SCHOOL_SUBJECT 科目 =======================
    String SCHOOL_SUBJECT_TYPE = "SCHOOL 科目";
    String SCHOOL_SUBJECT_CREATE_SUB_TYPE = "创建科目";
    String SCHOOL_SUBJECT_CREATE_SUCCESS = "创建了科目【{{#subject.name}}】";
    String SCHOOL_SUBJECT_UPDATE_SUB_TYPE = "更新科目";
    String SCHOOL_SUBJECT_UPDATE_SUCCESS = "更新了科目【{{#subject.name}}】: {_DIFF{#updateReqVO}}";
    String SCHOOL_SUBJECT_DELETE_SUB_TYPE = "删除科目";
    String SCHOOL_SUBJECT_DELETE_SUCCESS = "删除了科目【{{#subject.name}}】";


    // ======================= SCHOOL_GRADE 年级 =======================
    String SCHOOL_GRADE_TYPE = "SCHOOL 年级";
    String SCHOOL_GRADE_CREATE_SUB_TYPE = "创建年级";
    String SCHOOL_GRADE_CREATE_SUCCESS = "创建了年级【{{#grade.name}}】";
    String SCHOOL_GRADE_UPDATE_SUB_TYPE = "更新年级";
    String SCHOOL_GRADE_UPDATE_SUCCESS = "更新了年级【{{#grade.name}}】: {_DIFF{#updateReqVO}}";
    String SCHOOL_GRADE_DELETE_SUB_TYPE = "删除年级";
    String SCHOOL_GRADE_DELETE_SUCCESS = "删除了年级【{{#grade.name}}】";
}
