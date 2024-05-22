package cn.iocoder.yudao.module.school.dal.dataobject.teacher;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.jackson.core.databind.BigDecimalSerializable;
import cn.iocoder.yudao.framework.mybatis.core.type.JsonLongSetTypeHandler;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

/**
 * 教师表
 */
@TableName(value = "school_teacher", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDO extends TenantBaseDO {

    @TableId
    private Long id;

    private String name;

    /**
     * 状态
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

    private Integer sort;

    @TableField(typeHandler = JsonLongSetTypeHandler.class)
    private Set<Long> subjectIds;

    /**
     * 基础工资
     */
    @JsonSerialize(using = BigDecimalSerializable.class)
    private BigDecimal basicSalary;

    /**
     * 课时费单价
     */
    @JsonSerialize(using = BigDecimalSerializable.class)
    private BigDecimal courseSalary;
}
