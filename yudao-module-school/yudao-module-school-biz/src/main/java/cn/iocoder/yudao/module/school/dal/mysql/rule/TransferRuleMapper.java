package cn.iocoder.yudao.module.school.dal.mysql.rule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.school.controller.admin.rule.vo.TransferRulePageReqVO;
import cn.iocoder.yudao.module.school.dal.dataobject.rule.TransferRuleDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 临时调课 Mapper
 *
 * @author yangzy
 */
@Mapper
public interface TransferRuleMapper extends BaseMapperX<TransferRuleDO> {

    default PageResult<TransferRuleDO> selectPage(TransferRulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TransferRuleDO>()
                .orderByDesc(TransferRuleDO::getId));
    }

}