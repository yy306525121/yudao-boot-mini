package cn.iocoder.yudao.module.school.framework.poi.annotation;

import java.lang.annotation.*;

/**
 * @author yangzy
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Excel {
    /**
     * 导出到excel中的标题名
     * @return 标题名
     */
    String name() default "";

    /**
     * 是否汇总行
     */
    boolean isSummary() default false;

    String summaryExpr() default "";

    /**
     * 是否是日期格式
     */
    boolean isDateTitle() default false;

    /**
     * 日期标题的格式化
     */
    String dateTitleFormat() default "MM月dd日";

}
