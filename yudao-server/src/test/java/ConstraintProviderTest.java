//import ai.timefold.solver.test.api.score.stream.ConstraintVerifier;
//import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
//import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
//import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
//import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
//import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
//import cn.iocoder.yudao.module.school.enums.course.CourseTypeEnum;
//import cn.iocoder.yudao.module.school.timefold.contraint.TimeTableConstraintProvider;
//import cn.iocoder.yudao.module.school.timefold.domain.Lesson;
//import cn.iocoder.yudao.module.school.timefold.domain.TimeTableProblem;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.security.core.parameters.P;
//
//import java.math.BigDecimal;
//import java.time.DayOfWeek;
//
//public class ConstraintProviderTest {
//
//    private ConstraintVerifier<TimeTableConstraintProvider, TimeTableProblem> constraintVerifier
//            = ConstraintVerifier.build(new TimeTableConstraintProvider(), TimeTableProblem.class, Lesson.class);
//
//    private GradeDO grade1 = null;
//    private GradeDO grade2 = null;
//    private TeacherDO teacher1 = null;
//    private TeacherDO teacher2 = null;
//    private TeacherDO teacher3 = null;
//    private TimeSlotDO timeSlotSort2 = null;
//    private TimeSlotDO timeSlotSort3 = null;
//    private TimeSlotDO timeSlotSort5 = null;
//    private TimeSlotDO timeSlotSort6 = null;
//    private TimeSlotDO timeSlotSort8 = null;
//    private TimeSlotDO timeSlotSort11 = null;
//    private TimeSlotDO timeSlotSort12 = null;
//    private SubjectDO subjectEnglish = null;
//    private SubjectDO subjectMath = null;
//    private SubjectDO subjectChinese = null;
//    private CourseTypeDO courseTypeNormal = null;
//    private CourseTypeDO courseTypeEvening = null;
//
//    @BeforeEach
//    void init() {
//
//        grade1 = new GradeDO(1L, "高一1班", 0L, 1);
//        grade2 = new GradeDO(2L, "高一2班", 0L, 1);
//
//        teacher1 = new TeacherDO(1L, "陈金怡", 1, null, null, null, null);
//        teacher2 = new TeacherDO(2L, "朱丽茹", 1, null, null, null, null);
//        teacher3 = new TeacherDO(3L, "周易馨", 1, null, null, null, null);
//
//        timeSlotSort2 = new TimeSlotDO(1L, CourseTypeEnum.NORMAL.getType(), 2, null, null);
//        timeSlotSort3 = new TimeSlotDO(2L, CourseTypeEnum.NORMAL.getType(), 3, null, null);
//        timeSlotSort5 = new TimeSlotDO(5L, CourseTypeEnum.NORMAL.getType(), 5, null, null);
//        timeSlotSort6 = new TimeSlotDO(6L, CourseTypeEnum.NORMAL.getType(), 6, null, null);
//        timeSlotSort8 = new TimeSlotDO(8L, CourseTypeEnum.NORMAL.getType(), 8, null, null);
//        timeSlotSort11 = new TimeSlotDO(11L, CourseTypeEnum.EVENING.getType(), 11, null, null);
//        timeSlotSort12 = new TimeSlotDO(12L, CourseTypeEnum.EVENING.getType(), 12, null, null);
//
//        subjectEnglish = new SubjectDO(1L, "英语", 1);
//        subjectMath = new SubjectDO(2L, "数学", 2);
//        subjectChinese = new SubjectDO(3L, "语文", 2);
//
//
//        courseTypeNormal = new CourseTypeDO(1L, "正课", CourseTypeEnum.NORMAL.getType(), new BigDecimal(1), 1);
//        courseTypeEvening = new CourseTypeDO(1L, "晚自习", CourseTypeEnum.EVENING.getType(), new BigDecimal(1), 1);
//
//    }
//
//
//    /**
//     * 一个班级最多同时只能有一节课
//     */
//    @Test
//    public void gradeConflictTest() {
//        Lesson firstLesson = new Lesson("1", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, false, "", DayOfWeek.MONDAY, timeSlotSort2);
//
//        Lesson conflictingLesson = new Lesson("2", subjectMath, teacher2, grade1, courseTypeNormal, null, null, false, "", DayOfWeek.MONDAY, timeSlotSort2);
//
//        Lesson noConflictingLesson = new Lesson("3", subjectChinese, teacher3, grade1, courseTypeNormal, null, null, false, "", DayOfWeek.MONDAY, timeSlotSort3);
//
//        constraintVerifier.verifyThat(TimeTableConstraintProvider::gradeConflict)
//                .given(firstLesson, conflictingLesson, noConflictingLesson)
//                .penalizesBy(1);
//    }
//
//    /**
//     * 一个教师同时只能上一节课
//     */
//    @Test
//    public void teacherConflictTest() {
//        Lesson firstLesson = new Lesson("1", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, false, "", DayOfWeek.MONDAY, timeSlotSort2);
//
//        Lesson conflictingLesson = new Lesson("2", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, false, "", DayOfWeek.MONDAY, timeSlotSort2);
//
//        Lesson noConflictingLesson = new Lesson("3", subjectEnglish, teacher1, grade2, courseTypeNormal, null, null, false, "", DayOfWeek.MONDAY, timeSlotSort3);
//
//        constraintVerifier.verifyThat(TimeTableConstraintProvider::teacherConflict)
//                .given(firstLesson, conflictingLesson, noConflictingLesson)
//                .penalizesBy(1);
//    }
//
//    /**
//     * 正课和自习课的节次为周1~6的每天的2~10节次
//     */
//    @Test
//    public void timeSlotNormalTimeSlotConflictTest() {
//        Lesson conflictingLesson1 = new Lesson("1", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, false, "", DayOfWeek.MONDAY, timeSlotSort11);
//
//        Lesson conflictingLesson2 = new Lesson("2", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, false, "", DayOfWeek.SUNDAY, timeSlotSort2);
//
//        Lesson noConflictingLesson = new Lesson("3", subjectEnglish, teacher1, grade2, courseTypeNormal, null, null, false, "", DayOfWeek.MONDAY, timeSlotSort3);
//
//        constraintVerifier.verifyThat(TimeTableConstraintProvider::timeSlotNormalTimeSlotConflict)
//                .given(conflictingLesson1, conflictingLesson2, noConflictingLesson)
//                .penalizesBy(2);
//    }
//
//    /**
//     * 夜自习节次为每天的11~12节次
//     */
//    @Test
//    public void timeSlotEveningConflictTest() {
//        Lesson conflictingLesson1 = new Lesson("1", subjectEnglish, teacher1, grade1, courseTypeEvening, null, null, false, "", DayOfWeek.MONDAY, timeSlotSort3);
//
//        Lesson conflictingLesson2 = new Lesson("2", subjectEnglish, teacher1, grade1, courseTypeEvening, null, null, false, "", DayOfWeek.SUNDAY, timeSlotSort2);
//
//        Lesson noConflictingLesson = new Lesson("3", subjectEnglish, teacher1, grade2, courseTypeEvening, null, null, false, "", DayOfWeek.MONDAY, timeSlotSort11);
//
//        constraintVerifier.verifyThat(TimeTableConstraintProvider::timeSlotEveningConflict)
//                .given(conflictingLesson1, conflictingLesson2, noConflictingLesson)
//                .penalizesBy(2);
//    }
//
//
//    /**
//     * 连堂课必须是同一天的相同科目的连续节次
//     */
//    @Test
//    public void continuousConflictTest() {
//        Lesson firstLesson = new Lesson("1", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, true, "1", DayOfWeek.MONDAY, timeSlotSort6);
//        Lesson secondLesson = new Lesson("2", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, true, "1", DayOfWeek.MONDAY, timeSlotSort8);
//
//        constraintVerifier.verifyThat(TimeTableConstraintProvider::continuousConflict)
//                .given(firstLesson, secondLesson)
//                .penalizesBy(0);
//    }
//
//    /**
//     * 相同科目相同年级的连堂课时和普通课时不能安排在同一天
//     */
//    @Test
//    public void ordinaryAndContinuousConflictTest() {
//        Lesson continuousLesson1 = new Lesson("1", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, true, "1", DayOfWeek.MONDAY, timeSlotSort3);
//        Lesson continuousLesson2 = new Lesson("2", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, true, "1", DayOfWeek.MONDAY, timeSlotSort2);
//        Lesson normalLesson = new Lesson("3", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, false, "", DayOfWeek.FRIDAY, timeSlotSort5);
//
//        constraintVerifier.verifyThat(TimeTableConstraintProvider::ordinaryAndContinuousConflict)
//                .given(continuousLesson1, continuousLesson2, normalLesson)
//                .penalizesBy(0);
//    }
//
//    @Test
//    public void continuousDayConflictTest() {
//        Lesson firstContinuousLesson1 = new Lesson("1", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, true, "1", DayOfWeek.MONDAY, timeSlotSort3);
//        Lesson firstContinuousLesson2 = new Lesson("2", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, true, "1", DayOfWeek.MONDAY, timeSlotSort2);
//        Lesson secondContinuousLesson1 = new Lesson("3", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, true, "2", DayOfWeek.MONDAY, timeSlotSort5);
//        Lesson secondContinuousLesson2 = new Lesson("4", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, true, "2", DayOfWeek.MONDAY, timeSlotSort6);
//
//        constraintVerifier.verifyThat(TimeTableConstraintProvider::continuousDayConflict)
//                .given(firstContinuousLesson1, firstContinuousLesson2, secondContinuousLesson1, secondContinuousLesson2)
//                .penalizesBy(4);
//    }
//
//    /**
//     * 同一个班级一天内不允许出现多节相同科目的普通课时
//     */
//    @Test
//    public void ordinaryConflictTest() {
//        Lesson firstLesson = new Lesson("1", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, false, "", DayOfWeek.MONDAY, timeSlotSort3);
//        Lesson conflictingLesson = new Lesson("2", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, false, "", DayOfWeek.MONDAY, timeSlotSort2);
//        Lesson noConflictingLesson = new Lesson("3", subjectEnglish, teacher1, grade1, courseTypeNormal, null, null, false, "", DayOfWeek.FRIDAY, timeSlotSort2);
//
//        constraintVerifier.verifyThat(TimeTableConstraintProvider::ordinaryConflict)
//                .given(firstLesson, conflictingLesson, noConflictingLesson)
//                .penalizesBy(1);
//    }
//
//}
