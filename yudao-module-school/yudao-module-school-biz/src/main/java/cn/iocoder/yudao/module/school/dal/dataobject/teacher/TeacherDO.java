package cn.iocoder.yudao.module.school.dal.dataobject.teacher;

import cn.iocoder.yudao.framework.jackson.core.databind.BigDecimalSerializable;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@TableName("school_teacher")
@Data
@EqualsAndHashCode(callSuper = true)
public class TeacherDO extends TenantBaseDO {

    @TableId
    private Long id;

    private String name;

    /**
     * 0-在职
     * 1-离职
     */
    private Integer status;

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
