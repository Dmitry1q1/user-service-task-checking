package com.mit.userservice.taskchecking.model;

import com.mit.userservice.taskchecking.repository.SolutionRepository;
import com.mit.userservice.taskchecking.repository.TestRepository;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public interface Action {
    void compile();

    void runTests();

    void runChecker(Solution solution, SolutionRepository solutionRepository, TestRepository testRepository, String PATH_TO_USER_FILE, String PATH_TO_INPUT);

    boolean isTestPassed(String expected, String received);

    void prepareSystemToStartChecker();

    default String parseFile(String path_to_ce_file) {
        String solutionText = "";

        StringBuilder sb = new StringBuilder();
        try {
            Scanner in = new Scanner(new FileReader(path_to_ce_file));

            while (in.hasNext()) {
                sb.append(in.next());
            }
            in.close();
            solutionText = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return solutionText;
    }
}
