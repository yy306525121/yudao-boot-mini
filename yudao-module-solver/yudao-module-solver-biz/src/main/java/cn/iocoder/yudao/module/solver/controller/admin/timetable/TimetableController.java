package cn.iocoder.yudao.module.solver.controller.admin.timetable;

import cn.iocoder.yudao.module.solver.service.timetable.TimetableService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理后台 - 课时费明细")
@RestController
@RequestMapping("/solver/timetable")
@Validated
@RequiredArgsConstructor
public class TimetableController {
    private final TimetableService timetableService;
}
