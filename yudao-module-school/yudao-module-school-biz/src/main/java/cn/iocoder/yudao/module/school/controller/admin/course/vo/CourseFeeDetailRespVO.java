package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import cn.iocoder.yudao.module.school.controller.admin.grade.vo.GradeSimpleRespVO;
import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectSimpleRespVO;
import cn.iocoder.yudao.module.school.controller.admin.teacher.vo.TeacherSimpleRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Schema(description = "管理后台 - 课时费明细 Response VO")
@Data
@ToString(callSuper = true)
public class CourseFeeDetailRespVO {
    private TeacherSimpleRespVO teacher;

    private GradeSimpleRespVO grade;

    private SubjectSimpleRespVO subject;

    private Integer week;

    private TimeSlotRespVO timeSlot;

    private LocalDate date;
}
