package cn.iocoder.yudao.module.solver.service.timetable;

import cn.iocoder.yudao.module.solver.dal.dataobject.timetable.TimetableDO;

import java.util.List;

public interface TimetableService {
    List<TimetableDO> getTimetableList();
}
