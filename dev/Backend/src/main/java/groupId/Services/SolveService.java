package groupId.Services;


import Exceptions.InternalErrors.ClientSideError;
import Exceptions.SolverExceptions.SolverException;
import Exceptions.SolverExceptions.ValidationException;
import Exceptions.UserErrors.UserInputException;
import Image.Image;
import Model.Solution;
import Persistence.Entities.Image.ImageEntity;
import Persistence.Entities.UserEntity;
import Persistence.EntityMapper;
import groupId.DTO.Factories.RecordFactory;
import groupId.DTO.Records.Events.SolveRequest;
import groupId.DTO.Records.Image.SolutionDTO;
import groupId.DTO.Records.Requests.Commands.ImageConfigDTO;
import groupId.Services.KafkaServices.SolveThread;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@Service
public class SolveService {
    private final ConcurrentHashMap<String, CompletableFuture<Solution>> pendingRequests = new ConcurrentHashMap<>();
    private static final String TOPIC_NAME = "solve-requests";
    @NonNull
    private final SolveThread solverThread; //TEMP, TO BE REMOVED

    private final KafkaTemplate<String, SolveRequest> kafkaTemplate;
    private final ImageService imageService;
    private final UserService userService;

    public SolveService(KafkaTemplate<String, SolveRequest> kafkaTemplate, UserService userService, ImageService imageService) {
        this.imageService=imageService;
        this.userService=userService;
        this.kafkaTemplate = kafkaTemplate;
        this.solverThread= new SolveThread(this);
    }

    @NonNull
    @Transactional
    public SolutionDTO solve(@NonNull String userId, @NonNull String imageId, int timeout) {
        UserEntity user = userService.getUser(userId)
                .orElseThrow(() -> new ClientSideError("User id not found"));
        ImageEntity imageEntity=imageService.getImage(imageId)
                .orElseThrow(()->new ClientSideError("Invalid image ID during publish image."));
        Image image = EntityMapper.toDomain(imageEntity);
        String code= image.getModifiedZimplCode();
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<Solution> future = new CompletableFuture<>();

        pendingRequests.put(requestId,future);
        try {
            SolveRequest solveRequest= new SolveRequest(requestId, code, timeout,false);
            kafkaTemplate.send(TOPIC_NAME, solveRequest);

            Solution solution = future.get(timeout+5, TimeUnit.SECONDS);
            if(solution.isSolved())
                solution.postProcessSolution(image);
            log.info("Solve request completed successfully at Service level.");
            return RecordFactory.makeDTO(solution);
        } catch (TimeoutException e) {
            throw new RuntimeException("Solution timed out", e);
        } catch (Exception e) {
            throw new RuntimeException("Error getting solution", e);
        } finally {
            pendingRequests.remove(requestId);
        }

    }
    @Transactional(readOnly = true)
    public void validate(String zimplCode) {
        int timeout = 5;
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<Solution> future = new CompletableFuture<>();
        pendingRequests.put(requestId,future);
        try {
            SolveRequest solveRequest= new SolveRequest(requestId, zimplCode, timeout,true);
            kafkaTemplate.send(TOPIC_NAME, solveRequest);
            future.get(timeout, TimeUnit.SECONDS);
            //solution.postProcessSolution(image);
        } catch (TimeoutException e) {
            throw new RuntimeException("Solution timed out", e);
        }
        catch (ValidationException e) {
            throw new UserInputException(e.getMessage());
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error getting solution", e);
        } finally {
            pendingRequests.remove(requestId);
        }
    }
    @Transactional(readOnly = true)
    public void validateThreaded(String zimplCode) {
        int timeout = 5;
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<Solution> future = new CompletableFuture<>();
        pendingRequests.put(requestId,future);
        try {
            SolveRequest solveRequest = new SolveRequest(requestId, zimplCode, timeout,true);
            solverThread.submitRequest(solveRequest);
            future.get(timeout, TimeUnit.SECONDS);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("Solution timed out"+ e.getMessage());
        }
        catch (ExecutionException e) {
            throw new UserInputException(e.getCause().getMessage());
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Unexpected Solver error: "+ e.getMessage());
        } finally {
            pendingRequests.remove(requestId);
        }
    }
    @NonNull
    @Transactional
    public SolutionDTO solveThreaded(@NonNull String userId, @NonNull String imageId, @NonNull ImageConfigDTO config) {
        int timeout = config.timeout();
        UserEntity user = userService.getUser(userId)
                .orElseThrow(() -> new ClientSideError("User id not found"));
        ImageEntity imageEntity = imageService.getImage(imageId)
                .orElseThrow(() -> new ClientSideError("Invalid image ID during publish image."));
        Image image = EntityMapper.toDomain(imageEntity);
        image.apply(config);
        String code= image.getModifiedZimplCode();
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<Solution> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);

        try {
            SolveRequest solveRequest = new SolveRequest(requestId, code, timeout,false);
            solverThread.submitRequest(solveRequest);

            Solution solution = future.get(timeout + 20, TimeUnit.SECONDS);
            log.info("Solve request completed successfully at Service level.");
            solution.postProcessSolution(image);
            //solution.parseSolution(EntityMapper.toDomain(imageEntity));
            return RecordFactory.makeDTO(solution);
        } catch (TimeoutException e) {
            throw new RuntimeException("Solution timed out" );
        }
        catch (ExecutionException e) {
            throw new UserInputException(e.getCause().getMessage());
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Unexpected Solver error: "+ e.getMessage());
        } finally {
            pendingRequests.remove(requestId);
        }
    }
    @PreDestroy
    public void cleanup() {
        solverThread.shutdown();
    }

    public void completeSolution(String requestId, Solution solution) {
        CompletableFuture<Solution> future = pendingRequests.get(requestId);
        if (future != null) { //future may be null if the thread took too long
            future.complete(solution);
        }
    }

    public void completeWithError(String requestId, Exception error) {
        CompletableFuture<Solution> future = pendingRequests.get(requestId);
        if (future != null) { //future may be null if the thread took too long
            future.completeExceptionally(error);
        }
    }

}
