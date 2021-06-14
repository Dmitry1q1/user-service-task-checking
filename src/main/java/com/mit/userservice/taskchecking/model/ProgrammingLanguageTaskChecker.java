package com.mit.userservice.taskchecking.model;

import com.mit.userservice.taskchecking.repository.SolutionRepository;
import com.mit.userservice.taskchecking.repository.TestRepository;

public abstract class ProgrammingLanguageTaskChecker {

    public void runTaskChecker(Solution solution, SolutionRepository solutionRepository, TestRepository testRepository, String PATH_TO_USER_FILE, String PATH_TO_INPUT) {
        Action action = createAction();
        action.runChecker(solution, solutionRepository, testRepository, PATH_TO_USER_FILE, PATH_TO_INPUT);
    }

    public abstract Action createAction();
}
