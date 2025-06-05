package groupId.Services;


import Exceptions.InternalErrors.ClientSideError;
import Exceptions.SolverExceptions.ValidationException;
import Exceptions.UserErrors.UserInputException;
import Model.Solution;
import Persistence.Entities.Image.ImageEntity;
import Persistence.Entities.UserEntity;
import Persistence.EntityMapper;
import groupId.DTO.Factories.RecordFactory;
import groupId.DTO.Records.Events.SolveRequest;
import groupId.DTO.Records.Image.SolutionDTO;
import groupId.Services.KafkaServices.SolveThread;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
@Slf4j
@Service
public class SolveService {
    private final ConcurrentHashMap<String, CompletableFuture<Solution>> pendingRequests = new ConcurrentHashMap<>();
    private static final String TOPIC_NAME = "solve-requests";
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

    @Transactional
    public SolutionDTO solve(String userId, String imageId, int timeout) {
        UserEntity user = userService.getUser(userId)
                .orElseThrow(() -> new ClientSideError("User id not found"));
        ImageEntity imageEntity=imageService.getImage(imageId)
                .orElseThrow(()->new ClientSideError("Invalid image ID during publish image."));
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<Solution> future = new CompletableFuture<>();

        pendingRequests.put(requestId,future);
        try {
            SolveRequest solveRequest= new SolveRequest(requestId, imageEntity.getZimplCode(), timeout,false);
            kafkaTemplate.send(TOPIC_NAME, solveRequest);

            Solution solution = future.get(timeout+5, TimeUnit.SECONDS);
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
        } catch (TimeoutException e) {
            throw new RuntimeException("Solution timed out", e);
        }
        catch (ValidationException e) {
            throw new UserInputException(e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("Error getting solution", e);
        } finally {
            pendingRequests.remove(requestId);
        }
    }
    @Transactional
    public SolutionDTO solveThreaded(String userId, String imageId, int timeout) {
        UserEntity user = userService.getUser(userId)
                .orElseThrow(() -> new ClientSideError("User id not found"));
        ImageEntity imageEntity = imageService.getImage(imageId)
                .orElseThrow(() -> new ClientSideError("Invalid image ID during publish image."));

        String requestId = UUID.randomUUID().toString();
        CompletableFuture<Solution> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);

        try {
            SolveRequest solveRequest = new SolveRequest(requestId, imageEntity.getZimplCode(), timeout,false);
            solverThread.submitRequest(solveRequest);

            Solution solution = future.get(timeout + 20, TimeUnit.SECONDS);
            log.info("Solve request completed successfully at Service level.");

            solution.parseSolution(EntityMapper.toDomain(imageEntity));
            return RecordFactory.makeDTO(solution);
        } catch (TimeoutException e) {
            throw new RuntimeException("Solution timed out" );
        } catch (Exception e) {
            throw new RuntimeException("Error getting solution: "+ e.getMessage());
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
