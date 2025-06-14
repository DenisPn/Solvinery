package groupId.Controllers;

import Utilities.Configs.IntegrationTestsConfiguration;
import Utilities.Configs.PersistenceTestsConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.KafkaConfig;
import groupId.DTO.Records.Model.ModelDefinition.ModelDTO;
import groupId.DTO.Records.Requests.Commands.CreateImageFromFileDTO;
import groupId.DTO.Records.Requests.Commands.LoginDTO;
import groupId.DTO.Records.Requests.Commands.RegisterDTO;
import groupId.DTO.Records.Requests.Responses.LoginResponseDTO;
import groupId.Services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 4)
@AutoConfigureDataJpa
@ContextConfiguration(classes = {KafkaConfig.class, IntegrationTestsConfiguration.class})
@DisplayName("URI prefix: user/{userId}/image")
class UserImageControllerIntegrationTest {
   /* @Value("${test.zimpl.examples.problems.soldiers}")
    private Path filePath;*/
   private String BASE_URL;

    @Autowired
   private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    UserService userService;

    @BeforeEach
    void setUp() {
        RegisterDTO registerDTO = new RegisterDTO("testUser", "testUser", "test12345", "test@mail.com");
        userService.registerUser(registerDTO);
        LoginDTO loginDTO= new LoginDTO("testUser", "test12345");
        String userId =userService.loginUser(loginDTO).userId();
        BASE_URL = "/user/" + userId + "/image";
    }
    @Nested
    @DisplayName("parseModel test, URI: /model")
    class ParseModel{
        @Test
        void givenValidModel_whenParseModel_thenSuccess() throws Exception {
            CreateImageFromFileDTO createImageFromFileDTO = new CreateImageFromFileDTO("param i := 7;");
            String json = objectMapper.writeValueAsString(createImageFromFileDTO);
            ModelDTO expectedResponse= new ModelDTO(
                    Set.of(),
                    Set.of(),
                    Set.of(),
                    Map.of(),
                    Map.of("i","INT"));
            mockMvc.perform(
                            MockMvcRequestBuilders.post(BASE_URL + "/model")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        }
        static Stream<CreateImageFromFileDTO> ValidCaseStream(){
            return Stream.of(
                    new CreateImageFromFileDTO("param i := 7;")
            );
        }
    }


}