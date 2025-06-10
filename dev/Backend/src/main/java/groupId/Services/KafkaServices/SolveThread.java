package groupId.Services.KafkaServices;

import Exceptions.SolverExceptions.SolverException;
import Exceptions.SolverExceptions.ValidationException;
import Model.Solution;
import groupId.DTO.Records.Events.SolveRequest;
import groupId.Services.SolveService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SolveThread extends Thread {

    private final SolveService solveService;
    @NonNull
    private final BlockingQueue<SolveRequest> requestQueue;
    private volatile boolean running = true;

    //@Value("${app.file.storage-dir}")
    private final String baseStorageDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath() + "/User/Models";
    private static final int MAX_TIMEOUT_SECONDS = 30;

    @Override
    public void run() {
        while (running) {
            try {
                SolveRequest request = requestQueue.take();
                handleSolveRequest(request);
            } catch (InterruptedException e) {
                log.warn("Solver thread interrupted", e);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    public SolveThread(SolveService solveService) {
        this.solveService = solveService;
        this.requestQueue = new LinkedBlockingQueue<>();
        this.start();
    }

    public void submitRequest(@NonNull SolveRequest request) {
        requestQueue.offer(request);
    }

    public void shutdown() {
        running = false;
        interrupt();
    }

    public void handleSolveRequest(@NonNull SolveRequest request) {
        Path codeFile= null;
        try {
            log.info("---------------------------Threaded-------------------------------");
            log.info("Received solve request: {}", request.requestId());
            codeFile = createCodeFile(request);
            validateZimplCode(codeFile);
            if(request.validationOnly()){
                log.info("Validation only, returning solution.");
                solveService.completeSolution(request.requestId(), null);
            }
            else {
                Solution solution = solveProblem(request, codeFile);
                log.info("Solution found: {}", solution);
                solveService.completeSolution(request.requestId(), solution);
                log.info("Completed solve request: {}", request.requestId());
            }
        } catch (Exception e) {
            log.info("Caught exception of type: {}",e.getClass());
            log.error("Error while solving: {}", e.getMessage());
            solveService.completeWithError(request.requestId(), e);
        }
        finally {
                cleanupFile(codeFile);
        }
    }



    @NonNull
    private Solution solveProblem(@NonNull SolveRequest request, @NonNull Path codeFile) {
        Process scipProcess = null;
        try {
            log.info("Got path: {}", codeFile.toAbsolutePath());
            int timeout = Math.min(request.timeoutSeconds(), MAX_TIMEOUT_SECONDS);
            ProcessBuilder processBuilder = new ProcessBuilder("scip", "-c",
                    "read " + codeFile + " optimize display solution quit");
            processBuilder.directory(codeFile.getParent().toFile());
            processBuilder.redirectErrorStream(true);
            //processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD);
            log.info("Executing command: {}", processBuilder.command());
            scipProcess = processBuilder.start();
            //handleProcessOutput(scipProcess);

            //StringBuilder output = new StringBuilder();
            Solution solution= new Solution();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(scipProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    solution.processLine(line);
                }
            }

            boolean completed = scipProcess.waitFor(timeout, TimeUnit.SECONDS);
            //String outputStr = new String(scipProcess.getInputStream().readAllBytes());
            /*String outputStr = output.toString();
            outputStr = startFromStatus(outputStr);
            String prunedOutput = outputStr.lines()
                    .filter(line -> !line.startsWith("@@"))
                    .collect(Collectors.joining("\n"));
            if (!completed) {
                if(!prunedOutput.contains("SCIP Status")) {
                    log.info("SCIP process timed out, and no solution was found. Output: \n{}", prunedOutput);
                    throw new SolverException(prunedOutput);
                }
                else
                    log.info("SCIP process timed out, but solution was found");
            }*/
            if(!completed) {
                if (solution.isSolved())
                    log.info("SCIP process timed out, but solution was found");
                else
                    log.info("SCIP process timed out, and no solution was found.");
            }
            else {
                if (solution.isSolved())
                    log.info("SCIP process completed successfully, and solution was found");
                else
                    log.info("SCIP process completed successfully, but no solution was found.");
            }

            if (scipProcess.exitValue() != 0) {
                throw new SolverException("SCIP solver execution failed with exit code: " + scipProcess.exitValue());
            }
            log.info("Found solution: {}",solution);
            return solution;

        } catch (InterruptedException | IOException e) {
            throw new SolverException("Error during SCIP execution: " + e.getMessage());
        } finally {
            if (scipProcess != null && scipProcess.isAlive()) {
                scipProcess.destroyForcibly();
            }
        }
    }


    @NonNull
    private Path createCodeFile(@Nullable SolveRequest request) throws IOException {
        if(request == null){
            log.error("Null request while creating code file");
            throw new SolverException("Null request while solving");
        }

        String sessionId = UUID.randomUUID().toString();
        Path directory = Paths.get(baseStorageDir);
        Files.createDirectories(directory);
        return Files.writeString(
                directory.resolve("session_" + sessionId + ".zpl"),
                request.zimplContent(),
                StandardOpenOption.CREATE_NEW
        );


    }
    private void validateZimplCode(@NonNull Path codeFile) throws SolverException {
        log.info("Validating code file: {}", codeFile.toAbsolutePath());
        Process zimplProcess = null;
        try{
            ProcessBuilder processBuilder = new ProcessBuilder("zimpl",
                    "-v","0",
                    "-o", "/dev/null",
                    codeFile.toAbsolutePath().toString());
            processBuilder.directory(codeFile.getParent().toFile());
            processBuilder.redirectErrorStream(true);
            zimplProcess = processBuilder.start();
            boolean completed = zimplProcess.waitFor(5, TimeUnit.SECONDS);
            if (!completed) {
                log.info("Code file validation timed out");
                throw new SolverException("Zimpl validation timed out");
            }
            if (zimplProcess.exitValue() != 0) {
                log.info("Code validation failed with exit code: {}", zimplProcess.exitValue());
                throw new ValidationException("Error while validating code:\n" + new String(zimplProcess.getInputStream().readAllBytes()));
            }
            log.info("Code file successfully validated.");

        }
        catch (Exception e){
            throw new SolverException("Error while validating code: " + e.getMessage());
        }
        finally {
            if (zimplProcess != null && zimplProcess.isAlive()) {
                zimplProcess.destroyForcibly();
            }
        }
    }
    private void cleanupFile(@Nullable Path workDir) {
        if (workDir == null) {
            log.warn("Null path while cleaning up work directory");
            return;
        }

        try {
            boolean deleted = Files.deleteIfExists(workDir);
            if (!deleted) {
                log.warn("File {} was already deleted or doesn't exist", workDir);
            }
            log.info("Code file cleanup successful: {} ", workDir.toAbsolutePath());
        } catch (IOException e) {
            log.warn("Failed to cleanup code session file: {} ({})", workDir, e.getMessage(), e);
        }

    }

    @NonNull
    private static String startFromStatus(@NonNull String original) {
        String from = "SCIP Status";
        if(original.contains(from))
            return original.substring(original.indexOf(from));
        else return original;
    }
    @Deprecated(forRemoval = true)
    private @Nullable Solution solveProblemExecutor(SolveRequest request, Path codeFile) {
        /*Process scipProcess = null;
        int timeout = Math.min(request.timeoutSeconds(), MAX_TIMEOUT_SECONDS);
        try {
            log.info("Got path: {}", codeFile.toAbsolutePath());
            ProcessBuilder processBuilder = new ProcessBuilder("scip", "-c",
                    "read " + codeFile + " optimize display solution quit");

            processBuilder.directory(codeFile.getParent().toFile());
            processBuilder.redirectErrorStream(true);
            log.info("Executing command: {}\n timeout set to: {}", processBuilder.command(), timeout);
            scipProcess = processBuilder.start();
            //SCIP process doesn't seem to exit on completion, ever. A bad but working solution
            //is capture the solution during runtime.
            BufferedReader reader = new BufferedReader(new InputStreamReader(scipProcess.getInputStream()));
            StringBuilder solution = new StringBuilder();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            try {
                Future<?> future = executor.submit(() -> {
                    try {
                        boolean foundSolution = false;
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains("SCIP Status")) {
                                foundSolution = true;
                            }
                            if (foundSolution) {
                                if (!line.startsWith("@@"))
                                    solution.append(line).append("\n");
                            }
                        }
                    } catch (IOException e) {
                        log.error("Error reading process output", e);
                    }
                });

                // Wait for either completion or timeout
                try {
                    future.get(timeout, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    future.cancel(true);
                    throw new SolverException("Solver timed out after " + timeout + " seconds");
                } catch (InterruptedException | ExecutionException e) {
                    throw new SolverException("Error during solution reading: " + e.getMessage());
                }
            } finally {
                executor.shutdownNow();
            }

            if (!solution.isEmpty()) {
                scipProcess.destroyForcibly();
                log.info("Solution captured, process terminated");
                String solutionStr = solution.toString();
                log.info("\n-------------------OUTPUT----------------\n{}\n-------------------END--------------------\n", solutionStr);
                return new Solution(solutionStr);
            } else {
                throw new SolverException("Failed to capture solution");
            }
        } catch (IOException e) {
            throw new SolverException("Error during SCIP execution: " + e.getMessage());
        } finally {
            if (scipProcess != null && scipProcess.isAlive()) {
                scipProcess.destroyForcibly();
            }
        }*/
        return null;
    }
    //DEBUG METHOD
    private void handleProcessOutput(@NonNull Process process) {
        // Handle stdout
        Thread outputReader = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("stdout: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Handle stderr
        Thread errorReader = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println("stderr: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        outputReader.setDaemon(true);
        errorReader.setDaemon(true);
        outputReader.start();
        errorReader.start();
    }



}