package cn.iocoder.yudao.module.school.controller.admin.timetable;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting.TimetableSettingPageReqVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting.TimetableSettingPageRespVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting.TimetableSettingRespVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting.TimetableSettingSaveReqVO;
import cn.iocoder.yudao.module.school.convert.timetable.TimetableSettingConvert;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableSettingDO;
import cn.iocoder.yudao.module.school.enums.course.CourseTypeEnum;
import cn.iocoder.yudao.module.school.service.course.CourseTypeService;
import cn.iocoder.yudao.module.school.service.grade.GradeService;
import cn.iocoder.yudao.module.school.service.subject.SubjectService;
import cn.iocoder.yudao.module.school.service.teacher.TeacherService;
import cn.iocoder.yudao.module.school.service.timetable.TimetableService;
import cn.iocoder.yudao.module.school.service.timetable.TimetableSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.IMPORT;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.school.enums.ErrorCodeConstants.*;

@Tag(name = "管理后台 - 排课计划设置")
@RestController
@RequestMapping("/school/timetable-setting")
@Validated
@RequiredArgsConstructor
public class TimetableSettingController {

    private final TimetableSettingService timetableSettingService;
    private final GradeService gradeService;
    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final CourseTypeService courseTypeService;
    private final TimetableService timetableService;

    @SneakyThrows
    @PostMapping("/import")
    @Operation(summary = "导入课程计划设置 Excel")
    @PreAuthorize("@ss.hasPermission('school:timetable-setting:import')")
    @ApiAccessLog(operateType = IMPORT)
    public CommonResult<Boolean> importCoursePlanExcel(@RequestParam("file") MultipartFile file, Long timetableId) throws ServiceException {
        if (timetableService.getTimetable(timetableId) == null) {
            return error(TIMETABLE_NOT_EXISTS);
        }

        List<GradeDO> gradeList = gradeService.getAll();
        List<SubjectDO> subjectList = subjectService.getAll();
        List<TeacherDO> teacherList = teacherService.getAll();
        CourseTypeDO courseType = courseTypeService.getCourseTypeByType(CourseTypeEnum.NORMAL.getType());

        InputStream inputStream = file.getInputStream();
        Workbook workbook = WorkbookFactory.create(inputStream);

        // 只获取第一个sheet
        Sheet sheet = workbook.getSheetAt(0);

        // 匹配中文正则
        Pattern chinesePattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        // 匹配数字
        Pattern digitPattern = Pattern.compile("\\d+");
        // 匹配 (数字)班
        Pattern gradePattern = Pattern.compile("\\d+班");

        String gradeTopName = "";
        String gradeSecondName = "";
        // 每个科目的课时map
        Map<String, Integer> subjectCountMap = new HashMap<>();
        // 每个科目对应的列索引map
        Map<Integer, String> subjectCellIndexMap = new HashMap<>();

        List<TimetableSettingDO> timetableSettingList = new ArrayList<>();

        int rowCount = sheet.getLastRowNum();
        for (int rowIndex = 0; rowIndex < rowCount + 1; rowIndex++) {
            gradeSecondName = "";
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            int cellCount = row.getLastCellNum();
            // 存储值和列索引的map
            Map<String, Integer> cellValueIndexMap = new HashMap<>();
            for (int cellIndex = 0; cellIndex < cellCount; cellIndex++) {
                Cell timeSlotCell = row.getCell(cellIndex);
                String value = timeSlotCell.getStringCellValue();
                if (StrUtil.isNotEmpty(value)) {
                    cellValueIndexMap.put(value, cellIndex);
                }
            }

            // 找到当前行是几班的
            for (Map.Entry<String, Integer> entry : cellValueIndexMap.entrySet()) {
                String value = entry.getKey();
                if (gradePattern.matcher(value).find()) {
                    gradeSecondName = value;
                }
            }

            if (cellValueIndexMap.size() == 1) {
                // 说明是年级标题行
                for (Map.Entry<String, Integer> entry : cellValueIndexMap.entrySet()) {
                    String gradeTitle = entry.getKey();
                    gradeTopName = gradeTitle.replace("教师分工表", "");
                }
                subjectCountMap = new HashMap<>();
                subjectCellIndexMap = new HashMap<>();
            } else if (cellValueIndexMap.size() > 1) {
                for (Map.Entry<String, Integer> entry : cellValueIndexMap.entrySet()) {
                    String value = entry.getKey();
                    Integer index = entry.getValue();
                    if (Character.isDigit(value.charAt(value.length() - 1))) {
                        //如果是以数字结尾, 说明是课时标题行
                        String subjectName = "";
                        Integer count = 0;
                        Matcher subjectMatcher = chinesePattern.matcher(value);
                        Matcher countMatcher = digitPattern.matcher(value);
                        while (subjectMatcher.find()) {
                            subjectName += subjectMatcher.group();
                        }
                        while (countMatcher.find()) {
                            count += Integer.valueOf(countMatcher.group());
                        }
                        if (StrUtil.isEmpty(subjectName) || count == 0) {
                            throw new Exception("错误");
                        }

                        subjectCountMap.put(subjectName, count);
                        subjectCellIndexMap.put(index, subjectName);
                    } else {
                        Integer currentIndex = entry.getValue();
                        if (!subjectCellIndexMap.containsKey(currentIndex)) {
                            continue;
                        }
                        // 这里到了真正的每个人的课时定义处
                        String teacherName = value;

                        // 获取当前列对应的科目
                        String subjectName = subjectCellIndexMap.get(currentIndex);
                        Integer count = subjectCountMap.get(subjectName);

                        final String gradeName = gradeTopName + gradeSecondName;
                        GradeDO grade = gradeList.stream().filter(item -> item.getName().equals(gradeName)).findFirst().orElse(null);
                        if (grade == null) {
                            throw exception(GRADE_NAME_NOT_EXISTS, gradeName);
                        }
                        SubjectDO subject = subjectList.stream().filter(item -> item.getName().equals(subjectName)).findFirst().orElse(null);
                        if (subject == null) {
                            throw exception(SUBJECT_NAME_NOT_EXISTS, subjectName);
                        }
                        TeacherDO teacher = teacherList.stream().filter(item -> item.getName().equals(teacherName)).findFirst().orElse(null);
                        if (teacher == null) {
                            throw exception(TEACHER_NAME_NOT_EXISTS, teacherName);
                        }

                        TimetableSettingDO timetableSetting = new TimetableSettingDO();
                        timetableSetting.setTimetableId(timetableId);
                        timetableSetting.setCourseTypeId(courseType.getId());
                        timetableSetting.setGradeId(grade.getId());
                        timetableSetting.setSubjectId(subject.getId());
                        timetableSetting.setTeacherId(teacher.getId());
                        timetableSetting.setCount(count);
                        timetableSettingList.add(timetableSetting);
                    }
                }
            }
        }
        timetableSettingService.createTimetableSettingBatch(timetableSettingList);

        return success(true);
    }

