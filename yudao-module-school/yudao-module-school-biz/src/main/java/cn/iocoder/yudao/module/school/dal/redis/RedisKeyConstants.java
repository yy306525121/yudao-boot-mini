package cn.iocoder.yudao.module.school.dal.redis;

/**
 * School Redis Key 枚举类
 * @author yangzy
 */
public interface RedisKeyConstants {
    /**
     * 指定年级的所有子年级编号数组的缓存
     * <p>
     * KEY 格式：grade_children_ids:{id}
     * VALUE 数据类型：String 子年级编号集合
     */
    String GRADE_CHILDREN_ID_LIST = "grade_children_ids";

    /**
     * 年级树的缓存
     */
    String GRADE_TREE_LIST = "grade_tree";
}
