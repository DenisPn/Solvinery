package Acceptance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

//import org.springframework.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import DTO.Records.Model.ModelDefinition.ConstraintDTO;
import DTO.Records.Model.ModelDefinition.DependenciesDTO;
import DTO.Records.Model.ModelDefinition.ModelDTO;
import DTO.Records.Model.ModelDefinition.PreferenceDTO;
import DTO.Records.Model.ModelDefinition.VariableDTO;
import DTO.Records.Requests.Commands.*;
import DTO.Records.Requests.Responses.CreateImageResponseDTO;
import groupId.Main;
import groupId.Service;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = Main.class)
public class ServiceTest {
    @LocalServerPort
    private int port;
    static String SimpleCodeExample = """
                param x := 10;
                set mySet := {1,2,3};

                var myVar[mySet];

                subto sampleConstraint:
                    myVar[x] == mySet[1];

                maximize myObjective:
                    1;
            """;
    @Autowired
    private Service service;
    
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateImage() {
        // sample Zimpl code

        CreateImageFromFileDTO body = new CreateImageFromFileDTO(SimpleCodeExample);

        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Create http request with body and headers
        HttpEntity<CreateImageFromFileDTO> request = new HttpEntity<>(body, headers);

        // Send POST request with body
        String url = "http://localhost:" + port + "/images";
        ResponseEntity<CreateImageResponseDTO> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            CreateImageResponseDTO.class
        );

        //Expected response
        CreateImageResponseDTO expected = new CreateImageResponseDTO(
            "some id", new ModelDTO(
              List.of(new ConstraintDTO("sampleConstraint", new DependenciesDTO(List.of("mySet"),List.of("x")))),
              List.of(new PreferenceDTO("myObjective", new DependenciesDTO(List.of(),List.of()))),
              List.of(new VariableDTO("myVar", new DependenciesDTO(List.of("mySet"),List.of()))),
              Map.of(
                "mySet","INT",
                "x","INT"
                )
            ));
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().imageId() != null);
        assertEquals(response.getBody().model().constraints(), expected.model().constraints());
        assertEquals(response.getBody().model().preferences(), expected.model().preferences());
        assertEquals(response.getBody().model().variables(), expected.model().variables());
        assertEquals(response.getBody().model().types(), expected.model().types());
    }

}