package groupId.Services.KafkaServices;

import Exceptions.SolverExceptions.SolverException;
import Model.Solution;
import groupId.DTO.Records.Events.SolveRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolveListener {

    @Value("${app.file.storage-dir}")
    private String baseStorageDir;
    private static final int DEFAULT_TIMEOUT_SECONDS = 300;

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
        String workDir = createWorkDirectory(request);
        Solution solution = solveProblem(request, workDir);
        log.info("Solution found: {}", solution);
        ack.acknowledge();
    } catch (Exception e) {
        log.error("Error while solving: {}", e.getMessage());
        ack.nack(Duration.ofSeconds(10));
    }

    }



    private Solution solveProblem(SolveRequest request, String workDir) {
        Process process = null;
        try {
            ProcessBuilder pb = new ProcessBuilder("scip", "-c",
                    String.format("read %s optimize display solution quit", request.getModelPath()));
            pb.directory(new File(workDir));
            pb.redirectErrorStream(true);

            process = pb.start();
            Process finalProcess = process;

            Future<Solution> future = () ->
                   /* Something?? */;

            return future.get(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        } catch (TimeoutException e) {
            if (process != null) process.destroy();
            throw new SolverException("SCIP solver timed out");
        } catch (Exception e) {
            throw new SolverException("Error during SCIP execution", e);
        } finally {
            cleanupWorkDirectory(workDir);
        }
    }

    private String createWorkDirectory(SolveRequest request) throws IOException {
        String dirPath = baseStorageDir + "/solve_" + request.userId()+request.problemId() + "_" + System.currentTimeMillis();
        Files.createDirectory(Paths.get(dirPath));
        return dirPath;
    }

    private void cleanupWorkDirectory(String workDir) {
        try {
            FileUtils.deleteDirectory(new File(workDir));
        } catch (IOException e) {
            log.warn("Failed to cleanup work directory: {}", workDir, e);
        }
    }


}