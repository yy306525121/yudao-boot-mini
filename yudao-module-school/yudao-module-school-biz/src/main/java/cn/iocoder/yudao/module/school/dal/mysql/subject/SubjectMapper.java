package cn.iocoder.yudao.module.school.dal.mysql.subject;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.suject.vo.SubjectPageReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.subject.SubjectDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 科目信息 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface SubjectMapper extends BaseMapperX<SubjectDO> {

    default List<SubjectDO> selectListByIds(Collection<Long> subjectIds) {
        return selectList(SubjectDO::getId, subjectIds);
    }

    default PageResult<SubjectDO> selectPage(SubjectPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SubjectDO>()
                .likeIfPresent(SubjectDO::getName, reqVO.getName())
                .orderByAsc(SubjectDO::getSort));
    }

    default List<SubjectDO> selectList(Collection<Long> ids) {
        return selectList(new LambdaQueryWrapperX<SubjectDO>()
                .inIfPresent(SubjectDO::getId, ids));
    }
}
