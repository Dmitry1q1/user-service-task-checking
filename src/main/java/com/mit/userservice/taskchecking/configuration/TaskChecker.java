package com.mit.userservice.taskchecking.configuration;

import com.mit.userservice.taskchecking.Solution;
import com.mit.userservice.taskchecking.repository.SolutionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableScheduling
public class TaskChecker {
    @Value("${tests.max-count}")
    private int TEST_MAX_COUNT;
    @Value("${path.user-files}")
    private String PATH_TO_USER_FILE;
    @Value("${path.input-files}")
    private String PATH_TO_INPUT;
    private final SolutionRepository solutionRepository;

    public TaskChecker(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    @Scheduled(fixedDelay = 2000)
    public void scheduleFixedDelayTask() {

        List<Solution> solutions = new ArrayList<>();
        solutions = solutionRepository.getAllSolutions("READY_TO_COMPILE");

        if (solutions.size() > 0) {

            solutionRepository.changeSolutionStatus(solutions.get(0).getId(),
                    "ON_COMPILE");
            String solutionText = solutions.get(0).getSolutionText();

            String pathToUserFolder = PATH_TO_USER_FILE + solutions.get(0).getUserId() + "/";


            String pathToFolderWithProblems = pathToUserFolder + solutions.get(0).getProblemId() + "/";
            String pathToFolderWithUSerSolution = pathToFolderWithProblems + solutions.get(0).getId() + "/";

            String pathToUserFolderCe = pathToFolderWithUSerSolution + "ce.txt";

            Writer output = null;
            try {

                new File(pathToUserFolder).mkdir();
                new File(pathToFolderWithProblems).mkdir();
                new File(pathToFolderWithUSerSolution).mkdir();
                File file = new File(pathToFolderWithUSerSolution + "source.cpp");
                if (file.createNewFile()){
                    System.out.println("File created");
                }
                else{
                    System.out.println("File already exists");
                }

                output = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(file), StandardCharsets.UTF_8));

                output.write(solutionText);



            } catch (Exception e) {
                System.out.println("Could not create file");
            } finally {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            String[] compilation = new String[]{
                    "bash",
                    "-c",
                    "bash compilation.sh " + pathToFolderWithUSerSolution +
                            " 2>" + pathToUserFolderCe
            };

            Process processCompilation = null;
            try {
                processCompilation = new ProcessBuilder(compilation).start();
                processCompilation.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }


            String ceInformation = parseFile(pathToUserFolderCe);
            System.out.println(ceInformation);
            if (ceInformation.isEmpty()) {

                String pathToInputFiles = PATH_TO_INPUT + solutions.get(0).getProblemId() + "/";
                AtomicInteger i = new AtomicInteger(0);
                for (; i.get() < TEST_MAX_COUNT; i.getAndIncrement()) {
                    String[] command = new String[]{
                            "bash", "-c",
                            "bash run-tests.sh " + pathToFolderWithUSerSolution + " ./source <"
                                    + pathToInputFiles + "input" + (i.get() + 1) + ".txt " +
                                    "1>" + pathToFolderWithUSerSolution + "output" + (i.get() + 1) + ".txt 2>"
                                    + pathToFolderWithUSerSolution + "output" + (i.get() + 1) + ".txt"
                    };

                    String userOutput = "";
                    String trueOutput;

                    try {
                        String line;
                        Process process = new ProcessBuilder(command).start();
                        process.waitFor();
                    } catch (IOException | InterruptedException e) {
                        solutionRepository.changeSolutionStatus(solutions.get(0).getId(),
                                "RUNTIME_ERROR");
                        e.printStackTrace();
                    }


                    userOutput = parseFile(pathToFolderWithUSerSolution + "output" + (i.get() + 1) + ".txt");
                    trueOutput = parseFile(pathToInputFiles + "output" + (i.get() + 1) + ".txt");

                    if (!userOutput.equals(trueOutput)) {
                        System.out.println(userOutput);
                        solutionRepository.changeSolutionStatus(solutions.get(0).getId(),
                                "Wrong answer. Test " + (i.get() + 1));
                        i.getAndAdd(Integer.MAX_VALUE);
                    } else {
                        solutionRepository.changeSolutionStatus(solutions.get(0).getId(),
                                "True answer. Test " + (i.get() + 1));
                    }
                }
            } else {
                solutionRepository.changeSolutionStatus(solutions.get(0).getId(),
                        "COMPILATION_ERROR");
            }

        }
    }


    private String parseFile(String path_to_ce_file) {
        String solutionText = "";

        StringBuilder sb = new StringBuilder();
        try {
            Scanner in = new Scanner(new FileReader(path_to_ce_file));

            while(in.hasNext()) {
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
