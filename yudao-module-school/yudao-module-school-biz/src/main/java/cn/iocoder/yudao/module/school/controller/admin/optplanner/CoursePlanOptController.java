package cn.iocoder.yudao.module.school.controller.admin.optplanner;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.school.service.course.CoursePlanOptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 排课")
@RestController
@RequestMapping("/school/course-plan-opt")
@Validated
@RequiredArgsConstructor
public class CoursePlanOptController {

    private final CoursePlanOptService coursePlanOptService;

    @GetMapping("/opt")
    @Operation(summary = "排课")
    public CommonResult<Boolean> createCoursePlan() {
        coursePlanOptService.courseScheduling();
        return success(true);
    }

}
