package cn.iocoder.yudao.module.solver.dal.dataobject.timetable;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 排课DO
 */
@TableName(value = "solver_timetable")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableDO extends BaseDO {
    @TableId
    private Long id;
    private String no;
}