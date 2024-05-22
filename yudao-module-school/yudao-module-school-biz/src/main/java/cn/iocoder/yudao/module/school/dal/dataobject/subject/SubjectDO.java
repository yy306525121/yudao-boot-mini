package cn.iocoder.yudao.module.school.dal.dataobject.subject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 科目表
 * @author yangzy
 */
@TableName("school_subject")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDO extends TenantBaseDO {
    @TableId
    private Long id;

    /**
     * 科目名称
     */
    private String name;

    /**
     * 显示排序
     */
    private Integer sort;

}
