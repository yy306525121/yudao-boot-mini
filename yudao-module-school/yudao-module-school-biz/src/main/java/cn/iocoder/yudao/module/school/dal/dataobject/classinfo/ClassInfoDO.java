package cn.iocoder.yudao.module.school.dal.dataobject.classinfo;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 班级 DO
 *
 * @author yangzy
 */
@TableName("school_class_info")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassInfoDO extends BaseDO {

    public static final Long PARENT_ID_ROOT = 0L;

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 班级名称
     */
    private String name;
    /**
     * 所属年级编号
     */
    private Long parentId;
    /**
     * 显示排序
     */
    private Integer sort;

}