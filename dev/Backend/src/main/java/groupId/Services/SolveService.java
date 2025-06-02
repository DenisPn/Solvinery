package groupId.Services;


import Exceptions.InternalErrors.ClientSideError;
import Exceptions.SolverExceptions.SolverException;
import Model.Solution;
import Persistence.Entities.Image.ImageEntity;
import Persistence.Entities.UserEntity;
import Persistence.EntityMapper;
import groupId.DTO.Factories.RecordFactory;
import groupId.DTO.Records.Events.SolveRequest;
import groupId.DTO.Records.Image.SolutionDTO;
import groupId.DTO.Records.Requests.Responses.CreateImageResponseDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class SolveService {
    private final ConcurrentHashMap<String, CompletableFuture<Solution>> pendingRequests = new ConcurrentHashMap<>();
    private static final String TOPIC_NAME = "solve-requests";

    private final KafkaTemplate<String, SolveRequest> kafkaTemplate;
    private final ImageService imageService;
    private final UserService userService;

    public SolveService(KafkaTemplate<String, SolveRequest> kafkaTemplate, UserService userService, ImageService imageService) {
        this.imageService=imageService;
        this.userService=userService;
        this.kafkaTemplate = kafkaTemplate;
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
            SolveRequest solveRequest= new SolveRequest(requestId, imageEntity.getZimplCode(), timeout);
            kafkaTemplate.send(TOPIC_NAME, solveRequest);

            Solution solution = future.get(timeout+5, TimeUnit.SECONDS);
            return RecordFactory.makeDTO(solution);
        } catch (TimeoutException e) {
            throw new RuntimeException("Solution timed out", e);
        } catch (Exception e) {
            throw new RuntimeException("Error getting solution", e);
        } finally {
            pendingRequests.remove(requestId);
        }

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
