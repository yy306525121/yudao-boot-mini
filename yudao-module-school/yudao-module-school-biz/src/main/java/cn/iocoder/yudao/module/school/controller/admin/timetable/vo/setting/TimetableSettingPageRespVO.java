package cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting;

import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 排课计划设置 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TimetableSettingPageRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "14252")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "班级", example = "18382")
    private GradeDO grade;

    @Schema(description = "科目", example = "914")
    private SubjectDO subject;

    @Schema(description = "教师", example = "22321")
    private TeacherDO teacher;

    @Schema(description = "课程类型", example = "22321")
    private CourseTypeDO courseType;

    @Schema(description = "普通课时数")
    @ExcelProperty("普通课时数")
    private Integer ordinaryCount;

    @Schema(description = "连堂课次数")
    @ExcelProperty("连堂课次数")
    private Integer continuousCount;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
