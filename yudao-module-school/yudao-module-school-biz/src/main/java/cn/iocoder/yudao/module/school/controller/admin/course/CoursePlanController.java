package cn.iocoder.yudao.module.school.controller.admin.course;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CoursePlanChangeReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CoursePlanListReqVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CoursePlanRespVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.CoursePlanSaveReqVO;
import cn.iocoder.yudao.module.school.convert.course.CoursePlanConvert;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CoursePlanDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.enums.course.CourseNameEnum;
import cn.iocoder.yudao.module.school.enums.course.CoursePlanQueryTypeEnum;
import cn.iocoder.yudao.module.school.enums.course.CourseTypeNameEnum;
import cn.iocoder.yudao.module.school.enums.course.TimeSlotTypeEnum;
import cn.iocoder.yudao.module.school.service.course.CoursePlanService;
import cn.iocoder.yudao.module.school.service.course.CourseTypeService;
import cn.iocoder.yudao.module.school.service.course.TimeSlotService;
import cn.iocoder.yudao.module.school.service.grade.GradeService;
import cn.iocoder.yudao.module.school.service.subject.SubjectService;
import cn.iocoder.yudao.module.school.service.teacher.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.IMPORT;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.*;

@Tag(name = "管理后台 - 课程计划")
@RestController
@RequestMapping("/school/course-plan")
@Validated
@RequiredArgsConstructor
public class CoursePlanController {

    private final CoursePlanService coursePlanService;
    private final SubjectService subjectService;
    private final GradeService gradeService;
    private final TimeSlotService timeSlotService;
    private final TeacherService teacherService;
    private final CourseTypeService courseTypeService;

    public static final Map<String, Integer> WEEK_DATA = new HashMap<>(){{
        put("星期一", 1);
        put("星期二", 2);
        put("星期三", 3);
        put("星期四", 4);
        put("星期五", 5);
        put("星期六", 6);
        put("星期日", 7);
    }};

