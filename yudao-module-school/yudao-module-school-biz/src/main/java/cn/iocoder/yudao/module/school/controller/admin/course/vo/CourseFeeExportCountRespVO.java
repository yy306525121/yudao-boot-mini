package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import cn.iocoder.yudao.module.school.framework.poi.annotation.Excel;
import lombok.*;

import java.util.List;

/**
 * 课时费统计Sheet 导出dto
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CourseFeeExportCountRespVO {

    /**
     * 年级
     */
    @Excel(name = "年级")
    private String gradeName;

    @Excel(name = "姓名")
    private String teacherName;

    @Excel(name = "汇总", isSummary = true)
    private Double summary;

    /**
     * 汇总需要计算的另一个sheet页的行号
     */
    private List<Integer> summaryIndexList;

    @Excel(name = "应发课时")
    private Double expect;

    @Excel(name = "实发课时")
    private Double actual;
}
