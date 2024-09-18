package cn.iocoder.yudao.module.school.framework.timetable.core.utils;

import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.enums.timetable.SubjectEnum;
import cn.iocoder.yudao.module.school.timetable.domain.Lesson;
import com.google.ortools.sat.*;
import lombok.experimental.UtilityClass;
import org.checkerframework.common.value.qual.IntVal;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author yangzy
 */
@UtilityClass
public class TimetableConstraintUtils {
    /**
     * 每个班级的科目只能是指定的教师
     *
     * @param model          模型
     * @param x              变量
     * @param lessonList     课程列表
     * @param dayPerWeek     每周上几天课
     * @param timeSlotPerDay 每天有几节课
     */
    public void gradeTeacherUniqueConstraint(CpModel model, Literal[][][][][] x, List<Lesson> lessonList, int dayPerWeek, int timeSlotPerDay) {
        List<GradeDO> gradeList = lessonList.stream().map(Lesson::getGrade).distinct().toList();
        List<SubjectDO> subjectList = lessonList.stream().map(Lesson::getSubject).distinct().toList();
        List<TeacherDO> teacherList = lessonList.stream().map(Lesson::getTeacher).distinct().toList();

        for (int gradeIndex = 0; gradeIndex < gradeList.size(); gradeIndex++) {
            for (int subjectIndex = 0; subjectIndex < subjectList.size(); subjectIndex++) {
                // 获取该班级该科目的任课老师
                int finalG = gradeIndex;
                int finalS = subjectIndex;
                Optional<Lesson> opt = lessonList.stream().filter(item -> item.getGrade().getId().equals(gradeList.get(finalG).getId()) && item.getSubject().getId().equals(subjectList.get(finalS).getId())).findFirst();
                if (opt.isEmpty()) {
                    continue;
                }
                Lesson lesson = opt.get();
                for (int week = 0; week < dayPerWeek; week++) {
                    for (int sort = 0; sort < timeSlotPerDay; sort++) {
                        for (int teacherIndex = 0; teacherIndex < teacherList.size(); teacherIndex++) {
                            if (!teacherList.get(teacherIndex).getId().equals(lesson.getTeacher().getId())) {
                                // 如果不是指定的教师，值必须为0
                                model.addEquality(x[teacherIndex][gradeIndex][subjectIndex][week][sort], 0);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 同时只能存在一节课
     */
    public void timeSlotUniqueConstraint(CpModel model, Literal[][][][][] x, List<Lesson> lessonList, int dayPerWeek, int timeSlotPerDay) {
        List<GradeDO> gradeList = lessonList.stream().map(Lesson::getGrade).distinct().toList();
        List<SubjectDO> subjectList = lessonList.stream().map(Lesson::getSubject).distinct().toList();
        List<TeacherDO> teacherList = lessonList.stream().map(Lesson::getTeacher).distinct().toList();
        for (int gradeIndex = 0; gradeIndex < gradeList.size(); gradeIndex++) {
            for (int week = 0; week < dayPerWeek; week++) {
                for (int sort = 0; sort < timeSlotPerDay; sort++) {
                    List<Literal> possibleSubjectList = new ArrayList<>();
                    for (int s = 0; s < subjectList.size(); s++) {
                        for (int t = 0; t < teacherList.size(); t++) {
                            possibleSubjectList.add(x[t][gradeIndex][s][week][sort]);
                        }
                    }
                    model.addAtMostOne(possibleSubjectList);
                }
            }
        }
    }

    /**
     * 每个教师同时只能上一门课
     */
    public void teacherTimeConstraint(CpModel model, Literal[][][][][] x, List<Lesson> lessonList, int dayPerWeek, int timeSlotPerDay) {
        List<TeacherDO> teacherList = lessonList.stream().map(Lesson::getTeacher).distinct().toList();
        List<GradeDO> gradeList = lessonList.stream().map(Lesson::getGrade).distinct().toList();
        List<SubjectDO> subjectList = lessonList.stream().map(Lesson::getSubject).distinct().toList();

        for (int teacherIndex = 0; teacherIndex < teacherList.size(); teacherIndex++) {
            for (int week = 0; week < dayPerWeek; week++) {
                for (int sort = 0; sort < timeSlotPerDay; sort++) {
                    List<Literal> possibleTeacherList = new ArrayList<>();
                    for (int g = 0; g < gradeList.size(); g++) {
                        for (int s = 0; s < subjectList.size(); s++) {
                            possibleTeacherList.add(x[teacherIndex][g][s][week][sort]);
                        }
                    }
                    model.addAtMostOne(possibleTeacherList);
                }
            }
        }
    }

    /**
     * 每个班级，指定科目每天最多上几节课
     * @param model          模型
     * @param x              变量
     * @param lessonList     课程列表
     * @param dayPerWeek     每周上几天课
     * @param timeSlotPerDay 每天有几节课
     */
    public void gradeSubjectMaxPerDayConstraint(CpModel model, Literal[][][][][] x, List<Lesson> lessonList, int dayPerWeek, int timeSlotPerDay) {
        List<GradeDO> gradeList = lessonList.stream().map(Lesson::getGrade).distinct().toList();
        List<TeacherDO> teacherList = lessonList.stream().map(Lesson::getTeacher).distinct().toList();
        List<SubjectDO> subjectList = lessonList.stream().map(Lesson::getSubject).distinct().toList();
        int gradeSize = gradeList.size();
        int subjectSize = subjectList.size();
        for (int gradeIndex = 0; gradeIndex < gradeSize; gradeIndex++) {
            for (int subjectIndex = 0; subjectIndex < subjectSize; subjectIndex++) {
                // 获取当前班级的该科目的课程数
                GradeDO grade = gradeList.get(gradeIndex);
                SubjectDO subject = subjectList.get(subjectIndex);
                long subjectCount = lessonList.stream().filter(item -> item.getGrade().getId().equals(grade.getId()) && item.getSubject().getId().equals(subject.getId())).count();
                long minCountPerDay = subjectCount / dayPerWeek;
                long maxCountPerDay;
                if (subjectCount % dayPerWeek == 0) {
                    maxCountPerDay = minCountPerDay;
                } else {
                    maxCountPerDay = minCountPerDay + 1;
                }
                for (int w = 0; w < dayPerWeek; w++) {
                    LinearExprBuilder countPerDay = LinearExpr.newBuilder();
                    for (int t = 0; t < teacherList.size(); t++) {
                        for (int y = 0; y < timeSlotPerDay; y++) {
                            countPerDay.add(x[t][gradeIndex][subjectIndex][w][y]);
                        }
                    }
                    model.addLinearConstraint(countPerDay, minCountPerDay, maxCountPerDay);
                }
            }
        }
    }

    /**
     * 每个班级，指定科目每周的课时数
     * @param model          模型
     * @param x              变量
     * @param lessonList     课程列表
     * @param dayPerWeek     每周上几天课
     * @param timeSlotPerDay 每天有几节课
     */
    public void gradeSubjectMaxPerWeekConstraint(CpModel model, Literal[][][][][] x, List<Lesson> lessonList, int dayPerWeek, int timeSlotPerDay) {
        Map<GradeDO, List<Lesson>> gradeLessonMap = lessonList.stream().collect(Collectors.groupingBy(Lesson::getGrade));
        List<TeacherDO> teacherList = lessonList.stream().map(Lesson::getTeacher).distinct().toList();
        List<SubjectDO> subjectList = lessonList.stream().map(Lesson::getSubject).distinct().toList();
        List<GradeDO> gradeList = lessonList.stream().map(Lesson::getGrade).distinct().toList();
        gradeLessonMap.forEach((grade, gradeLessonList) -> {
            // 该班级的所有科目
            List<SubjectDO> gradeSubjectList = gradeLessonList.stream().map(Lesson::getSubject).distinct().toList();

            for (SubjectDO subject : gradeSubjectList) {
                LinearExpr sumExpr = LinearExpr.sum(
                        teacherList.stream()
                                .flatMap(teacher -> IntStream.range(0, dayPerWeek).boxed()
                                        .flatMap(day -> Arrays.stream(x[teacherList.indexOf(teacher)][gradeList.indexOf(grade)][subjectList.indexOf(subject)][day], 0, timeSlotPerDay)))
                                .toArray(Literal[]::new)
                );
                // 获取对应课程的数量
                long subjectCount = gradeLessonList.stream().filter(item -> item.getSubject().getId().equals(subject.getId())).count();
                model.addEquality(sumExpr, subjectCount);
            }
        });
    }


    /**
     * 每天相同的课程必须连续
     * @param model          模型
     * @param x              变量
     * @param lessonList     课程列表
     * @param dayPerWeek     每周上几天课
     * @param timeSlotPerDay 每天有几节课
     */
    public void continuousConstraint(CpModel model, Literal[][][][][] x, List<Lesson> lessonList, int dayPerWeek, int timeSlotPerDay) {
        Map<GradeDO, List<Lesson>> gradeLessonMap = lessonList.stream().collect(Collectors.groupingBy(Lesson::getGrade));
        List<TeacherDO> teacherList = lessonList.stream().map(Lesson::getTeacher).distinct().toList();
        List<SubjectDO> subjectList = lessonList.stream().map(Lesson::getSubject).distinct().toList();
        List<GradeDO> gradeList = lessonList.stream().map(Lesson::getGrade).distinct().toList();
        gradeLessonMap.forEach((grade, gradeLessonList) -> {
            // 该班级的所有科目
            List<SubjectDO> gradeSubjectList = gradeLessonList.stream().map(Lesson::getSubject).distinct().toList();

            for (SubjectDO subject : gradeSubjectList) {
                //该班级的老师
                Lesson lesson = gradeLessonList.stream().filter(item -> item.getSubject().getId().equals(subject.getId())).findFirst().orElseThrow();
                int teacherIndex = teacherList.indexOf(lesson.getTeacher());
                int gradeIndex = gradeList.indexOf(grade);
                int subjectIndex = subjectList.indexOf(subject);

                for (int week = 0; week < dayPerWeek; week++) {
                    for (int sort1 = 0; sort1 < timeSlotPerDay; sort1++) {
                        for (int sort2 = sort1 + 1; sort2 < timeSlotPerDay; sort2++) {
                            // 连堂课不能安排在第5节和第6节
                            if ((sort1 == 4 && sort2 == 5) || (sort1 == 5 && sort2 == 4)) {
                                model.addBoolOr(new Literal[]{x[teacherIndex][gradeIndex][subjectIndex][week][sort1].not(),
                                        x[teacherIndex][gradeIndex][subjectIndex][week][sort2].not(),
                                });
                            }
                            if (Math.abs(sort1 - sort2) != 1) {
                                model.addBoolOr(new Literal[]{
                                        x[teacherIndex][gradeIndex][subjectIndex][week][sort1].not(),
                                        x[teacherIndex][gradeIndex][subjectIndex][week][sort2].not(),
                                });
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 1、3、5语文课尽量往前排，2、4、6英语课尽量往前排
     * @param model          模型
     * @param x              变量
     * @param lessonList     课程列表
     * @param dayPerWeek     每周上几天课
     * @param timeSlotPerDay 每天有几节课
     */
    public void englishAndChinesePreferenceConstraint(CpModel model, Literal[][][][][] x, List<Lesson> lessonList, int dayPerWeek, int timeSlotPerDay) {
        Map<GradeDO, List<Lesson>> gradeLessonMap = lessonList.stream().collect(Collectors.groupingBy(Lesson::getGrade));
        List<TeacherDO> teacherList = lessonList.stream().map(Lesson::getTeacher).distinct().toList();
        List<SubjectDO> subjectList = lessonList.stream().map(Lesson::getSubject).distinct().toList();
        List<GradeDO> gradeList = lessonList.stream().map(Lesson::getGrade).distinct().toList();

        SubjectDO chineseSubject = subjectList.stream().filter(item -> item.getName().equals("语文")).findFirst().orElseThrow();
        SubjectDO englishSubject = subjectList.stream().filter(item -> item.getName().equals("英语")).findFirst().orElseThrow();

        LinearExprBuilder obj = LinearExpr.newBuilder();
        gradeLessonMap.forEach((grade, gradeLessonList) -> {
            int gradeIndex = gradeList.indexOf(grade);
            // 该班级的所有科目
            List<SubjectDO> gradeSubjectList = gradeLessonList.stream().map(Lesson::getSubject).distinct().toList();
            for (SubjectDO subject : gradeSubjectList) {
                int subjectIndex = subjectList.indexOf(subject);

                //该班级的老师
                Lesson lesson = gradeLessonList.stream().filter(item -> item.getSubject().getId().equals(subject.getId())).findFirst().orElseThrow();
                int teacherIndex = teacherList.indexOf(lesson.getTeacher());
                for (int week = 0; week < dayPerWeek; week++) {
                    if (week % 2 == 0 && subject.getId().equals(chineseSubject.getId())) {
                        // 0、2、4（对应周1、3、5)
                        for (int sort = 0; sort < timeSlotPerDay; sort++) {
                            obj.addTerm(x[teacherIndex][gradeIndex][subjectIndex][week][sort], sort);
                        }
                    } else if (week % 2 == 1 && subject.getId().equals(englishSubject.getId())) {
                        // 1、3、5（对应周2、4、6)
                        for (int sort = 0; sort < timeSlotPerDay; sort++) {
                            obj.addTerm(x[teacherIndex][gradeIndex][subjectIndex][week][sort], sort);
                        }
                    }
                }
            }
        });
        model.minimize(obj);
    }

    /**
     * 体育课不能排在上午前两节
     * @param model          模型
     * @param x              变量
     * @param lessonList     课程列表
     * @param dayPerWeek     每周上几天课
     */
    public void sportsTimeSlotLimitConstraint(CpModel model, Literal[][][][][] x, List<Lesson> lessonList, int dayPerWeek) {
        List<TeacherDO> teacherList = lessonList.stream().map(Lesson::getTeacher).distinct().toList();
        List<SubjectDO> subjectList = lessonList.stream().map(Lesson::getSubject).distinct().toList();
        List<GradeDO> gradeList = lessonList.stream().map(Lesson::getGrade).distinct().toList();

        int teacherSize = teacherList.size();
        int gradeSize = gradeList.size();

        SubjectDO sportsSubject = subjectList.stream().filter(item -> item.getName().equals(SubjectEnum.SPORTS.getName())).findFirst().orElse(null);
        if (sportsSubject == null) {
            return;
        }

        int sportSubjectIndex = subjectList.indexOf(sportsSubject);
        for (int teacherIndex = 0; teacherIndex < teacherSize; teacherIndex++) {
            for (int gradeIndex = 0; gradeIndex < gradeSize; gradeIndex++) {
                for (int week = 0; week < dayPerWeek; week++) {
                    for (int sort = 0; sort < 2; sort++) {
                        model.addEquality(x[teacherIndex][gradeIndex][sportSubjectIndex][week][sort], 0);
                    }
                }
            }
        }
    }

    /**
     * 体育课不能排在周1、2、3
     * @param model          模型
     * @param x              变量
     * @param lessonList     课程列表
     */
    public void sportsWeekLimitConstraint(CpModel model, Literal[][][][][] x, List<Lesson> lessonList, int timeSlotPerDay) {
        List<GradeDO> gradeList = lessonList.stream().map(Lesson::getGrade).distinct().toList();
        List<TeacherDO> teacherList = lessonList.stream().map(Lesson::getTeacher).distinct().toList();
        List<SubjectDO> subjectList = lessonList.stream().map(Lesson::getSubject).distinct().toList();
        int teacherSize = teacherList.size();
        int gradeSize = gradeList.size();

        SubjectDO sportSubject = subjectList.stream().filter(item -> item.getName().equals(SubjectEnum.SPORTS.getName())).findFirst().orElse(null);
        if (sportSubject == null) {
            return;
        }

        int sportSubjectIndex = subjectList.indexOf(sportSubject);
        for (int teacherIndex = 0; teacherIndex < teacherSize; teacherIndex++) {
            for (int gradeIndex = 0; gradeIndex < gradeSize; gradeIndex++) {
                for (int week = 0; week < 3; week++) {
                    for (int sort = 0; sort < timeSlotPerDay; sort++) {
                        model.addEquality(x[teacherIndex][gradeIndex][sportSubjectIndex][week][sort], 0);
                    }
                }
            }
        }
    }

    /**
     * 每天的第1、2、6节课必须排课
     * @param model          模型
     * @param x              变量
     * @param lessonList     课程列表
     * @param dayPerWeek     每周上几天课
     */
    public void timeSlotMustConstraint(CpModel model, Literal[][][][][] x, List<Lesson> lessonList, int dayPerWeek) {
        Map<GradeDO, List<Lesson>> gradeLessonMap = lessonList.stream().collect(Collectors.groupingBy(Lesson::getGrade));
        List<GradeDO> gradeList = lessonList.stream().map(Lesson::getGrade).distinct().toList();
        List<TeacherDO> teacherList = lessonList.stream().map(Lesson::getTeacher).distinct().toList();
        List<SubjectDO> subjectList = lessonList.stream().map(Lesson::getSubject).distinct().toList();

        int[] mustSort = new int[]{0, 1, 5};
        gradeLessonMap.forEach((grade, gradeLessonList) -> {
            int gradeIndex = gradeList.indexOf(grade);
            for (int week = 0; week < dayPerWeek; week++) {
                for (int sort : mustSort) {
                    LinearExprBuilder sumExpr = LinearExpr.newBuilder();
                    List<SubjectDO> gradeSubjectList = gradeLessonList.stream().map(Lesson::getSubject).distinct().toList();
                    for (SubjectDO subject : gradeSubjectList) {
                        int subjectIndex = subjectList.indexOf(subject);
                        Lesson lesson = gradeLessonList.stream().filter(item -> item.getSubject().getId().equals(subject.getId())).findFirst().orElseThrow();
                        int teacherIndex = teacherList.indexOf(lesson.getTeacher());
                        sumExpr.add(x[teacherIndex][gradeIndex][subjectIndex][week][sort]);
                    }
                    model.addEquality(sumExpr, 1);
                }
            }
        });
    }

    /**
     * 教师的课程尽量集中
     * @param model          模型
     * @param x              变量
     * @param lessonList     课程列表
     * @param dayPerWeek     每周上几天课
     */
    public void courseFocusConstraint(CpModel model, Literal[][][][][] x, List<Lesson> lessonList, int dayPerWeek, int timeSlotPerDay) {

        List<GradeDO> gradeList = lessonList.stream().map(Lesson::getGrade).distinct().toList();
        List<TeacherDO> teacherList = lessonList.stream().map(Lesson::getTeacher).distinct().toList();
        List<SubjectDO> subjectList = lessonList.stream().map(Lesson::getSubject).distinct().toList();

        int teacherSize = teacherList.size();

        //辅助变量：每个老师每天的最早课时
        IntVar[][] teacherEarliestSort = new IntVar[teacherList.size()][dayPerWeek];
        // 辅助变量：每个老师每天的最晚课时
        IntVar[][] teacherLatestSort = new IntVar[teacherList.size()][dayPerWeek];
        // 辅助变量：每个老师每天的课时差
        IntVar[][] teacherSortGap = new IntVar[teacherList.size()][dayPerWeek];
        for (int teacherIndex = 0; teacherIndex < teacherSize; teacherIndex++) {
            for (int week = 0; week < dayPerWeek; week++) {
                teacherEarliestSort[teacherIndex][week] = model.newIntVar(0, timeSlotPerDay-1,  "teacher_earliest_sort_" + teacherList.get(teacherIndex).getName() + "_" + week);
                teacherLatestSort[teacherIndex][week] = model.newIntVar(0, timeSlotPerDay-1,  "teacher_earliest_sort_" + teacherList.get(teacherIndex).getName() + "_" + week);
                teacherSortGap[teacherIndex][week] = model.newIntVar(0, timeSlotPerDay-1,  "teacher_earliest_sort_" + teacherList.get(teacherIndex).getName() + "_" + week);
            }
        }

        // 添加约束：计算每个老师每天的最早课时和最晚课时
        for (int teacherIndex = 0; teacherIndex < teacherSize; teacherIndex++) {
            TeacherDO teacher = teacherList.get(teacherIndex);
            List<Lesson> teacherLessonList = lessonList.stream().filter(item -> item.getTeacher().getId().equals(teacher.getId())).toList();
            List<GradeDO> teacherGradeList = teacherLessonList.stream().map(Lesson::getGrade).distinct().toList();
            List<SubjectDO> teacherSubjectList = teacherLessonList.stream().map(Lesson::getSubject).distinct().toList();
            for (int week = 0; week < dayPerWeek; week++) {
                // 创建辅助变量，表示老师在某一时段是否游客
                Literal[] hasClass = new Literal[timeSlotPerDay];
                for (int sort = 0; sort < timeSlotPerDay; sort++) {
                    hasClass[sort] = model.newBoolVar("has_class_" + teacherList.get(teacherIndex).getName() + "_" + week + "_" + sort);

                    LinearExprBuilder sumExpr = LinearExpr.newBuilder();
                    for (GradeDO grade : teacherGradeList) {
                        for (SubjectDO subject : teacherSubjectList) {
                            int gradeIndex = gradeList.indexOf(grade);
                            int subjectIndex = subjectList.indexOf(subject);
                            sumExpr.add(x[teacherIndex][gradeIndex][subjectIndex][week][sort]);
                        }
                    }
                    model.addEquality(sumExpr, 1).onlyEnforceIf(hasClass[sort]);
                    model.addEquality(sumExpr, 0).onlyEnforceIf(hasClass[sort].not());
                }

                LinearExpr[] listExpr = new LinearExpr[timeSlotPerDay];
                for (int sort = 0; sort < timeSlotPerDay; sort++) {
                    listExpr[sort] = LinearExpr.newBuilder().addTerm(hasClass[sort], sort).build();
                }
                //最早课时
                model.addMinEquality(teacherEarliestSort[teacherIndex][week], listExpr);
                // 最晚课时
                model.addMaxEquality(teacherLatestSort[teacherIndex][week], listExpr);
            }
        }

        // 添加约束，计算每个老师每天的课时差
        for (int teacherIndex = 0; teacherIndex < teacherSize; teacherIndex++) {
            for (int week = 0; week < dayPerWeek; week++) {
                model.addEquality(teacherSortGap[teacherIndex][week], LinearExpr.weightedSum(new IntVar[]{teacherLatestSort[teacherIndex][week], teacherEarliestSort[teacherIndex][week]}, new long[]{1, -1}));
            }
        }

        LinearExprBuilder sumExpr = LinearExpr.newBuilder();
        for (int teacherIndex = 0; teacherIndex < teacherSize; teacherIndex++) {
            for (int week = 0; week < dayPerWeek; week++) {
                sumExpr.add(teacherSortGap[teacherIndex][week]);
            }
        }
        model.minimize(sumExpr);
    }

}
