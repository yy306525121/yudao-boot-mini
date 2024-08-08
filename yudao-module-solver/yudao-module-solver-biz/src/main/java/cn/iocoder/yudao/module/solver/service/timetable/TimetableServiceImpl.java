package cn.iocoder.yudao.module.solver.service.timetable;

import cn.iocoder.yudao.module.solver.dal.dataobject.timetable.TimetableDO;
import cn.iocoder.yudao.module.solver.dal.mysql.timetable.TimetableMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 排课 Service 实现类
 */
@Service
@Validated
@RequiredArgsConstructor
public class TimetableServiceImpl implements TimetableService {
    private final TimetableMapper timetableMapper;

    @Override
    public List<TimetableDO> getTimetableList() {
        return timetableMapper.selectList();
    }
}
