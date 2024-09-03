package cn.iocoder.yudao.module.school.controller.admin.course;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.*;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherListReqVO;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherPageReqVO;
import cn.iocoder.yudao.module.school.convert.course.CourseFeeConvert;
import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseFeeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import cn.iocoder.yudao.module.school.enums.course.CourseTypeEnum;
import cn.iocoder.yudao.module.school.framework.poi.annotation.Excel;
import cn.iocoder.yudao.module.school.rule.calculate.RuleCalculateHandler;
import cn.iocoder.yudao.module.school.service.course.CourseFeeService;
import cn.iocoder.yudao.module.school.service.course.TimeSlotService;
import cn.iocoder.yudao.module.school.service.grade.GradeService;
import cn.iocoder.yudao.module.school.service.subject.SubjectService;
import cn.iocoder.yudao.module.school.service.teacher.TeacherService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 课时费明细")
@RestController
@RequestMapping("/school/course-fee")
@Validated
@RequiredArgsConstructor
public class CourseFeeController {

    private final CourseFeeService courseFeeService;
    private final TeacherService teacherService;
    private final GradeService gradeService;
    private final SubjectService subjectService;
    private final TimeSlotService timeSlotService;
    private final List<RuleCalculateHandler> calculateHandlerList;

    @GetMapping("/page")
    @Operation(summary = "获得课时费明细分页")
    @PreAuthorize("@ss.hasPermission('school:course-fee:query')")
    public CommonResult<PageResult<CourseFeeRespVO>> getCourseFeePage(@Valid CourseFeePageReqVO pageReqVO) {
        LocalDate date = pageReqVO.getDate();
        LocalDate startDate = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = date.with(TemporalAdjusters.lastDayOfMonth());

        CourseFeeListReqVO listReqVO = new CourseFeeListReqVO();
        listReqVO.setStartDate(startDate);
        listReqVO.setEndDate(endDate);
        listReqVO.setTeacherId(pageReqVO.getTeacherId());
        List<CourseFeeDO> courseFeeList = courseFeeService.getCourseFeeList(listReqVO);
        if (CollUtil.isEmpty(courseFeeList)) {
            return success(PageResult.empty());
        }

        List<Long> teacherIds = courseFeeList.stream().map(CourseFeeDO::getTeacherId).distinct().toList();
        TeacherPageReqVO teacherPageReqVO = new TeacherPageReqVO();
        teacherPageReqVO.setPageNo(pageReqVO.getPageNo());
        teacherPageReqVO.setPageSize(pageReqVO.getPageSize());
        teacherPageReqVO.setIds(teacherIds);
        PageResult<TeacherDO> pageResult = teacherService.getTeacherPage(teacherPageReqVO);

        return success(CourseFeeConvert.INSTANCE.convertPage(pageResult, courseFeeList));
    }

    @PostMapping("/calculate")
    @Operation(summary = "课时费计算")
    @PreAuthorize("@ss.hasPermission('school:course-fee:calculate')")
    public CommonResult<Boolean> calculateCourseFee(@Valid @RequestBody CourseFeeCalculateReqVO reqVO) {
        LocalDate date = reqVO.getDate();
        LocalDate start = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());

        // 删除原有数据
        courseFeeService.removeCourseFee(reqVO.getTeacherId(), start, end);

        // 计算所有课时费（不包含规则设置）
        List<CourseFeeDO> courseFeeList = calculate(reqVO, start, end);

        // 过滤放假规则
        for (RuleCalculateHandler handler : calculateHandlerList) {
            handler.handleCourseFee(courseFeeList, start, end);
        }

