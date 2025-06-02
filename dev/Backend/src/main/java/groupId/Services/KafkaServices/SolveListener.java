package groupId.Services.KafkaServices;

import Exceptions.SolverExceptions.SolverException;
import Model.Solution;
import groupId.DTO.Records.Events.SolveRequest;
import groupId.Services.SolveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolveListener {

    private final SolveService solveService;

    @Value("${app.file.storage-dir}")
    private String baseStorageDir;
    private static final int MAX_TIMEOUT_SECONDS = 300;
@KafkaListener(
        topics = "solve-requests",
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "problem-solving-group"
)
public void handleSolveRequest(@Payload SolveRequest request,
                               Acknowledgment ack,
                               @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
    log.info("Received solve request: {}", request);
    try {
        Path codeFile = createCodeFile(request);
        Solution solution = solveProblem(request, codeFile);
        log.info("Solution found: {}", solution);
        ack.acknowledge();
    } catch (Exception e) {
        log.error("Error while solving: {}", e.getMessage());
        ack.nack(Duration.ofSeconds(3));
        }
    }



    private Solution solveProblem(SolveRequest request, Path codeFile) {
        Process process = null;
        try {
            int timeout = Math.min(request.timeoutSeconds(), MAX_TIMEOUT_SECONDS);
            ProcessBuilder processBuilder = new ProcessBuilder("scip", "-c",
                    String.format("read %s optimize display solution quit", codeFile.toString()));
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

            byte[] outputBytes = process.getInputStream().readAllBytes();
            return new Solution(new String(outputBytes));

        } catch (InterruptedException | IOException e) {
            throw new SolverException("Error during SCIP execution: " + e.getMessage());
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }

            cleanupFile(codeFile);
        }
    }

    private Path createCodeFile(SolveRequest request) throws IOException {
        if(request == null){
            log.error("Null request while creating code file");
            throw new SolverException("Null request while solving");
        }

        String sessionId = UUID.randomUUID().toString();
        Path directory = Paths.get(baseStorageDir);
        Path filePath = directory.resolve("session_" + sessionId + ".zpl");
        Files.createDirectories(directory);
        return Files.writeString(
                Files.createFile(filePath),
                request.zimplContent(),
                StandardOpenOption.CREATE_NEW
        );

    }

    private void cleanupFile(Path workDir) {
        if (workDir == null) {
            log.error("Null path while cleaning up work directory");
            throw new SolverException("Null path while solving");
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