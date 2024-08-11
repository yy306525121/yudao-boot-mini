package cn.iocoder.yudao.module.school.controller.admin.timetable.vo.result;

import cn.iocoder.yudao.module.school.dal.dataobject.course.CourseTypeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.course.TimeSlotDO;
import cn.iocoder.yudao.module.school.dal.dataobject.grade.GradeDO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import cn.iocoder.yudao.module.school.dal.dataobject.teacher.TeacherDO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 排课结果 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TimetableResultRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7092")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "排课编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "7092")
    private Long timetableId;

    @Schema(description = "星期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("星期")
    private Integer week;

    @Schema(description = "班级", requiredMode = Schema.RequiredMode.REQUIRED)
    private GradeDO grade;

    @Schema(description = "教师", requiredMode = Schema.RequiredMode.REQUIRED)
    private TeacherDO teacher;

    @Schema(description = "科目", requiredMode = Schema.RequiredMode.REQUIRED)
    private SubjectDO subject;

    @Schema(description = "节次", requiredMode = Schema.RequiredMode.REQUIRED)
    private TimeSlotDO timeSlot;

    @Schema(description = "课程类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private CourseTypeDO courseType;

}