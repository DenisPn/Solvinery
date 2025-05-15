package groupId.Services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import groupId.DTO.Records.Events.SolveRequest;
import groupId.DTO.Records.Requests.Commands.RegisterDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import groupId.DTO.Records.Image.SolutionDTO;
import groupId.DTO.Records.Model.ModelData.InputDTO;
import groupId.DTO.Records.Requests.Commands.SolveCommandDTO;
import Exceptions.InternalErrors.BadRequestException;
import Image.Image;
import Model.ModelInterface;
import Model.Data.Types.ModelType;

@Service
@RequiredArgsConstructor
public class MainService {


    private static final String TOPIC_NAME = "solve-requests";

    private final KafkaTemplate<String, SolveRequest> kafkaTemplate;

    /**
     * @param command solve command DTO object
     * @return A DTO object with the parsed solution in its fields
     * @see SolutionDTO
     */
    //need to replace with kafka impl
    @Deprecated(forRemoval = true)
    public SolutionDTO solve(SolveCommandDTO command)  {
       /* //encapsulated in try-catch, since having a method throw Exception is bad practice.
        try {
            Image image = images.get(UUID.fromString(command.imageId()));
            ModelInterface model = image.getModel();
            for (Map.Entry<String, List<List<String>>> set : command.input().setData().entrySet()) {
                *//*List<String> setElements = new LinkedList<>();
                for (List<String> element : set.getValue()) {
                    String tuple = ModelType.convertArrayOfAtoms((element.toArray(new String[0])), model.getSet(set.getKey()).getType());
                    setElements.add(tuple);
                }
                model.setInput(model.getSet(set.getKey()), setElements);*//*

            }

            for (Map.Entry<String, List<String>> parameter : command.input().paramData().entrySet()) {
                model.setInput(model.getParameter(parameter.getKey()), ModelType.convertArrayOfAtoms(parameter.getValue().toArray(new String[0]), model.getParameter(parameter.getKey()).getType()));


            }

            for (String constraint : command.input().constraintsToggledOff()) {
                model.toggleFunctionality(model.getConstraint(constraint), false);
            }

            for (String preference : command.input().preferencesToggledOff()) {
                model.toggleFunctionality(model.getPreference(preference), false);
            }

            return image.solve(command.timeout());
        }
        //encapsulated in try-catch, since having a method throw Exception is bad practice.
        catch (Exception e) {
            throw new BadRequestException("Error while solving: "+e.getMessage());
        }*/
        return null;
    }

    public void sendSolveRequest(String userId, String problemId, String zimplContent) {
        SolveRequest request = new SolveRequest(userId, problemId, zimplContent);
        kafkaTemplate.send(TOPIC_NAME, request);
    }

    @KafkaListener(topics = TOPIC_NAME, groupId = "${spring.kafka.consumer.group-id}")
    public void handleSolveRequest(SolveRequest request) {
        System.out.println("Received solve request:");
        System.out.println("User ID: " + request.userId());
        System.out.println("Problem ID: " + request.problemId());
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
