package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import cn.iocoder.yudao.module.school.framework.poi.annotation.Excel;
import lombok.*;

/**
 * 课时费详情 sheet 导出dto
 * @author yangzy
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CourseFeeExportDetailRespVO {

    @Excel(name = "年级")
    private String gradeName;

    @Excel(name = "教师")
    private String teacherName;

    @Excel(name = "汇总", isSummary = true)
    private Double summary;

    @Excel(name = "本月值班")
    private Double onDuty;

    @Excel(name = "备注\n（调课、早读、班主任看自习等）")
    private Double remark;

    @Excel(isDateTitle = true)
    private Double day1;

    @Excel(isDateTitle = true)
    private Double day2;

    @Excel(isDateTitle = true)
    private Double day3;

    @Excel(isDateTitle = true)
    private Double day4;

    @Excel(isDateTitle = true)
    private Double day5;

    @Excel(isDateTitle = true)
    private Double day6;

    @Excel(isDateTitle = true)
    private Double day7;

    @Excel(isDateTitle = true)
    private Double day8;

    @Excel(isDateTitle = true)
    private Double day9;

    @Excel(isDateTitle = true)
    private Double day10;

    @Excel(isDateTitle = true)
    private Double day11;

    @Excel(isDateTitle = true)
    private Double day12;

    @Excel(isDateTitle = true)
    private Double day13;

    @Excel(isDateTitle = true)
    private Double day14;

    @Excel(isDateTitle = true)
    private Double day15;

    @Excel(isDateTitle = true)
    private Double day16;

    @Excel(isDateTitle = true)
    private Double day17;

    @Excel(isDateTitle = true)
    private Double day18;

    @Excel(isDateTitle = true)
    private Double day19;

    @Excel(isDateTitle = true)
    private Double day20;

    @Excel(isDateTitle = true)
    private Double day21;

    @Excel(isDateTitle = true)
    private Double day22;

    @Excel(isDateTitle = true)
    private Double day23;

    @Excel(isDateTitle = true)
    private Double day24;

    @Excel(isDateTitle = true)
    private Double day25;

    @Excel(isDateTitle = true)
    private Double day26;

    @Excel(isDateTitle = true)
    private Double day27;

    @Excel(isDateTitle = true)
    private Double day28;

    @Excel(isDateTitle = true)
    private Double day29;

    @Excel(isDateTitle = true)
    private Double day30;

    @Excel(isDateTitle = true)
    private Double day31;
}
