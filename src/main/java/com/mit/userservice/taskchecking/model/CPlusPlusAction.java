package com.mit.userservice.taskchecking.model;

import com.mit.userservice.taskchecking.repository.SolutionRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import static com.mit.userservice.taskchecking.config.SolutionStatus.*;

public class CPlusPlusAction implements Action {

    String pathToUserFolder;
    String pathToFolderWithProblems;
    String pathToFolderWithUSerSolution;
    String pathToUserFolderCe;
    Solution solution;
    SolutionRepository solutionRepository;
    //    @Value("${tests.max-count}")
    private String TEST_MAX_COUNT;
    //    @Value("${path.user-files}")
    private String PATH_TO_USER_FILE;
    //    @Value("${path.input-files}")
    private String PATH_TO_INPUT;

    @Override
    public void compile() {

        String[] compilation = new String[]{
                "bash",
                "-c",
                "bash compilation.sh " + pathToFolderWithUSerSolution +
                        " 2>" + pathToUserFolderCe
        };

        Process processCompilation;
        try {
            processCompilation = new ProcessBuilder(compilation).start();
            processCompilation.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void runTests() {

        String pathToInputFiles = PATH_TO_INPUT + solution.getProblemId() + "/";

        for (int i = 0; i < Integer.parseInt(TEST_MAX_COUNT); i++) {
            String[] command = new String[]{
                    "bash", "-c",
                    "bash run-tests.sh " + pathToFolderWithUSerSolution + " ./source <"
                            + pathToInputFiles + "input" + (i + 1) + ".txt " +
                            "1>" + pathToFolderWithUSerSolution + "output" + (i + 1) + ".txt 2>"
                            + pathToFolderWithUSerSolution + "output" + (i + 1) + ".txt"
            };


            try {
                Process process = new ProcessBuilder(command).start();
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                solutionRepository.changeSolutionStatus(solution.getId(),
                        RUNTIME_ERROR.value);
                e.printStackTrace();
            }


            String userOutput = parseFile(pathToFolderWithUSerSolution + "output" + (i + 1) + ".txt");
            String trueOutput = parseFile(pathToInputFiles + "output" + (i + 1) + ".txt");

            if (!isTestPassed(trueOutput, userOutput)) {
                solutionRepository.changeSolutionStatus(solution.getId(),
                        WRONG_ANSWER.value + " Test " + (i + 1));
                return;
            }
        }
        solutionRepository.changeSolutionStatus(solution.getId(),
                PROBLEM_SOLVED.value);
    }

    @Override
    public void runChecker(Solution solution, SolutionRepository solutionRepository, String TEST_MAX_COUNT, String PATH_TO_USER_FILE, String PATH_TO_INPUT) {
        this.solution = solution;
        this.solutionRepository = solutionRepository;
        this.TEST_MAX_COUNT = TEST_MAX_COUNT;
        this.PATH_TO_USER_FILE = PATH_TO_USER_FILE;
        this.PATH_TO_INPUT = PATH_TO_INPUT;
        prepareSystemToStartChecker();
        compile();
        String ceInformation = parseFile(pathToUserFolderCe);

        if (ceInformation.isEmpty()) {
            runTests();
        } else {
            solutionRepository.changeSolutionStatus(solution.getId(),
                    COMPILATION_ERROR.value);
        }
    }

    @Override
    public boolean isTestPassed(String expected, String received) {
        return received.equals(expected);
    }

    @Override
    public void prepareSystemToStartChecker() {
        pathToUserFolder = PATH_TO_USER_FILE + solution.getUserId() + "/";
        pathToFolderWithProblems = pathToUserFolder + solution.getProblemId() + "/";
        pathToFolderWithUSerSolution = pathToFolderWithProblems + solution.getId() + "/";
        pathToUserFolderCe = pathToFolderWithUSerSolution + "ce.txt";


        Writer output = null;
        String fileName = "source";
        try {

            new File(pathToUserFolder).mkdir();
            new File(pathToFolderWithProblems).mkdir();
            new File(pathToFolderWithUSerSolution).mkdir();
            File file;

            file = new File(pathToFolderWithUSerSolution + fileName + ".cpp");


            if (file.createNewFile()) {
                System.out.println("File created");
            } else {
                System.out.println("File already exists");
            }

            output = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8));

            output.write(solution.getSolutionText());


        } catch (Exception e) {
            System.out.println("Could not create file");
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
