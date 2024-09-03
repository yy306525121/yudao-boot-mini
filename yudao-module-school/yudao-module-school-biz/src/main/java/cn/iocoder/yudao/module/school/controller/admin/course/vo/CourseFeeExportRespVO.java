package cn.iocoder.yudao.module.school.controller.admin.course.vo;

import cn.iocoder.yudao.module.school.framework.poi.annotation.Excel;
import lombok.*;

/**
 * @author yangzy
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CourseFeeExportRespVO {

    @Excel(name = "年级")
    private String gradeName;

    @Excel(name = "教师")
    private String teacherName;

}
