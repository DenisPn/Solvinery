package groupId.Services.KafkaServices;

import Exceptions.SolverExceptions.SolverException;
import Exceptions.SolverExceptions.ValidationException;
import Model.Solution;
import groupId.DTO.Records.Events.SolveRequest;
import groupId.Services.SolveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.lang.NonNull;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolveListener {

    @NonNull
    private final SolveService solveService;

    //@Value("${app.file.storage-dir}")
    private final String baseStorageDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath() + "/User/Models";
    private static final int MAX_TIMEOUT_SECONDS = 30;
@KafkaListener(
        topics = "solve-requests",
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "problem-solving-group-20"
)
public void handleSolveRequest(@NonNull @Payload SolveRequest request) {
    Path codeFile = null;
    try {
        log.info("---------------------------Attempt 18-------------------------------");
        log.info("Received solve request: {}", request.requestId());
        codeFile = createCodeFile(request);
        validateZimplCode(codeFile);
        if(request.validationOnly()){
            log.info("Validation only, returning solution.");
            solveService.completeSolution(request.requestId(), null);
        }
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



    @NonNull
    private Solution solveProblem(@NonNull SolveRequest request, @NonNull Path codeFile) {
        Process scipProcess = null;
        try {
            log.info("Got path: {}", codeFile.toAbsolutePath());
            int timeout = Math.min(request.timeoutSeconds(), MAX_TIMEOUT_SECONDS);
            ProcessBuilder processBuilder = new ProcessBuilder("scip", "-c",
                    String.format("\"read %s optimize display solution quit\"", codeFile));
            //"scip", "-c", "read " + sourceFilePath + " optimize display solution q"
            processBuilder.directory(codeFile.getParent().toFile());
            processBuilder.redirectErrorStream(true);
            log.info("Executing command: {}", processBuilder.command());
            scipProcess = processBuilder.start();

            boolean completed = scipProcess.waitFor(timeout, TimeUnit.SECONDS);
            if (!completed) {
                String output = new String(scipProcess.getInputStream().readAllBytes());
                output = startFromStatus(output);
                String prunedOutput = output.lines()
                        .filter(line -> !line.startsWith("@@"))
                        .collect(Collectors.joining("\n"));
                throw new SolverException(prunedOutput);
            }

            if (scipProcess.exitValue() != 0) {
                throw new SolverException("SCIP solver execution failed with exit code: " + scipProcess.exitValue());
            }
            log.info("SCIP process successfully completed.");
            String output = new String(scipProcess.getInputStream().readAllBytes());
            output = startFromStatus(output);
            String prunedOutput = output.lines()
                    .filter(line -> !line.startsWith("@@"))
                    .collect(Collectors.joining("\n"));

            log.info("\n-------------------OUTPUT----------------\n{}\n-------------------END--------------------\n",prunedOutput);
            //return new Solution(prunedOutput);
            return new Solution(); //TEMP UNTILL IMPL

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

}