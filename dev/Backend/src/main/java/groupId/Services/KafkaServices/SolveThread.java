package groupId.Services.KafkaServices;

import Exceptions.SolverExceptions.SolverException;
import Exceptions.SolverExceptions.ValidationException;
import Model.Solution;
import groupId.DTO.Records.Events.SolveRequest;
import groupId.Services.SolveService;
import org.jetbrains.annotations.Nullable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.Closeable;
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
public class SolveThread extends Thread {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SolveThread.class);

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
            log.info("---------------------------Solver v3-------------------------------");
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
            log.info("Caught exception of structure: {}",e.getClass());
            log.error("Error while solving: {}", e.getMessage());
            solveService.completeWithError(request.requestId(), e);
        }
        finally {
                cleanupFile(codeFile);
        }
    }


    private Solution solveProblem(@NonNull SolveRequest request, @NonNull Path codeFile) {
        Process scipProcess = null;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int timeout = Math.min(request.timeoutSeconds(), MAX_TIMEOUT_SECONDS);
        
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("scip", "-c",
                    "read " + codeFile + " optimize display solution quit");
            processBuilder.directory(codeFile.getParent().toFile());
            processBuilder.redirectErrorStream(true);
            scipProcess = processBuilder.start();
            final Process finalScipProcess = scipProcess;

            Solution solution = new Solution();
            Future<Boolean> readerFuture = executor.submit(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(finalScipProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (solution.processLine(line)) {
                            return true; // Solution  read
                        }
                    }
                    return false; // Reached end of output without finding a complete solution
                }
            });

            try {
                boolean solutionFound = readerFuture.get(timeout, TimeUnit.SECONDS);
                if (!solutionFound) {
                    throw new SolverException("Complete solution was not found in output");
                }
                return solution;
            } catch (TimeoutException e) {
                throw new SolverException("Solver timed out after " + timeout + " seconds");
            }
        }
        catch (IOException e) {
            throw new SolverException("IO Error during SCIP execution: " + e.getMessage());
        }
        catch (InterruptedException | ExecutionException e) {
            throw new SolverException("Solver execution error: " + e.getMessage());
        }
        finally {
            executor.shutdownNow();
            if (scipProcess != null) {
                scipProcess.destroyForcibly();
                closeQuietly(scipProcess.getInputStream());
                closeQuietly(scipProcess.getOutputStream());
                closeQuietly(scipProcess.getErrorStream());
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
            throw new ValidationException(e.getMessage());
        }
        finally {
            if (zimplProcess != null) {
                // Close all streams explicitly
                closeQuietly(zimplProcess.getInputStream());
                closeQuietly(zimplProcess.getOutputStream());
                closeQuietly(zimplProcess.getErrorStream());
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
    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.warn("Failed to close stream", e);
            }
        }
    }


}