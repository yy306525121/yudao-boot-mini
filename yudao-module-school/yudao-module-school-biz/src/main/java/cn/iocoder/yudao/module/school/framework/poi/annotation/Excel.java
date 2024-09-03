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


}
