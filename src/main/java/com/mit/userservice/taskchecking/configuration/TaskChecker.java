package com.mit.userservice.taskchecking.configuration;

import com.mit.userservice.taskchecking.model.CPlusPlusTaskChecker;
import com.mit.userservice.taskchecking.model.JavaTaskChecker;
import com.mit.userservice.taskchecking.model.ProgrammingLanguageTaskChecker;
import com.mit.userservice.taskchecking.model.Solution;
import com.mit.userservice.taskchecking.repository.SolutionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

import static com.mit.userservice.taskchecking.config.SolutionStatus.ON_COMPILE;
import static com.mit.userservice.taskchecking.config.SolutionStatus.READY_TO_COMPILE;

@Configuration
@EnableScheduling
public class TaskChecker {

    private final SolutionRepository solutionRepository;
    @Value("${tests.max-count}")
    private String TEST_MAX_COUNT;
    @Value("${path.user-files}")
    private String PATH_TO_USER_FILE;
    @Value("${path.input-files}")
    private String PATH_TO_INPUT;
    private ProgrammingLanguageTaskChecker programmingLanguageTaskChecker;

    public TaskChecker(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    @Scheduled(fixedDelay = 2000)
    public void scheduleFixedDelayTask() {

        List<Solution> solutions;
        solutions = solutionRepository.getAllSolutions(READY_TO_COMPILE.value);

        if (solutions.size() > 0) {

            Solution solution = solutions.get(0);
            solutionRepository.changeSolutionStatus(solution.getId(),
                    ON_COMPILE.value);

            configure(solution);
            runTaskChecker(solution);
        }
    }

    private void runTaskChecker(Solution solution) {
        programmingLanguageTaskChecker.runTaskChecker(solution, solutionRepository, TEST_MAX_COUNT, PATH_TO_USER_FILE, PATH_TO_INPUT);
    }

    private void configure(Solution solution) {
        switch ((int) solution.getProgrammingLanguageId()) {
            case 1:
                programmingLanguageTaskChecker = new CPlusPlusTaskChecker();
                break;
            case 2:
                programmingLanguageTaskChecker = new JavaTaskChecker();
                break;
        }
    }

}
