package cn.iocoder.yudao.module.school.dal.dataobject.subject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教师和科目关联表
 * @author yangzy
 */
@TableName("school_teacher_subject")
@Data
@EqualsAndHashCode(callSuper = true)
public class TeacherSubjectDO extends TenantBaseDO {
    @TableId
    private Long id;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 科目ID
     */
    private Long subjectId;
}