    @PostMapping("/create")
    @Operation(summary = "创建课程计划")
    @PreAuthorize("@ss.hasPermission('school:course-plan:create')")
    public CommonResult<Long> createCoursePlan(@Valid @RequestBody CoursePlanSaveReqVO createReqVO) {
        return success(coursePlanService.createCoursePlan(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新课程计划")
    @PreAuthorize("@ss.hasPermission('school:course-plan:update')")
    public CommonResult<Boolean> updateCoursePlan(@Valid @RequestBody CoursePlanSaveReqVO updateReqVO) {
        coursePlanService.updateCoursePlan(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除课程计划")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('school:course-plan:delete')")
    public CommonResult<Boolean> deleteCoursePlan(@RequestParam("id") Long id) {
        coursePlanService.deleteCoursePlan(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得课程计划")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:course-plan:query')")
    public CommonResult<CoursePlanRespVO> getCoursePlan(@RequestParam("id") Long id) {
        CoursePlanDO coursePlan = coursePlanService.getCoursePlan(id);
        GradeDO grade = gradeService.getGrade(coursePlan.getGradeId());
        TimeSlotDO timeSlot = timeSlotService.getTimeSlot(coursePlan.getTimeSlotId());
        CourseTypeDO courseType = courseTypeService.getCourseType(coursePlan.getCourseTypeId());

        TeacherDO teacher = null;
        SubjectDO subject = null;

        if (coursePlan.getTeacherId() != null) {
            teacher = teacherService.getTeacher(coursePlan.getTeacherId());
        }
        if (coursePlan.getSubjectId() != null) {
            subject = subjectService.getSubject(coursePlan.getSubjectId());
        }


        return success(CoursePlanConvert.INSTANCE.convert(coursePlan, grade, teacher, subject, timeSlot, courseType));
    }

    @GetMapping("/list")
    @Operation(summary = "获得课程计划分页")
    @PreAuthorize("@ss.hasPermission('school:course-plan:query')")
    public CommonResult<List<CoursePlanRespVO>> getCoursePlanList(@Valid CoursePlanListReqVO reqVO) {
        Integer queryType = reqVO.getQueryType();

        List<CoursePlanDO> coursePlanList = null;
        if (CoursePlanQueryTypeEnum.GRADE.getType().equals(queryType)) {
            if (reqVO.getGradeId() == null) {
                throw exception(GRADE_NOT_EXISTS);
            }
            coursePlanList = coursePlanService.getCoursePlanList(reqVO.getGradeId(), null, null, null, reqVO.getDate());
        } else {
            // 按教师查询课程表
            if (reqVO.getTeacherId() == null) {
                throw exception(TEACHER_NOT_EXISTS);
            }
            coursePlanList = coursePlanService.getCoursePlanList(null, reqVO.getTeacherId(), null, null, reqVO.getDate());

            // 当按教师查询时，由于早自习无法直接挂教师Id,所以正常查询无法查询到早自习课程， 需要单独查询
            List<CoursePlanDO> morningCoursePlanList = new ArrayList<>();
            SubjectDO subjectChinese = subjectService.getSubject(CourseNameEnum.CHINESE.getName());
            SubjectDO subjectEnglish = subjectService.getSubject(CourseNameEnum.ENGLISH.getName());
            CourseTypeDO courseType = courseTypeService.getCourseType(CourseTypeNameEnum.MORNING.getName());
            if (teacherService.hasSubject(reqVO.getTeacherId(), subjectChinese.getId())) {
                morningCoursePlanList.addAll(coursePlanService.getCoursePlanList(null, null, courseType.getId(), subjectChinese.getId(), reqVO.getDate()));
            }
            if (teacherService.hasSubject(reqVO.getTeacherId(), subjectEnglish.getId())) {
                morningCoursePlanList.addAll(coursePlanService.getCoursePlanList(null, null, courseType.getId(), subjectEnglish.getId(), reqVO.getDate()));
            }
            // 由于这里早自习查询时没有指定年级， 所以需要去重
            morningCoursePlanList = CollUtil.distinct(morningCoursePlanList, e -> StrUtil.format("{}_{}_{}_{}", e.getWeek(), e.getCourseTypeId(), e.getSubjectId(), e.getTimeSlotId()), true);
            coursePlanList.addAll(morningCoursePlanList);
        }

        List<GradeDO> gradeList = gradeService.getAll();
        List<TeacherDO> teacherList = teacherService.getAll();
        List<SubjectDO> subjectList = subjectService.getAll();
        List<TimeSlotDO> timeSlotList = timeSlotService.getAll();
        List<CourseTypeDO> courseTypeList = courseTypeService.getAll();

        return success(CoursePlanConvert.INSTANCE.convertList(coursePlanList, gradeList, teacherList, subjectList, timeSlotList, courseTypeList));
    }


    @PostMapping("/import")
    @Operation(summary = "导入课程计划 Excel")
    @PreAuthorize("@ss.hasPermission('school:course-plan:import')")
    @ApiAccessLog(operateType = IMPORT)
    public CommonResult<Boolean> importCoursePlanExcel(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        Workbook workbook = WorkbookFactory.create(inputStream);

        SubjectDO subjectEnglish = subjectService.getSubject(CourseNameEnum.ENGLISH.getName());
        SubjectDO subjectChinese = subjectService.getSubject(CourseNameEnum.CHINESE.getName());

        if (subjectEnglish == null || subjectChinese == null) {
            throw exception(COURSE_PLAN_MORNING_NOT_EXISTS);
        }

        List<CoursePlanDO> coursePlanList = new ArrayList<>();
        // 获取sheet数量
        int numberOfSheets = workbook.getNumberOfSheets();
        // 遍历sheet页
        for (int i = 0; i < numberOfSheets; i++) {
            // 获取sheet名称，sheet名称就是班级名称
            String sheetName = workbook.getSheetName(i);
            GradeDO grade = gradeService.getGrade(sheetName);
            if (grade == null) {
                throw exception(GRADE_NAME_NOT_EXISTS, sheetName);
            }

            Sheet sheet = workbook.getSheetAt(i);
            List<CoursePlanDO> sheetDataList = getSheetCoursePlanData(sheet, grade, 2, 1);

            // 添加早读课程
            List<TimeSlotDO> timeSlotMorningList = timeSlotService.getTimeSlotByType(TimeSlotTypeEnum.MORNING.getType());
            CourseTypeDO courseTypeMorning = courseTypeService.getCourseType(CourseTypeNameEnum.MORNING.getName());
            if (courseTypeMorning == null) {
                throw exception(COURSE_TYPE_NAME_NOT_EXISTS, CourseTypeNameEnum.MORNING.getName());
            }
            for (int week = 1; week < 7; week++) {
                CoursePlanDO coursePlan = new CoursePlanDO();
                coursePlan.setGradeId(grade.getId());
                if (week % 2 == 1) {
                    coursePlan.setSubjectId(subjectChinese.getId());
                } else {
                    coursePlan.setSubjectId(subjectEnglish.getId());
                }
                coursePlan.setCourseTypeId(courseTypeMorning.getId());
                coursePlan.setWeek(week);

                for (TimeSlotDO timeSlot : timeSlotMorningList) {
                    CoursePlanDO item = BeanUtils.toBean(coursePlan, CoursePlanDO.class);
                    item.setTimeSlotId(timeSlot.getId());
                    sheetDataList.add(item);
                }
            }

            coursePlanList.addAll(sheetDataList);
        }

        coursePlanService.createCoursePlan(coursePlanList);
        return success(true);
    }

    @PostMapping("/change")
    @Operation(summary = "调整课程计划")
    @PreAuthorize("@ss.hasPermission('school:course-plan:change')")
    public CommonResult<Boolean> changeCoursePlan(@Valid @RequestBody CoursePlanChangeReqVO reqVO) {
        coursePlanService.changeCoursePlan(reqVO);
        return success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出课程计划 Excel")
    @PreAuthorize("@ss.hasPermission('school:course-plan:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCoursePlanExcel(@Valid CoursePlanListReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
    }


    /**
     * 获取sheet中的课程数据
     * @param sheet sheet信息
     * @param grade 班级信息
     * @param weekRowIndex 课表中星期title所在的行index（从1开始）
     * @param timeSlotCellIndex 课表中节次title列所在的列index（从0开始）
     * @return
     */
    private List<CoursePlanDO> getSheetCoursePlanData(Sheet sheet, GradeDO grade, int weekRowIndex, int timeSlotCellIndex) {
        List<CoursePlanDO> coursePlanList = new ArrayList<>();

        for (int rowIndex = weekRowIndex; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;
            // 1. 获取课程节次信息
            Cell timeSlotCell = row.getCell(timeSlotCellIndex);
            TimeSlotDO timeSlot = null;
            if (timeSlotCell == null) {
                throw exception(COURSE_PLAN_IMPORT_TIME_SLOT_ERROR,  sheet.getSheetName(), rowIndex + 1);
            }
            String timeSlotStr = timeSlotCell.getStringCellValue();
            timeSlotStr = timeSlotStr.split("\\n")[0].trim();
            timeSlotStr = ReUtil.getGroup0("\\d+", timeSlotStr);
            if (StrUtil.isEmpty(timeSlotStr)) {
                throw exception(COURSE_PLAN_IMPORT_TIME_SLOT_ERROR,  sheet.getSheetName(), rowIndex + 1);
            }
            timeSlot = timeSlotService.getTimeSlotBySort(Convert.toInt(timeSlotStr) + 1);
            if (timeSlot == null) {
                throw exception(COURSE_PLAN_IMPORT_TIME_SLOT_SORT_NOT_EXISTS, sheet.getSheetName(), timeSlot);
            }

            //2. 开始迭代每一列
            for (int colIndex = timeSlotCellIndex + 1; colIndex < row.getLastCellNum(); colIndex++) {
                Cell cell = row.getCell(colIndex);
                if (cell == null) continue;
                CoursePlanDO coursePlan = new CoursePlanDO();
                coursePlan.setGradeId(grade.getId());
                coursePlan.setTimeSlotId(timeSlot.getId());

                // 2.1 获取周信息
                Row headerRow = sheet.getRow(1);
                Cell headerCell = headerRow.getCell(colIndex);
                if (headerCell == null) {
                    throw exception(COURSE_PLAN_IMPORT_TIME_SLOT_WEEK_ERROR, sheet.getSheetName(), rowIndex + 1);
                }
                String weekStr = headerCell.getStringCellValue();
                Integer week = WEEK_DATA.get(weekStr);
                if (week == null) {
                    throw exception(COURSE_PLAN_IMPORT_TIME_SLOT_WEEK_ERROR, sheet.getSheetName(), rowIndex + 1);
                }
                coursePlan.setWeek(week);

                // 2.2 获取课程信息
                String courseStr = cell.getStringCellValue();
                if (StrUtil.isEmpty(courseStr)) {
                    continue;
                }
                List<String> courseStrSplitList = StrUtil.splitTrim(courseStr, StrUtil.LF);
                if (courseStrSplitList.size() == 2) {
                    // 正课
                    String subjectName = courseStrSplitList.get(0);
                    String teacherName = courseStrSplitList.get(1);
                    SubjectDO subject = subjectService.getSubject(subjectName);
                    TeacherDO teacher = teacherService.getTeacher(teacherName);
                    if (subject == null) {
                        throw exception(SUBJECT_NAME_NOT_EXISTS, subjectName);
                    }
                    if (teacher == null) {
                        throw exception(TEACHER_NAME_NOT_EXISTS, teacherName);
                    }
                    if (!teacher.getSubjectIds().contains(subject.getId())) {
                        throw exception(TEACHER_SUBJECT_NOT_EXISTS, teacherName, subjectName);
                    }

                    CourseTypeDO courseType = courseTypeService.getCourseType(CourseTypeNameEnum.NORMAL.getName());
                    if (courseType == null) {
                        throw exception(COURSE_TYPE_NAME_NOT_EXISTS, CourseTypeNameEnum.NORMAL.getName());
                    }

                    coursePlan.setCourseTypeId(courseType.getId());
                    coursePlan.setTeacherId(teacher.getId());
                    coursePlan.setSubjectId(subject.getId());
                } else if (courseStrSplitList.size() == 1){
                    if (timeSlot.getType().equals(TimeSlotTypeEnum.NIGHT_SELF.getType())) {
                        // 晚自习， 课表中只有教师姓名
                        String teacherName = courseStrSplitList.get(0);
                        TeacherDO teacher = teacherService.getTeacher(teacherName);
                        if (teacher == null) {
                            throw exception(TEACHER_NAME_NOT_EXISTS, teacherName);
                        }
                        CourseTypeDO courseType = courseTypeService.getCourseType(CourseTypeNameEnum.EVENING.getName());
                        if (courseType == null) {
                            throw exception(COURSE_TYPE_NAME_NOT_EXISTS, CourseTypeNameEnum.NORMAL.getName());
                        }
                        coursePlan.setCourseTypeId(courseType.getId());
                        coursePlan.setTeacherId(teacher.getId());
                    } else if (timeSlot.getType().equals(TimeSlotTypeEnum.DAYTIME.getType())) {
                        // 白天自习课
                        String teacherName = courseStrSplitList.get(0);
                        TeacherDO teacher = teacherService.getTeacher(teacherName);
                        if (teacher == null) {
                            throw exception(TEACHER_NAME_NOT_EXISTS, teacherName);
                        }
                        CourseTypeDO courseType = courseTypeService.getCourseType(CourseTypeNameEnum.SELF.getName());
                        if (courseType == null) {
                            throw exception(COURSE_TYPE_NAME_NOT_EXISTS, CourseTypeNameEnum.NORMAL.getName());
                        }
                        coursePlan.setCourseTypeId(courseType.getId());
                        coursePlan.setTeacherId(teacher.getId());
                    }
                }
                coursePlanList.add(coursePlan);
            }

        }
        return coursePlanList;
    }

}