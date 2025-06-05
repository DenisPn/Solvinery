package groupId.Services.KafkaServices;

import Exceptions.SolverExceptions.SolverException;
import Model.Solution;
import groupId.DTO.Records.Events.SolveRequest;
import groupId.Services.SolveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SolveLThread extends Thread {

    private final SolveService solveService;
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
    public SolveLThread(SolveService solveService) {
        this.solveService = solveService;
        this.requestQueue = new LinkedBlockingQueue<>();
        this.start();
    }

    public void submitRequest(SolveRequest request) {
        requestQueue.offer(request);
    }

    public void shutdown() {
        running = false;
        interrupt();
    }

    public void handleSolveRequest(@Payload SolveRequest request)
    {
    Path codeFile = null;
    try {
        log.info("---------------------------Attempt 9-------------------------------");
        log.info("Received solve request: {}", request.requestId());
        codeFile = createCodeFile(request);
        Solution solution = solveProblem(request, codeFile);
        log.info("Solution found: {}", solution);
        solveService.completeSolution(request.requestId(), solution);
        log.info("Completed solve request: {}", request.requestId());
    } catch (Exception e) {
        log.info("Caught exception of type: {}",e.getClass());
        log.error("Error while solving: {}", e.getMessage());
        solveService.completeWithError(request.requestId(), e);
        }
    finally {
       // cleanupFile(codeFile);
    }
    }



    private Solution solveProblem(SolveRequest request, Path codeFile) {
        Process process = null;
        try {
            log.info("Got path: {}", codeFile.toAbsolutePath());
            int timeout = Math.min(request.timeoutSeconds(), MAX_TIMEOUT_SECONDS);
            ProcessBuilder processBuilder = new ProcessBuilder("scip", "-c",
                    String.format("read %s optimize display solution q", codeFile));
            //"scip", "-c", "read " + sourceFilePath + " optimize display solution q"
            processBuilder.directory(codeFile.getParent().toFile());
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();

            boolean completed = process.waitFor(timeout, TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                throw new SolverException("SCIP solver timed out");
            }

            if (process.exitValue() != 0) {
                throw new SolverException("SCIP process failed with exit code: " + process.exitValue());
            }
            log.info("SCIP process successfully completed.");
            byte[] outputBytes = process.getInputStream().readAllBytes();
            log.info("\n-------------------OUTPUT----------------\n{}\n-------------------END--------------------\n",new String(outputBytes));
            return new Solution(new String(outputBytes));

        } catch (InterruptedException | IOException e) {
            throw new SolverException("Error during SCIP execution: " + e.getMessage());
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    private Path createCodeFile(SolveRequest request) throws IOException {
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

    private void cleanupFile(Path workDir) {
        if (workDir == null) {
            log.warn("Null path while cleaning up work directory");
//            throw new SolverException("Null path while solving");
            return;
        }

        try {
            boolean deleted = Files.deleteIfExists(workDir);
            if (!deleted) {
                log.warn("File {} was already deleted or doesn't exist", workDir);
            }
        } catch (IOException e) {
            log.warn("Failed to cleanup code session file: {} ({})", workDir, e.getMessage(), e);
        }

    }


}