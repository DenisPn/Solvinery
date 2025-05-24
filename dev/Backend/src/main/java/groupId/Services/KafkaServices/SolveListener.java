package groupId.Services.KafkaServices;

import groupId.DTO.Records.Events.SolveRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolveListener {


@KafkaListener(
        topics = "solve-requests",
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "problem-solving-group"
)
public void handleSolveRequest(@Payload SolveRequest request,
                               Acknowledgment ack,
                               @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
    }

}