        courseFeeService.createCourseFeeBatch(courseFeeList);
        return success(true);
    }

    @GetMapping("/detail")
    @Operation(summary = "课时费明细")
    @PreAuthorize("@ss.hasPermission('school:course-fee:detail')")
    public CommonResult<List<CourseFeeCountRespVO>> detail(@Valid CourseFeeCountReqVO reqVO) {
        LocalDate date = reqVO.getDate();
        LocalDate start = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());

        CourseFeeListReqVO listReqVO = new CourseFeeListReqVO();
        listReqVO.setStartDate(start);
        listReqVO.setEndDate(end);
        listReqVO.setTeacherId(reqVO.getTeacherId());
        List<CourseFeeDO> list = courseFeeService.getCourseFeeList(listReqVO);

        // 获取所有相关的教师
        List<Long> teacherIds = list.stream().map(CourseFeeDO::getTeacherId).distinct().toList();
        List<TeacherDO> teacherList = teacherService.getTeacherListByIds(teacherIds);

        // 获取所有相关的班级
        List<Long> gradeIds = list.stream().map(CourseFeeDO::getGradeId).distinct().toList();
        List<GradeDO> gradeList = gradeService.getGradeListByIds(gradeIds);

        // 获取所有相关的课程
        List<Long> subjectIds = list.stream().map(CourseFeeDO::getSubjectId).distinct().toList();
        List<SubjectDO> subjectList = subjectService.getSubjectList(subjectIds);

        // 获取所有相关的节次信息
        List<Long> timeSlotIds = list.stream().map(CourseFeeDO::getTimeSlotId).distinct().toList();
        List<TimeSlotDO> timeSlotList = timeSlotService.getTimeSlotListByIds(timeSlotIds);

        return success(CourseFeeConvert.INSTANCE.convertListGroupByDay(list, teacherList, gradeList, subjectList, timeSlotList));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出课时费明细 Excel")
    @PreAuthorize("@ss.hasPermission('school:course-fee:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCourseFeeExcel(@Valid CourseFeeExportReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        LocalDate date = pageReqVO.getDate();
        LocalDate startDate = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = date.with(TemporalAdjusters.lastDayOfMonth());

        CourseFeeListReqVO listReqVO = new CourseFeeListReqVO();
        listReqVO.setStartDate(startDate);
        listReqVO.setEndDate(endDate);
        List<CourseFeeDO> courseFeeList = courseFeeService.getCourseFeeList(listReqVO);
        List<Long> teacherIdList = courseFeeList.stream().map(CourseFeeDO::getTeacherId).toList();
        List<TeacherDO> teacherList = teacherService.getTeacherListByIds(teacherIdList);
        // List<CourseFeeExportRespVO> data = data(date, courseFeeList, teacherList);

        // 创建一个工作簿
        Workbook workbook = new XSSFWorkbook();
        // 创建一个工作表
        Sheet sheet = workbook.createSheet("Sheet1");

        // 示例数据
        List<CourseFeeExportRespVO> data = new ArrayList<>();
        data.add(new CourseFeeExportRespVO("早读", "张三"));

        int rowNum = 0;
        // 设置头
        Field[] allFields = ClassUtil.getDeclaredFields(CourseFeeExportRespVO.class);
        int headIndex = 0;
        Row headRow = sheet.createRow(rowNum);
        for (Field field : allFields) {
            if (field.isAnnotationPresent(Excel.class)) {
                Excel attr = field.getAnnotation(Excel.class);
                String name = attr.name();
                Cell cell = headRow.createCell(headIndex++);
                cell.setCellValue(name);
            }
        }

        rowNum += 1;
        // 创建行和单元格并填充数据
        for (CourseFeeExportRespVO respVo : data) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;

            for (Field field : allFields) {
                String value = (String) ReflectUtil.getFieldValue(respVo, field);

                Cell cell = row.createCell(colNum++);
                cell.setCellValue(value);
            }
        }

        // 将工作簿写入响应输出流
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }
    }

    private void getFields(Class<?> clazz) {
        Field[] fields = ClassUtil.getDeclaredFields(clazz);
    }

    private List<CourseFeeExportRespVO> data(LocalDate date, List<CourseFeeDO> courseFeeList, List<TeacherDO> teacherList) {
        List<CourseFeeExportRespVO> dataList = new ArrayList<>();

        teacherList = teacherList.stream().sorted(Comparator.comparingInt(TeacherDO::getSort)).toList();

        //1:早自习
        List<TimeSlotDO> timeSlotMorningList = timeSlotService.getTimeSlotByType(CourseTypeEnum.MORNING.getType());
        List<Long> timeSlotMorningIdList = timeSlotMorningList.stream().map(TimeSlotDO::getId).toList();
        for (TeacherDO teacher : teacherList) {
            List<CourseFeeDO> morningCourseFeeList = courseFeeList.stream()
                    .filter(item -> timeSlotMorningIdList.contains(item.getTimeSlotId()) &&
                            item.getTeacherId().equals(teacher.getId()))
                    .toList();
            if (CollUtil.isNotEmpty(morningCourseFeeList)) {
                CourseFeeExportRespVO item = new CourseFeeExportRespVO();
                // 汇总
                // double sum = morningCourseFeeList.stream().mapToDouble(item -> item.getCount().doubleValue()).sum();

                item.setGradeName("早自习");
                item.setTeacherName(teacher.getName());
                // dataRowList.add("SUM(D" + dataList.size() + 1 + ":AJ" + dataList.size() + 1 + ")");
                // dataRowList.add("");
                // dataRowList.add("");

                // LocalDate startDate = date.with(TemporalAdjusters.firstDayOfMonth());
                // LocalDate endDate = date.with(TemporalAdjusters.lastDayOfMonth());
                // while (!startDate.isAfter(endDate)) {
                //     LocalDate finalStartDate = startDate;
                //     List<CourseFeeDO> currentDateCourseFeeList = morningCourseFeeList.stream().filter(item -> item.getDate().isEqual(finalStartDate)).toList();
                //     if (CollUtil.isNotEmpty(currentDateCourseFeeList)) {
                //         double currentSum = currentDateCourseFeeList.stream().mapToDouble(item -> item.getCount().doubleValue()).sum();
                //         dataRowList.add(currentSum);
                //     } else {
                //         dataRowList.add(0);
                //     }
                //     startDate = startDate.plusDays(1);
                // }
                dataList.add(item);
            }
        }

        List<GradeDO> gradeList = gradeService.getGradeListByParentIds(List.of(0L));
        gradeList = gradeList.stream().sorted(Comparator.comparingInt(GradeDO::getSort)).toList();
        for (GradeDO grade : gradeList) {
            List<GradeDO> subGradeList = gradeService.getGradeListByParentIds(List.of(grade.getId()));
            List<Long> subGradeIdList = subGradeList.stream().map(GradeDO::getId).toList();

            for (TeacherDO teacher : teacherList) {
                List<CourseFeeDO> currentCourseFeeList = courseFeeList.stream().filter(item -> item.getTeacherId().equals(teacher.getId()) && subGradeIdList.contains(item.getGradeId())).toList();

                if (CollUtil.isNotEmpty(currentCourseFeeList)) {
                    List<Object> dataRowList = new ArrayList<>();

                    // 汇总
                    // double sum = currentCourseFeeList.stream().mapToDouble(item -> item.getCount().doubleValue()).sum();

                    dataRowList.add(grade.getName());
                    dataRowList.add(teacher.getName());
                    dataRowList.add("SUM(D" + dataList.size() + 1 + ":AJ" + dataList.size() + 1 + ")");
                    dataRowList.add("");
                    dataRowList.add("");

                    LocalDate startDate = date.with(TemporalAdjusters.firstDayOfMonth());
                    LocalDate endDate = date.with(TemporalAdjusters.lastDayOfMonth());
                    while (!startDate.isAfter(endDate)) {
                        LocalDate finalStartDate = startDate;
                        List<CourseFeeDO> currentDateCourseFeeList = currentCourseFeeList.stream().filter(item -> item.getDate().isEqual(finalStartDate)).toList();
                        if (CollUtil.isNotEmpty(currentDateCourseFeeList)) {
                            double currentSum = currentDateCourseFeeList.stream().mapToDouble(item -> item.getCount().doubleValue()).sum();
                            dataRowList.add(currentSum);
                        } else {
                            dataRowList.add(0);
                        }
                        startDate = startDate.plusDays(1);
                    }
                    // dataList.add(dataRowList);
                }
            }
        }


        return dataList;
    }

    private List<List<String>> head(LocalDate date) {
        List<List<String>> list = new ArrayList<>();

        List<String> classHead = new ArrayList<>();
        classHead.add("年级");
        list.add(classHead);

        List<String> teacherHead = new ArrayList<>();
        teacherHead.add("教师");
        list.add(teacherHead);

        List<String> summaryHead = new ArrayList<>();
        summaryHead.add("汇总");
        list.add(summaryHead);

        List<String> dutyHead = new ArrayList<>();
        dutyHead.add("值班");
        list.add(dutyHead);

        List<String> remarkHead = new ArrayList<>();
        remarkHead.add("备注\n" + "（调课、早读、班主任看自习等）");
        list.add(remarkHead);

        LocalDate startDate = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = date.with(TemporalAdjusters.lastDayOfMonth());
        while (!startDate.isAfter(endDate)) {
            List<String> dateHead = new ArrayList<>();
            dateHead.add(LocalDateTimeUtil.format(startDate, "MM-dd"));
            list.add(dateHead);

            startDate = startDate.plusDays(1);
        }

        return list;
    }


    private List<CourseFeeDO> calculate(CourseFeeCalculateReqVO reqVO, LocalDate start, LocalDate end) {

        List<CourseFeeDO> courseFeeList = new ArrayList<>();
        LocalDate currentDate = start;
        while (!currentDate.isAfter(end)) {
            courseFeeList.addAll(courseFeeService.calculateCourseFee(currentDate,  null,null, null, reqVO.getTeacherId()));

            currentDate = currentDate.plusDays(1);
        }

        return courseFeeList;
    }

}