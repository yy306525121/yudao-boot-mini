package cn.iocoder.yudao.module.school.convert.timetable;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.TimetableRespVO;
import cn.iocoder.yudao.module.school.controller.admin.timetable.vo.setting.TimetableSettingPageRespVO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableDO;
import cn.iocoder.yudao.module.school.dal.dataobject.timetable.TimetableSettingDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangzy
 */
@Mapper
public interface TimetableConvert {
    TimetableConvert INSTANCE = Mappers.getMapper(TimetableConvert.class);

    TimetableRespVO convert(TimetableDO timetable);


    default PageResult<TimetableRespVO> convertPage(PageResult<TimetableDO> page,
                                                    RedisTemplate<String, Object> redisTemplate,
                                                    String runningJobKey) {
        PageResult<TimetableRespVO> pageResult = new PageResult<>();
        List<TimetableRespVO> pageList = new ArrayList<>();

        List<TimetableDO> list = page.getList();
        for (TimetableDO timetable : list) {
            String key = String.format(runningJobKey, timetable.getId());

            TimetableRespVO respVO = convert(timetable);

            if (Boolean.TRUE.equals(redisTemplate.hasKey(key)) && Boolean.TRUE.equals(redisTemplate.opsForValue().get(key))) {
                respVO.setRunning(Boolean.TRUE);
            } else {
                respVO.setRunning(Boolean.FALSE);
            }

            pageList.add(respVO);
        }
        pageResult.setList(pageList);
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }
}
