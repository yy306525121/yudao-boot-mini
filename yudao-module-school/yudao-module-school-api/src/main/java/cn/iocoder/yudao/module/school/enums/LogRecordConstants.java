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

    // ======================= SCHOOL_COURSE_TYPE 课程类型 =======================
    String SCHOOL_COURSE_TYPE_TYPE = "SCHOOL 课程类型";
    String SCHOOL_COURSE_TYPE_CREATE_SUB_TYPE = "创建课程类型";
    String SCHOOL_COURSE_TYPE_CREATE_SUCCESS = "创建了课程类型【{{#courseType.name}}】";
    String SCHOOL_COURSE_TYPE_UPDATE_SUB_TYPE = "更新课程类型";
    String SCHOOL_COURSE_TYPE_UPDATE_SUCCESS = "更新了课程类型【{{#courseType.name}}】: {_DIFF{#updateReqVO}}";
    String SCHOOL_COURSE_TYPE_DELETE_SUB_TYPE = "删除课程类型";
    String SCHOOL_COURSE_TYPE_DELETE_SUCCESS = "删除了课程类型【{{#courseType.name}}】";


    // ======================= SCHOOL_TIME_SLOT 课程节次 =======================
    String SCHOOL_TIME_SLOT_TYPE = "SCHOOL 课程节次";
    String SCHOOL_TIME_SLOT_CREATE_SUB_TYPE = "创建课程节次";
    String SCHOOL_TIME_SLOT_CREATE_SUCCESS = "创建了课程节次【第{{#timeSlot.sort}}节】";
    String SCHOOL_TIME_SLOT_UPDATE_SUB_TYPE = "更新课程节次";
    String SCHOOL_TIME_SLOT_UPDATE_SUCCESS = "更新了课程节次【第{{#timeSlot.sort}}节】: {_DIFF{#updateReqVO}}";
    String SCHOOL_TIME_SLOT_DELETE_SUB_TYPE = "删除课程节次";
    String SCHOOL_TIME_SLOT_DELETE_SUCCESS = "删除了课程节次【第{{#timeSlot.sort}}节】";

    // ======================= SCHOOL_COURSE_PLAN 课程计划 =======================
    String SCHOOL_COURSE_PLAN_TYPE = "SCHOOL 课程节次";
    String SCHOOL_COURSE_PLAN_CHANGE_SUCCESS = "更新了课程计划【班级（{{#reqVO.gradeId}}）: {_DIFF{#newCoursePlan}}】";
}
