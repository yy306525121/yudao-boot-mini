package cn.iocoder.yudao.module.school.controller.admin.rule.vo;

import cn.iocoder.yudao.module.school.controller.admin.course.vo.CourseTypeRespVO;
import cn.iocoder.yudao.module.school.controller.admin.course.vo.TimeSlotRespVO;
import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeRespVO;
import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectRespVO;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherRespVO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - 临时调课 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TransferRulePageRespVO {
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7381")
    private Long id;

    @Schema(description = "调课时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;

    @Schema(description = "调课班级", requiredMode = Schema.RequiredMode.REQUIRED)
    private GradeRespVO grade;

    @Schema(description = "调课节次", requiredMode = Schema.RequiredMode.REQUIRED)
    private TimeSlotRespVO timeSlot;

    @Schema(description = "调课前教师", requiredMode = Schema.RequiredMode.REQUIRED)
    private TeacherRespVO fromTeacher;

    @Schema(description = "调课后教师", requiredMode = Schema.RequiredMode.REQUIRED)
    private TeacherRespVO toTeacher;

    @Schema(description = "调课前课程", requiredMode = Schema.RequiredMode.REQUIRED)
    private SubjectRespVO fromSubject;

    @Schema(description = "调课后课程", requiredMode = Schema.RequiredMode.REQUIRED)
    private SubjectRespVO toSubject;

    @Schema(description = "调课前课程类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private CourseTypeRespVO fromCourseType;

    @Schema(description = "调课后课程类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private CourseTypeRespVO toCourseType;
}
