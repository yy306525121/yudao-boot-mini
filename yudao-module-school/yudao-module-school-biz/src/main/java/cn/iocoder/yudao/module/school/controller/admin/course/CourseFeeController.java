package cn.iocoder.yudao.module.school.controller.admin.course;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.*;
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

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
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
        // 数据
        List<CourseFeeExportDetailRespVO> data = getCourseFeeDate(date);

        // 创建一个工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet1 = workbook.createSheet(date.getMonth().getValue() + "月课时费统计");
        int sheet1RowNum = 0;
        Field[] sheet1FieldList = ClassUtil.getDeclaredFields(CourseFeeExportCountRespVO.class);
        int sheet1HeadIndex = 0;
        Row sheet1HeadRow = sheet1.createRow(sheet1RowNum);
        for (Field field : sheet1FieldList) {
            if (field.isAnnotationPresent(Excel.class)) {
                Excel attr = field.getAnnotation(Excel.class);
                String name = attr.name();
                if (StrUtil.isNotEmpty(name)) {
                    Cell cell = sheet1HeadRow.createCell(sheet1HeadIndex++);
                    cell.setCellValue(name);
                }
            }
        }




        // 创建一个工作表
        Sheet sheet2 = workbook.createSheet(date.getMonth().getValue() + "月课时详情");


        int sheet2RowNum = 0;
        // 设置头
        Field[] sheet2FieldList = ClassUtil.getDeclaredFields(CourseFeeExportDetailRespVO.class);
        int sheet2HeadIndex = 0;
        Row sheet2HeadRow = sheet2.createRow(sheet2RowNum);
        for (Field field : sheet2FieldList) {
            if (field.isAnnotationPresent(Excel.class)) {
                Excel attr = field.getAnnotation(Excel.class);

                String name = null;
                if (attr.isDateTitle() && field.getName().startsWith("day")) {
                    String fieldName = field.getName();
                    int sort = Integer.parseInt(fieldName.replaceAll("day", ""));
                    if (sort <= DateUtil.lengthOfMonth(date.getMonthValue(), DateUtil.isLeapYear(date.getYear()))) {
                        LocalDate localDate = LocalDate.of(date.getYear(), date.getMonth(), sort);
                        name = LocalDateTimeUtil.format(localDate, attr.dateTitleFormat());
                    }
                } else {
                    name = attr.name();
                }

                if (StrUtil.isNotEmpty(name)) {
                    Cell cell = sheet2HeadRow.createCell(sheet2HeadIndex++);
                    cell.setCellValue(name);
                }
            }
        }

        sheet2RowNum += 1;
        // 数据行
        for (CourseFeeExportDetailRespVO respVo : data) {
            Row row = sheet2.createRow(sheet2RowNum++);
            int colNum = 0;

            for (Field field : sheet2FieldList) {
                if (!field.isAnnotationPresent(Excel.class)) {
                    continue;
                }
                Excel attr = field.getAnnotation(Excel.class);
                if (attr.isSummary()) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellFormula(StrUtil.format("SUM(D{}:AJ{})", sheet2RowNum, sheet2RowNum));
                } else if (field.getType().getName().equals(String.class.getName())) {
                    String value = (String) ReflectUtil.getFieldValue(respVo, field);
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(value);
                } else if (field.getType().getName().equals(Double.class.getName())) {
                    Double value = (Double) ReflectUtil.getFieldValue(respVo, field);
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(value);
                }
            }
        }

        // 将工作簿写入响应输出流
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }
    }

    private List<CourseFeeExportDetailRespVO> getCourseFeeDate(LocalDate date) {
        LocalDate startDate = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = date.with(TemporalAdjusters.lastDayOfMonth());

        List<CourseFeeExportDetailRespVO> dataList = new ArrayList<>();


        //1:早自习
        List<TimeSlotDO> timeSlotList = timeSlotService.getTimeSlotByType(CourseTypeEnum.MORNING.getType());
        List<Long> timeSlotIdList = timeSlotList.stream().map(TimeSlotDO::getId).toList();
        CourseFeeListReqVO listReqVO = new CourseFeeListReqVO();
        listReqVO.setStartDate(startDate);
        listReqVO.setEndDate(endDate);
        listReqVO.setTimeSlotIdList(timeSlotIdList);
        List<CourseFeeDO> courseFeeList = courseFeeService.getCourseFeeList(listReqVO);
        List<Long> teacherIdList = courseFeeList.stream().map(CourseFeeDO::getTeacherId).toList();
        List<TeacherDO> teacherList = teacherService.getTeacherListByIds(teacherIdList);
        teacherList = teacherList.stream().sorted(Comparator.comparing(TeacherDO::getSort)).toList();
        for (TeacherDO teacher : teacherList) {
            CourseFeeExportDetailRespVO data = new CourseFeeExportDetailRespVO();
            data.setGradeName(CourseTypeEnum.MORNING.getName());
            data.setTeacherName(teacher.getName());
            data.setOnDuty(0.0);
            data.setRemark(0.0);

            LocalDate tempDate = startDate;
            while (!tempDate.isAfter(endDate)) {
                LocalDate finalTempDate = tempDate;
                double sum = courseFeeList.stream().filter(item -> item.getTeacherId().equals(teacher.getId()) && finalTempDate.isEqual(item.getDate())).mapToDouble(item -> item.getCount().doubleValue()).sum();

                int sort = DateUtil.dayOfMonth(new DateTime(tempDate));
                ReflectUtil.setFieldValue(data, "day" + sort, sum);
                tempDate = tempDate.plusDays(1);
            }
            dataList.add(data);
        }

        // 接下来的查询结果要排除早自习
        List<Long> finalTimeSlotIdList = timeSlotIdList;
        timeSlotIdList = timeSlotList.stream().filter(item -> !finalTimeSlotIdList.contains(item.getId())).map(TimeSlotDO::getId).toList();
        listReqVO.setTimeSlotIdList(timeSlotIdList);

        //2:其他年级
        List<GradeDO> gradeList = gradeService.getGradeListByParentIds(List.of(0L));
        for (GradeDO grade : gradeList) {
            List<Long> subGradeIdList = gradeService.getGradeListByParentIds(List.of(grade.getId())).stream().map(GradeDO::getId).toList();
            listReqVO.setGradeIdList(subGradeIdList);
            courseFeeList = courseFeeService.getCourseFeeList(listReqVO);

            teacherIdList = courseFeeList.stream().map(CourseFeeDO::getTeacherId).toList();
            teacherList = teacherService.getTeacherListByIds(teacherIdList);
            for (TeacherDO teacher : teacherList) {
                CourseFeeExportDetailRespVO data = new CourseFeeExportDetailRespVO();
                data.setGradeName(grade.getName());
                data.setTeacherName(teacher.getName());
                data.setOnDuty(0.0);
                data.setRemark(0.0);

                LocalDate tempDate = startDate;
                while (!tempDate.isAfter(endDate)) {
                    LocalDate finalTempDate = tempDate;
                    double sum = courseFeeList.stream().filter(item -> item.getTeacherId().equals(teacher.getId()) && finalTempDate.isEqual(item.getDate())).mapToDouble(item -> item.getCount().doubleValue()).sum();

                    int sort = DateUtil.dayOfMonth(new DateTime(tempDate));
                    ReflectUtil.setFieldValue(data, "day" + sort, sum);
                    tempDate = tempDate.plusDays(1);
                }
                dataList.add(data);
            }
        }

        return dataList;
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