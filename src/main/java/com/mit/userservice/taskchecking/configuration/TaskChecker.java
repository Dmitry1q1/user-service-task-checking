package com.mit.userservice.taskchecking.configuration;

import com.mit.userservice.taskchecking.Solution;
import com.mit.userservice.taskchecking.repository.SolutionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
public class TaskChecker {
    @Value("${tests.max-count}")
    private int TEST_MAX_COUNT;
    private final SolutionRepository solutionRepository;

    public TaskChecker(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    @Scheduled(fixedDelay = 2000)
    public void scheduleFixedDelayTask() {
        System.out.println(
                "Fixed delay task - " + LocalDateTime.now());
        List<Solution> solutions = new ArrayList<>();
        solutions = solutionRepository.getAllSolutions();
        int newSolutionsCount = solutions.size();
        for (int i = 0; i < TEST_MAX_COUNT; i++) {
            System.out.println(solutions.get(i).getSolutionText());
        }
    }
}
