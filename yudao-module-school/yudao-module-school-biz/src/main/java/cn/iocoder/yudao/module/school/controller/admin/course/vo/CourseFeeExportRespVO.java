package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author yangzy
 */
@Getter
@Setter
@EqualsAndHashCode
public class CourseFeeExportRespVO {

    @ExcelProperty(value = "年级")
    private String gradeName;

    @ExcelProperty(value = "教师")
    private String teacherName;

    @ExcelProperty(value = "汇总")
    private Double summary;

    @ExcelProperty(value = "本月值班")
    private Double onDutyCount;

    @ExcelProperty(value = "备注\n（调课、早读、班主任看自习等）")
    private Double remark;

    @ExcelProperty(value = "3月1号")
    private Double day1;
}
