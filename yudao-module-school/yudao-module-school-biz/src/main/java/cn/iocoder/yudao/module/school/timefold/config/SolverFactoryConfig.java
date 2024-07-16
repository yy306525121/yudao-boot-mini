package cn.iocoder.yudao.module.school.timefold.config;

import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import cn.iocoder.yudao.module.school.timefold.domain.Lesson;
import cn.iocoder.yudao.module.school.timefold.domain.TimeTable;
import cn.iocoder.yudao.module.school.timefold.solver.TimeTableConstraintProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class SolverFactoryConfig {

    @Bean
    public SolverFactory<TimeTable> solverFactory() {
        return SolverFactory.create(new SolverConfig()
                .withSolutionClass(TimeTable.class)
                .withEntityClasses(Lesson.class)
                .withConstraintProviderClass(TimeTableConstraintProvider.class)
                .withTerminationSpentLimit(Duration.ofSeconds(60)));
    }
}
