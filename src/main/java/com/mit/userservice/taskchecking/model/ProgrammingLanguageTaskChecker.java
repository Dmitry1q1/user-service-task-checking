package com.mit.userservice.taskchecking.model;

import com.mit.userservice.taskchecking.repository.SolutionRepository;

public abstract class ProgrammingLanguageTaskChecker {

    public void runTaskChecker(Solution solution, SolutionRepository solutionRepository, String TEST_MAX_COUNT, String PATH_TO_USER_FILE, String PATH_TO_INPUT) {
        Action action = createAction();
        action.runChecker(solution, solutionRepository, TEST_MAX_COUNT, PATH_TO_USER_FILE, PATH_TO_INPUT);
    }

    public abstract Action createAction();
}
