package groupId.Controllers;

import Utilities.Configs.IntegrationTestsConfiguration;
import config.KafkaConfig;
import groupId.DTO.Records.Image.ConstraintModuleDTO;
import groupId.DTO.Records.Image.ImageDTO;
import groupId.DTO.Records.Image.PreferenceModuleDTO;
import groupId.DTO.Records.Model.ModelData.ParameterDTO;
import groupId.DTO.Records.Model.ModelData.SetDTO;
import groupId.DTO.Records.Model.ModelData.SetDefinitionDTO;
import groupId.DTO.Records.Model.ModelDefinition.ConstraintDTO;
import groupId.DTO.Records.Model.ModelDefinition.ModelDTO;
import groupId.DTO.Records.Model.ModelDefinition.PreferenceDTO;
import groupId.DTO.Records.Model.ModelDefinition.VariableDTO;
import groupId.DTO.Records.Requests.Commands.CreateImageFromFileDTO;
import groupId.DTO.Records.Requests.Commands.LoginDTO;
import groupId.DTO.Records.Requests.Commands.RegisterDTO;
import groupId.DTO.Records.Requests.Responses.ConfirmationDTO;
import groupId.DTO.Records.Requests.Responses.CreateImageResponseDTO;
import groupId.DTO.Records.Requests.Responses.ImagesDTO;
import groupId.DTO.Records.Requests.Responses.LoginResponseDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@EmbeddedKafka(partitions = 4)
@AutoConfigureDataJpa
@ContextConfiguration(classes = {KafkaConfig.class, IntegrationTestsConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserImageControllerETETest {
    private static final String soldiersExample;
    private static final String brokenExample;
    private static final String classesExample;

    static {
        try {
            soldiersExample = Files.readString(Paths.get("src/test/Utilities/ZimplExamples/Problems/SoldierStationsExample.zpl"));
            brokenExample = Files.readString(Paths.get("src/test/Utilities/ZimplExamples/Problems/BasicZimplProgram.zpl"));
            classesExample = Files.readString(Paths.get("src/test/Utilities/ZimplExamples/Problems/ClassScheduleProblem.zpl"));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Autowired
    JdbcTemplate jdbcTemplate;
    private static final String baseUriTemplate = "http://localhost:4000/user/{userId}/image";
    private String baseUri;
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;



    @BeforeEach
    void setUp() {
        String userId = setUpUser();
        baseUri = "http://localhost:" + port + "/user/" + userId + "/image";
    }
    @AfterEach
    void cleanUp() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'",
                String.class
        );
        tables.forEach(table ->
                jdbcTemplate.execute("TRUNCATE TABLE " + table)
        );
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    private String setUpUser(){
        baseUri = "http://localhost:" + port;
        RegisterDTO registerDTO = new RegisterDTO("testUser", "testUser", "test12345", "test@mail.com");
        ResponseEntity<ConfirmationDTO> regResponse = restTemplate.postForEntity(
                baseUri + "/users",
                registerDTO,
                ConfirmationDTO.class
        );

        assertTrue(regResponse.getStatusCode().is2xxSuccessful());
        assertNotNull(regResponse.getBody());

        LoginDTO loginDTO= new LoginDTO("testUser", "test12345");
        ResponseEntity<LoginResponseDTO> loginResponse = restTemplate.postForEntity(
                baseUri + "/users/session",
                loginDTO,
                LoginResponseDTO.class
        );
        assertTrue(loginResponse.getStatusCode().is2xxSuccessful());
        assertNotNull(loginResponse.getBody());
        return loginResponse.getBody().userId();
    }
    static Stream<ImageDTO> validExampleImagesStream() {
        return Stream.of(
                new ImageDTO(
                        Set.of(
                                new VariableDTO("day_has_class", List.of("Weekday"), "days with classes"),
                                new VariableDTO("selection", List.of("Class", "Weekday", "Time", "Duration"), "Lessons")
                        ),
                        Set.of(
                                new ConstraintModuleDTO("Overlap", "Force classes to not overlap", Set.of("no_overlap"), false)
                        ),
                        Set.of(
                                new PreferenceModuleDTO("minimize days with class", "strive for a minimum days with at least one class", Set.of("(20 * sum <d> in DAYS: day_has_class[d])"), 0.5F)
                        ),
                        Set.of(
                                new SetDTO(new SetDefinitionDTO("CLASS_OPTIONS", List.of("Class", "Weekday", "Time", "Duration"), "Lessons"), List.of())
                        ),
                        Set.of(
                        ),
                        "Class optimizer",
                        "does stuff and things",
                        classesExample
                )
        );
    }


    @Nested
    @DisplayName("Test Parse Model: POST "+baseUriTemplate+"/model")
    class TestParseModel{

        static Stream<String> InvalidCaseStream(){
            return Stream.of(
                    "",
                    " ",
                    "param i",
                    "\n\t",
                    "this should not validate");
        }
        record ParseCase(CreateImageFromFileDTO DTO, ModelDTO expectedResponse){}

        static Stream<ParseCase> ValidCaseStream(){
            return Stream.of(
                    new ParseCase(new CreateImageFromFileDTO(classesExample),
                            new ModelDTO(
                                    Set.of(
                                            new ConstraintDTO("one_option_per_class"),
                                            new ConstraintDTO("no_overlap"),
                                            new ConstraintDTO("set_day_has_class"),
                                            new ConstraintDTO("force_day_has_class")
                                    ),
                                    Set.of(
                                            new PreferenceDTO("(sum <c,d,h,dur> in CLASS_OPTIONS:\\r\\n selection[c,d,h,dur] * dur)"),
                                            new PreferenceDTO("(20 * sum <d> in DAYS: day_has_class[d])"),
                                            new PreferenceDTO("(sum <c1,d1,h1,dur1> in CLASS_OPTIONS:\\r\\n sum <c2,d2,h2,dur2> in CLASS_OPTIONS with d1 == d2 and h1 < h2:\\r\\n selection[c1,d1,h1,dur1] * selection[c2,d2,h2,dur2] * (h2 - (h1 + dur1)))")
                                    ),
                                    Set.of(
                                            new VariableDTO("day_has_class", List.of("UNKNOWN"), null),
                                            new VariableDTO("selection", List.of("TEXT","TEXT","INT","FLOAT"), null)
                                    ),
                                    Map.of("CLASS_OPTIONS",List.of("TEXT", "TEXT", "INT", "FLOAT")),
                                    Map.of()
                            )),
                    new ParseCase(
                            new CreateImageFromFileDTO(soldiersExample),
                            new ModelDTO(
                                    Set.of(
                                            new ConstraintDTO("forward_shift_transition"),
                                            new ConstraintDTO("no_simultaneous_duties"),
                                            new ConstraintDTO("minimum_shifts"),
                                            new ConstraintDTO("backward_shift_transition"),
                                            new ConstraintDTO("maximum_shifts"),
                                            new ConstraintDTO("shift_spacing"),
                                            new ConstraintDTO("transition_requires_assignment"),
                                            new ConstraintDTO("station_coverage"),
                                            new ConstraintDTO("transition_shift_consistency")),
                                    Set.of(
                                            new PreferenceDTO("(min_hours_between_shifts)**2"),
                                            new PreferenceDTO("sum<s,st1,t1> in POSSIBLE_ASSIGNMENTS:\r\n sum<st2,t2> in STATION_TIME_PAIRS | st2 != st1 or t2!=t1:\r\n (assignment[s,st1,t1] * assignment[s,st2,t2] * (t1-t2))"),
                                            new PreferenceDTO("(max_shifts_per_soldier-min_shifts_per_soldier)")
                                    ),
                                    Set.of(
                                            new VariableDTO("max_shifts_per_soldier", List.of(), null),
                                            new VariableDTO("assignment", List.of("TEXT", "TEXT", "INT"), null),
                                            new VariableDTO("min_hours_between_shifts", List.of(), null),
                                            new VariableDTO("transition", List.of("TEXT", "TEXT", "INT", "TEXT", "INT") ,null),
                                            new VariableDTO("min_shifts_per_soldier", List.of(), null)
                                    ),
                                    Map.of("TIME_SLOTS", List.of("INT")
                                            ,"STATIONS",List.of("TEXT"),
                                            "SOLDIERS",List.of("TEXT")
                                    ),
                                    Map.of("MIN_HOURS_BETWEEN_SHIFTS","INT")
                            ))
            );
        }

        static void equalsPreferences(Set<PreferenceDTO> expected, Set<PreferenceDTO> actual) {
            Set<String> normalizedExpected= expected.stream().map(p->p.identifier().replace("\r\n", "\n").replace("\r", "\n")).collect(Collectors.toSet());
            Set<String> normalizedActual= expected.stream().map(p->p.identifier().replace("\r\n", "\n").replace("\r", "\n")).collect(Collectors.toSet());
            assertEquals(normalizedExpected, normalizedActual);
        }

        @ParameterizedTest
        @MethodSource("ValidCaseStream")
        @DisplayName("Given valid model, when parse model, then shouldn't fail")
        void givenValidModel_whenParseModel_thenSuccess(ParseCase parseCase) {
            ResponseEntity<ModelDTO> parseResponse = restTemplate.postForEntity(
                    baseUri + "/model",
                    parseCase.DTO,
                    ModelDTO.class
            );
            assertTrue(parseResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(parseResponse.getBody());
            ModelDTO actualResponse = parseResponse.getBody();
            assertEquals(parseCase.expectedResponse.constraints(), actualResponse.constraints());
            equalsPreferences(parseCase.expectedResponse.preferences(), actualResponse.preferences());
            assertEquals(parseCase.expectedResponse.variables(), actualResponse.variables());
            assertEquals(parseCase.expectedResponse.paramTypes(), actualResponse.paramTypes());
            assertEquals(parseCase.expectedResponse.setTypes(), actualResponse.setTypes());
        }

        @ParameterizedTest
        @MethodSource("InvalidCaseStream")
        @DisplayName("Given invalid model, when parse model, then should fail")
        void givenInvalidCode_whenParseModel_thenFail(String invalidCode){
            CreateImageFromFileDTO createImageFromFileDTO = new CreateImageFromFileDTO(invalidCode);
            ResponseEntity<ModelDTO> parseResponse = restTemplate.postForEntity(
                    baseUri + "/model",
                    createImageFromFileDTO,
                    ModelDTO.class
            );
            assertTrue(parseResponse.getStatusCode().is4xxClientError());
        }
    }



    @Nested
    @DisplayName("Test Create Image: POST  "+baseUriTemplate+"/")
    class TestCreateImage {

        record CreateImageCase(ImageDTO given, ImageDTO expected){}

        static Stream<CreateImageCase> validCaseStream() {
            return Stream.of(
                    new CreateImageCase(
                            new ImageDTO(
                                    Set.of(
                                            new VariableDTO("day_has_class", List.of("Weekday"), "days with classes"),
                                            new VariableDTO("selection", List.of("Class", "Weekday", "Time", "Duration"), "Lessons")
                                    ),
                                    Set.of(
                                            new ConstraintModuleDTO("Overlap", "Force classes to not overlap", Set.of("no_overlap"), false)
                                    ),
                                    Set.of(
                                            new PreferenceModuleDTO("minimize days with class", "strive for a minimum days with at least one class", Set.of("(20 * sum <d> in DAYS: day_has_class[d])"), 0.5F)
                                    ),
                                    Set.of(
                                            new SetDTO(new SetDefinitionDTO("CLASS_OPTIONS", List.of("Class", "Weekday", "Time", "Duration"), "Lessons"), List.of())
                                    ),
                                    Set.of(
                                    ),
                                    "Class optimizer",
                                    "does stuff and things",
                                    classesExample
                            ),
                            new ImageDTO(
                            Set.of(
                                    new VariableDTO("day_has_class", List.of("Weekday"), "days with classes"),
                                    new VariableDTO("selection", List.of("Class", "Weekday", "Time", "Duration"), "Lessons")
                            ),
                            Set.of(
                                    new ConstraintModuleDTO("Overlap", "Force classes to not overlap", Set.of("no_overlap"), false)
                            ),
                            Set.of(
                                    new PreferenceModuleDTO("minimize days with class", "strive for a minimum days with at least one class", Set.of("(20 * sum <d> in DAYS: day_has_class[d])"), 0.5F)
                            ),
                            Set.of(
                                    new SetDTO(new SetDefinitionDTO("CLASS_OPTIONS", List.of("Class", "Weekday", "Time", "Duration"), "Lessons"),
                                            List.of(
                                                    "<\"Math101\",\"SUNDAY\",8,2>",
                                                    "<\"Math101\",\"MONDAY\",10,2>",
                                                    "<\"Math101\",\"WEDNESDAY\",14,2>",
                                                    "<\"Math101\",\"THURSDAY\",16,2>",
                                                    "<\"Physics102\",\"SUNDAY\",13,3>",
                                                    "<\"Physics102\",\"TUESDAY\",9,3>",
                                                    "<\"Physics102\",\"WEDNESDAY\",9,3>",
                                                    "<\"Physics102\",\"FRIDAY\",14,3>",
                                                    "<\"Chemistry101\",\"MONDAY\",15,2>",
                                                    "<\"Chemistry101\",\"TUESDAY\",13,3>",
                                                    "<\"Chemistry101\",\"THURSDAY\",11,2>",
                                                    "<\"Chemistry101\",\"FRIDAY\",9,3>",
                                                    "<\"English201\",\"SUNDAY\",8,1.5>",
                                                    "<\"English201\",\"MONDAY\",12,1.5>",
                                                    "<\"English201\",\"WEDNESDAY\",16,1.5>",
                                                    "<\"English201\",\"FRIDAY\",12,1.5>",
                                                    "<\"CompSci301\",\"SUNDAY\",11,2>",
                                                    "<\"CompSci301\",\"MONDAY\",8,2>",
                                                    "<\"CompSci301\",\"TUESDAY\",15,2>",
                                                    "<\"CompSci301\",\"THURSDAY\",14,2>",
                                                    "<\"Biology201\",\"SUNDAY\",15,2>",
                                                    "<\"Biology201\",\"TUESDAY\",8,3>",
                                                    "<\"Biology201\",\"WEDNESDAY\",11,2>",
                                                    "<\"Biology201\",\"THURSDAY\",9,3>",
                                                    "<\"Statistics102\",\"MONDAY\",13,1.5>",
                                                    "<\"Statistics102\",\"TUESDAY\",11,1.5>",
                                                    "<\"Statistics102\",\"THURSDAY\",8,1.5>",
                                                    "<\"Statistics102\",\"FRIDAY\",15,1.5>"
                                            ))
                            ),
                            Set.of(),
                            "Class optimizer",
                            "does stuff and things",
                            classesExample
                    )
            ));
        }
        static Stream<ImageDTO> invalidCaseStream() {
        return Stream.of(
                new ImageDTO( //No variables
                        Set.of(

                        ),
                        Set.of(
                                new ConstraintModuleDTO("Overlap", "Force classes to not overlap", Set.of("no_overlap"), false)
                        ),
                        Set.of(
                                new PreferenceModuleDTO("minimize days with class", "strive for a minimum days with at least one class", Set.of("(20 * sum <d> in DAYS: day_has_class[d])"), 0.5F)
                        ),
                        Set.of(
                                new SetDTO(new SetDefinitionDTO("CLASS_OPTIONS", List.of("Class", "Weekday", "Time", "Duration"), "Lessons"), List.of())
                        ),
                        Set.of(
                        ),
                        "Class optimizer",
                        "does stuff and things",
                        classesExample
                ),
                new ImageDTO( //null code
                        Set.of(
                                new VariableDTO("day_has_class", List.of("Weekday"), "days with classes"),
                                new VariableDTO("selection", List.of("Class", "Weekday", "Time", "Duration"), "Lessons")
                        ),
                        Set.of(
                                new ConstraintModuleDTO("Overlap", "Force classes to not overlap", Set.of("no_overlap"), false)
                        ),
                        Set.of(
                                new PreferenceModuleDTO("minimize days with class", "strive for a minimum days with at least one class", Set.of("(20 * sum <d> in DAYS: day_has_class[d])"), 0.5F)
                        ),
                        Set.of(
                                new SetDTO(new SetDefinitionDTO("CLASS_OPTIONS", List.of("Class", "Weekday", "Time", "Duration"), "Lessons"), List.of())
                        ),
                        Set.of(
                        ),
                        "Class optimizer",
                        "does stuff and things",
                        null
                ),
                new ImageDTO( //blank code
                        Set.of(
                                new VariableDTO("day_has_class", List.of("Weekday"), "days with classes"),
                                new VariableDTO("selection", List.of("Class", "Weekday", "Time", "Duration"), "Lessons")
                        ),
                        Set.of(
                                new ConstraintModuleDTO("Overlap", "Force classes to not overlap", Set.of("no_overlap"), false)
                        ),
                        Set.of(
                                new PreferenceModuleDTO("minimize days with class", "strive for a minimum days with at least one class", Set.of("(20 * sum <d> in DAYS: day_has_class[d])"), 0.5F)
                        ),
                        Set.of(
                                new SetDTO(new SetDefinitionDTO("CLASS_OPTIONS", List.of("Class", "Weekday", "Time", "Duration"), "Lessons"), List.of())
                        ),
                        Set.of(
                        ),
                        "Class optimizer",
                        "does stuff and things",
                        "\t\t\n\t"
                ),
                new ImageDTO( //invalid variable name
                    Set.of(
                            new VariableDTO("I don't exist", List.of("Weekday"), "days with classes")
                    ),
                    Set.of(
                            new ConstraintModuleDTO("Overlap", "Force classes to not overlap", Set.of("no_overlap"), false)
                    ),
                    Set.of(
                            new PreferenceModuleDTO("minimize days with class", "strive for a minimum days with at least one class", Set.of("(20 * sum <d> in DAYS: day_has_class[d])"), 0.5F)
                    ),
                    Set.of(
                            new SetDTO(new SetDefinitionDTO("CLASS_OPTIONS", List.of("Class", "Weekday", "Time", "Duration"), "Lessons"), List.of())
                    ),
                    Set.of(
                    ),
                    "Class optimizer",
                    "does stuff and things",
                    classesExample
            ),
                new ImageDTO( //empty constraint module
                        Set.of(
                                new VariableDTO("day_has_class", List.of("Weekday"), "days with classes"),
                                new VariableDTO("selection", List.of("Class", "Weekday", "Time", "Duration"), "Lessons")
                        ),
                        Set.of(
                                new ConstraintModuleDTO("Overlap", "Force classes to not overlap", Set.of(), false)
                        ),
                        Set.of(
                                new PreferenceModuleDTO("minimize days with class", "strive for a minimum days with at least one class", Set.of("(20 * sum <d> in DAYS: day_has_class[d])"), 0.5F)
                        ),
                        Set.of(
                                new SetDTO(new SetDefinitionDTO("CLASS_OPTIONS", List.of("Class", "Weekday", "Time", "Duration"), "Lessons"), List.of())
                        ),
                        Set.of(
                        ),
                        "Class optimizer",
                        "does stuff and things",
                        classesExample
                ),
                new ImageDTO( //invalid constraint in module
                        Set.of(
                                new VariableDTO("day_has_class", List.of("Weekday"), "days with classes"),
                                new VariableDTO("selection", List.of("Class", "Weekday", "Time", "Duration"), "Lessons")
                        ),
                        Set.of(
                                new ConstraintModuleDTO("Overlap", "Force classes to not overlap", Set.of("I dont exist"), false)
                        ),
                        Set.of(
                                new PreferenceModuleDTO("minimize days with class", "strive for a minimum days with at least one class", Set.of("(20 * sum <d> in DAYS: day_has_class[d])"), 0.5F)
                        ),
                        Set.of(
                                new SetDTO(new SetDefinitionDTO("CLASS_OPTIONS", List.of("Class", "Weekday", "Time", "Duration"), "Lessons"), List.of())
                        ),
                        Set.of(
                        ),
                        "Class optimizer",
                        "does stuff and things",
                        classesExample
                ),
                new ImageDTO( //Empty preference module
                        Set.of(
                                new VariableDTO("day_has_class", List.of("Weekday"), "days with classes"),
                                new VariableDTO("selection", List.of("Class", "Weekday", "Time", "Duration"), "Lessons")
                        ),
                        Set.of(
                                new ConstraintModuleDTO("Overlap", "Force classes to not overlap", Set.of("no_overlap"), false)
                        ),
                        Set.of(
                                new PreferenceModuleDTO("minimize days with class", "strive for a minimum days with at least one class", Set.of(), 0.5F)
                        ),
                        Set.of(
                                new SetDTO(new SetDefinitionDTO("CLASS_OPTIONS", List.of("Class", "Weekday", "Time", "Duration"), "Lessons"), List.of())
                        ),
                        Set.of(
                        ),
                        "Class optimizer",
                        "does stuff and things",
                        classesExample
                ),
                new ImageDTO( //invalid preference in module
                        Set.of(
                                new VariableDTO("day_has_class", List.of("Weekday"), "days with classes"),
                                new VariableDTO("selection", List.of("Class", "Weekday", "Time", "Duration"), "Lessons")
                        ),
                        Set.of(
                                new ConstraintModuleDTO("Overlap", "Force classes to not overlap", Set.of("no_overlap"), false)
                        ),
                        Set.of(
                                new PreferenceModuleDTO("minimize days with class", "strive for a minimum days with at least one class", Set.of("I don't exist"), 0.5F)
                        ),
                        Set.of(
                                new SetDTO(new SetDefinitionDTO("CLASS_OPTIONS", List.of("Class", "Weekday", "Time", "Duration"), "Lessons"), List.of())
                        ),
                        Set.of(
                        ),
                        "Class optimizer",
                        "does stuff and things",
                        classesExample
                )
        );
        }
        @ParameterizedTest
        @MethodSource("validCaseStream")
        @DisplayName("Given valid image, when create image, then should not fail")
        void givenValidImage_WhenCreateImage_thenSuccess(CreateImageCase createImageCase) {
            ResponseEntity<CreateImageResponseDTO> createImageResponse = restTemplate.postForEntity(
                    baseUri,
                    createImageCase.given,
                    CreateImageResponseDTO.class
            );
            assertTrue(createImageResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(createImageResponse.getBody());
            CreateImageResponseDTO actualResponse = createImageResponse.getBody();
            UUID imageId = UUID.fromString(actualResponse.imageId());
            ResponseEntity<ImagesDTO> fetchImageResponse = restTemplate.getForEntity(
                    baseUri + "/0",
                    ImagesDTO.class
            );
            assertTrue(fetchImageResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(fetchImageResponse.getBody());
            ImagesDTO actualImages = fetchImageResponse.getBody();
            assertEquals(1, actualImages.images().size());
            ImageDTO actualImage = actualImages.images().get(imageId);
            assertEquals(createImageCase.expected, actualImage);
        }
        @ParameterizedTest
        @MethodSource("invalidCaseStream")
        @DisplayName("Given invalid image, when create image, then should fail")
        void givenInvalidImage_WhenCreateImage_thenFail(ImageDTO imageDTO) {
            ResponseEntity<CreateImageResponseDTO> createImageResponse = restTemplate.postForEntity(
                    baseUri,
                    imageDTO,
                    CreateImageResponseDTO.class
            );
            assertFalse(createImageResponse.getStatusCode().is2xxSuccessful());
            ResponseEntity<ImagesDTO> fetchImageResponse = restTemplate.getForEntity(
                    baseUri + "/0",
                    ImagesDTO.class
            );
            assertTrue(fetchImageResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(fetchImageResponse.getBody());
            ImagesDTO actualImages = fetchImageResponse.getBody();
            assertEquals(0, actualImages.images().size());
        }
    }
    @Nested
    @DisplayName("Test Delete Images: DELETE  "+baseUriTemplate+"/{imageId}")
    class DeleteImageTest {

        @ParameterizedTest
        @MethodSource("groupId.Controllers.UserImageControllerETETest#validExampleImagesStream")
        @DisplayName("Given valid image, when delete image, then should not fail")
        void givenValidImage_WhenDeleteImage_thenSuccess(ImageDTO imageDTO) {
            ResponseEntity<CreateImageResponseDTO> createImageResponse = restTemplate.postForEntity(
                    baseUri,
                    imageDTO,
                    CreateImageResponseDTO.class
            );
            assertTrue(createImageResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(createImageResponse.getBody());
            ResponseEntity<Void> deleteImageResponse = restTemplate.exchange(
                    baseUri + "/{imageId}",
                    HttpMethod.DELETE,
                    null,
                    Void.class,
                    createImageResponse.getBody().imageId()
            );
            assertTrue(deleteImageResponse.getStatusCode().is2xxSuccessful());
            ResponseEntity<ImagesDTO> fetchImageResponse = restTemplate.getForEntity(
                    baseUri + "/0",
                    ImagesDTO.class
            );
            assertTrue(fetchImageResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(fetchImageResponse.getBody());
            ImagesDTO actualImages = fetchImageResponse.getBody();
            assertEquals(0, actualImages.images().size());
        }

        @Test
        @DisplayName("Given no image with given id, when delete image, then should fail")
        void givenInvalidImageId_WhenDeleteImage_thenFail() {
            ResponseEntity<Void> deleteImageResponse = restTemplate.exchange(
                    baseUri + "/{imageId}",
                    HttpMethod.DELETE,
                    null,
                    Void.class,
                    UUID.randomUUID().toString()
            );
            assertTrue(deleteImageResponse.getStatusCode().is4xxClientError());
        }
    }
    @Nested
    @DisplayName("Test Fetch Images: GET "+baseUriTemplate+"/{page}")
    class TestFetchImages{
        static final int DEFAULT_PAGE_SIZE = 10;
        private record ImageAndCountPair(ImageDTO imageDTO, int saveCount){}

        static Stream<ImageAndCountPair> validExampleImagesAndCountStream() {
            return validExampleImagesStream().flatMap(imageDTO -> Stream.of(
                    new ImageAndCountPair(imageDTO, 0), //check that fetch returns an empty list
                    new ImageAndCountPair(imageDTO, 1),
                    new ImageAndCountPair(imageDTO, 10),
                    new ImageAndCountPair(imageDTO, 11),
                    new ImageAndCountPair(imageDTO, 22),
                    new ImageAndCountPair(imageDTO, 100)
            ));
        }
        @ParameterizedTest
        @MethodSource("validExampleImagesAndCountStream")
        @DisplayName("Given images, when saved many times, fetch pages correctly")
        void givenImages_whenSavedManyTimes_fetchPagesCorrectly(ImageAndCountPair imageAndCountPair) {
            ImageDTO imageDTO = imageAndCountPair.imageDTO();
            int saveCount = imageAndCountPair.saveCount();
            //save count number of images
            for(int i = 0; i < saveCount; i++){
                ResponseEntity<CreateImageResponseDTO> createImageResponse = restTemplate.postForEntity(
                        baseUri,
                        imageDTO,
                        CreateImageResponseDTO.class
                );
                assertTrue(createImageResponse.getStatusCode().is2xxSuccessful());
                assertNotNull(createImageResponse.getBody());
            }
            //fetch all full pages
            for(int page=0; page < (saveCount / DEFAULT_PAGE_SIZE); page++){
                ResponseEntity<ImagesDTO> imagesResponseEntityFullPage = restTemplate.getForEntity(
                        baseUri +"/"+page,
                        ImagesDTO.class
                );
                assertTrue(imagesResponseEntityFullPage.getStatusCode().is2xxSuccessful());
                assertNotNull(imagesResponseEntityFullPage.getBody());
                assertEquals(DEFAULT_PAGE_SIZE, imagesResponseEntityFullPage.getBody().images().size());
                for (ImageDTO image : imagesResponseEntityFullPage.getBody().images().values()) {
                    assertEquals(imageDTO.code(), image.code());
                    assertEquals(imageDTO.description(), image.description());
                    assertEquals(imageDTO.name(), image.name());
                    assertEquals(imageDTO.constraintModules(), image.constraintModules());
                    assertEquals(imageDTO.preferenceModules(), image.preferenceModules());
                    assertEquals(imageDTO.variables(), image.variables());
                    assertEquals(imageDTO.sets().stream().map(SetDTO::setDefinition).collect(Collectors.toSet()), image.sets().stream().map(SetDTO::setDefinition).collect(Collectors.toSet()));
                    assertEquals(imageDTO.parameters().stream().map(ParameterDTO::parameterDefinition).collect(Collectors.toSet()), image.parameters().stream().map(ParameterDTO::parameterDefinition).collect(Collectors.toSet()));
                }
            }
            //if exists, fetch partially full last page
            if(saveCount % DEFAULT_PAGE_SIZE != 0){
                ResponseEntity<ImagesDTO> imagesResponseEntityPartialPage = restTemplate.getForEntity(
                        baseUri +"/"+(saveCount / DEFAULT_PAGE_SIZE),
                        ImagesDTO.class
                );
                assertTrue(imagesResponseEntityPartialPage.getStatusCode().is2xxSuccessful());
                assertNotNull(imagesResponseEntityPartialPage.getBody());
                assertEquals(saveCount % DEFAULT_PAGE_SIZE, imagesResponseEntityPartialPage.getBody().images().size());
                //ignoring params and sets data, since additional data may be read from source code.
                for (ImageDTO image : imagesResponseEntityPartialPage.getBody().images().values()) {
                    assertEquals(imageDTO.code(), image.code());
                    assertEquals(imageDTO.description(), image.description());
                    assertEquals(imageDTO.name(), image.name());
                    assertEquals(imageDTO.constraintModules(), image.constraintModules());
                    assertEquals(imageDTO.preferenceModules(), image.preferenceModules());
                    assertEquals(imageDTO.variables(), image.variables());
                    assertEquals(imageDTO.sets().stream().map(SetDTO::setDefinition).collect(Collectors.toSet()), image.sets().stream().map(SetDTO::setDefinition).collect(Collectors.toSet()));
                    assertEquals(imageDTO.parameters().stream().map(ParameterDTO::parameterDefinition).collect(Collectors.toSet()), image.parameters().stream().map(ParameterDTO::parameterDefinition).collect(Collectors.toSet()));
                }
            }
            //fetch empty page
            ResponseEntity<ImagesDTO> imagesResponseEntityEmptyPage = restTemplate.getForEntity(
                    baseUri +"/"+(saveCount / DEFAULT_PAGE_SIZE + 1),
                    ImagesDTO.class
            );
            assertTrue(imagesResponseEntityEmptyPage.getStatusCode().is2xxSuccessful());
            assertNotNull(imagesResponseEntityEmptyPage.getBody());
            assertEquals(0, imagesResponseEntityEmptyPage.getBody().images().size());
        }
    }
    @Nested
    @DisplayName("Test Configure Images: PATCH "+baseUriTemplate+"/{imageId}")
    class ConfigureImage{

    }
}
