package groupId.Services;

import groupId.DTO.Records.Events.SolveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainService {


    private static final String TOPIC_NAME = "solve-requests";

    private final KafkaTemplate<String, SolveRequest> kafkaTemplate;

    public void sendSolveRequest(String userId, String problemId, String zimplContent) {
        /*SolveRequest request = new SolveRequest(userId, problemId, zimplContent,30);
        kafkaTemplate.send(TOPIC_NAME, request);*/
    }

    @KafkaListener(topics = TOPIC_NAME, groupId = "${spring.kafka.consumer.group-id}")
    public void handleSolveRequest(SolveRequest request) {
        System.out.println("Received solve request:");
        System.out.println("Zimpl Content: " + request.zimplContent());
    }

    public void testKafka() {
        // Test method to send a sample message
        sendSolveRequest(
                "test-user-123",
                "problem-456",
                "param n := 2;"
        );
    }



}
