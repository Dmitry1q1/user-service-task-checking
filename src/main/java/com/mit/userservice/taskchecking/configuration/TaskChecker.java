package com.mit.userservice.taskchecking.configuration;

import com.mit.userservice.taskchecking.Solution;
import com.mit.userservice.taskchecking.repository.SolutionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Configuration
@EnableScheduling
public class TaskChecker {
    @Value("${tests.max-count}")
    private int TEST_MAX_COUNT;
    //    private static final String PATH_TO_CE_FILE = "/home/dmitry/Public/user-service/solutions/1/ce.txt";
    private static final String PATH_TO_USER_FILE = "/home/dmitry/Public/user-service/solutions/";
    private static final String PATH_TO_INPUT = "/home/dmitry/Public/user-service/problems/";
    private final SolutionRepository solutionRepository;

    public TaskChecker(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    //    @Scheduled()
    @Scheduled(fixedDelay = 2000)
    public void scheduleFixedDelayTask() {
        System.out.println(
                "Fixed delay task - " + LocalDateTime.now());
        List<Solution> solutions = new ArrayList<>();
        solutions = solutionRepository.getAllSolutions("READY_TO_COMPILE");

        solutionRepository.changeSolutionStatus(solutions.get(0).getId(),
                "ON_COMPILE");
        String solutionText = solutions.get(0).getSolutionText();

        String pathToUserFolder = PATH_TO_USER_FILE + solutions.get(0).getUserId() + "/";
        String pathToUserFolderCe = PATH_TO_USER_FILE + solutions.get(0).getUserId() + "/ce.txt";

        String[] fileToDelete = new String[]{
                "bash",
                "-c",
                "bash delete-files.sh " + pathToUserFolder +
                        " 2>" + pathToUserFolderCe
        };

        try {
            Process process = new ProcessBuilder(fileToDelete).start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        Writer output = null;
        try {

            new File(pathToUserFolder).mkdir();
            File file = new File(pathToUserFolder + "source.cpp");
            if (file.createNewFile())
                System.out.println("File created");
            else
                System.out.println("File already exists");
            output = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8));

            output.write(solutionText);


            System.out.println("File has been written");

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
                "bash compilation.sh " + pathToUserFolder +
                        " 2>" + pathToUserFolderCe
        };

        Process processCompilation = null;
        try {
            processCompilation = new ProcessBuilder(compilation).start();
            processCompilation.waitFor();
//            processCompilation.pid();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


        File folder = new File(pathToUserFolder);

        File[] files;
        boolean isFileInDirectory = false;
        while (!isFileInDirectory) {
            try {
                files = folder.listFiles();
                if (files != null) {
                    for (File fileInDirectory : files) {
                        if (fileInDirectory.getName().equals("ce.txt")) {
                            isFileInDirectory = true;
                        }
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

        String ceInformation = parseFile(pathToUserFolderCe);
        if (ceInformation.isEmpty()) {

            String pathToInputFiles = PATH_TO_INPUT + solutions.get(0).getProblemId() + "/";
            for (int i = 0; i < 4; i++) {
                String[] command = new String[]{
                        "bash", "-c",
                        "bash run-tests.sh " + pathToUserFolder + " ./source <"
                                + pathToInputFiles + "input" + (i + 1) + ".txt " +
                                "1>" + pathToUserFolder + "output" + (i + 1) + ".txt 2>"
                                + pathToUserFolder + "output" + (i + 1) + ".txt"
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

                System.out.println(userOutput);
                while (userOutput.equals("")) {
                    userOutput = parseFile(pathToUserFolder + "output" + (i + 1) + ".txt");
                }
                trueOutput = parseFile(pathToInputFiles + "output" + (i + 1) + ".txt");

                if (!userOutput.equals(trueOutput)) {
                    System.out.println(userOutput);
                    solutionRepository.changeSolutionStatus(solutions.get(0).getId(),
                            "Wrong answer. Test " + (i + 1));
                    i = Integer.MAX_VALUE;
                } else {
                    solutionRepository.changeSolutionStatus(solutions.get(0).getId(),
                            "True answer. Test " + (i + 1));
                }
                System.out.println(userOutput);
            }
        } else {
            solutionRepository.changeSolutionStatus(solutions.get(0).getId(),
                    "COMPILATION_ERROR");
        }
    }


    private String parseFile(String path_to_ce_file) {
        String solutionText = "";
        try (Scanner scanner = new Scanner(
                Path.of(path_to_ce_file)).useDelimiter("\\Z")) {
            if (scanner.hasNext()) {
                solutionText = scanner.next();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return solutionText;
    }
}