    @PostMapping("/create")
    @Operation(summary = "创建排课计划设置")
    @PreAuthorize("@ss.hasPermission('school:timetable-setting:create')")
    public CommonResult<Long> createTimetableSetting(@Valid @RequestBody TimetableSettingSaveReqVO createReqVO) {
        return success(timetableSettingService.createTimetableSetting(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新排课计划设置")
    @PreAuthorize("@ss.hasPermission('school:timetable-setting:update')")
    public CommonResult<Boolean> updateTimetableSetting(@Valid @RequestBody TimetableSettingSaveReqVO updateReqVO) {
        timetableSettingService.updateTimetableSetting(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除排课计划设置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('school:timetable-setting:delete')")
    public CommonResult<Boolean> deleteTimetableSetting(@RequestParam("id") Long id) {
        timetableSettingService.deleteTimetableSetting(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得排课计划设置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('school:timetable-setting:query')")
    public CommonResult<TimetableSettingRespVO> getTimetableSetting(@RequestParam("id") Long id) {
        TimetableSettingDO timetableSetting = timetableSettingService.getTimetableSetting(id);

        return success(BeanUtils.toBean(timetableSetting, TimetableSettingRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得排课计划设置分页")
    @PreAuthorize("@ss.hasPermission('school:timetable-setting:query')")
    public CommonResult<PageResult<TimetableSettingPageRespVO>> getTimetableSettingPage(@Valid TimetableSettingPageReqVO pageReqVO) {
        PageResult<TimetableSettingDO> pageResult = timetableSettingService.getTimetableSettingPage(pageReqVO);

        // 获取所有的班级
        List<GradeDO> gradeList = gradeService.getAll();
        // 获取所有的教师
        List<TeacherDO> teacherList = teacherService.getAll();
        // 获取所有的课程科目
        List<SubjectDO> subjectList = subjectService.getAll();
        List<CourseTypeDO> courseTypeList = courseTypeService.getAll();


        return success(TimetableSettingConvert.INSTANCE.convertPage(pageResult, gradeList, subjectList, teacherList, courseTypeList));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出排课计划设置 Excel")
    @PreAuthorize("@ss.hasPermission('school:timetable-setting:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTimetableSettingExcel(@Valid TimetableSettingPageReqVO pageReqVO,
                                            HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TimetableSettingDO> list = timetableSettingService.getTimetableSettingPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "排课计划设置.xls", "数据", TimetableSettingPageRespVO.class,
                BeanUtils.toBean(list, TimetableSettingPageRespVO.class));
    }

}
