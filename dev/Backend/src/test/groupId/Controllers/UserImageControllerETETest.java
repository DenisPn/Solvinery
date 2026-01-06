package groupId.Controllers;

import Utilities.Builders.ImageDTOBuilder;
import Utilities.Configs.IntegrationTestsConfiguration;
import config.KafkaConfig;
import groupId.DTO.Records.Image.ConstraintModuleDTO;
import groupId.DTO.Records.Image.ImageDTO;
import groupId.DTO.Records.Image.PreferenceModuleDTO;
import groupId.DTO.Records.Model.ModelData.ParameterDTO;
import groupId.DTO.Records.Model.ModelData.ParameterDefinitionDTO;
import groupId.DTO.Records.Model.ModelData.SetDTO;
import groupId.DTO.Records.Model.ModelData.SetDefinitionDTO;
import groupId.DTO.Records.Model.ModelDefinition.ConstraintDTO;
import groupId.DTO.Records.Model.ModelDefinition.ModelDTO;
import groupId.DTO.Records.Model.ModelDefinition.PreferenceDTO;
import groupId.DTO.Records.Model.ModelDefinition.VariableDTO;
import groupId.DTO.Records.Requests.Commands.CreateImageFromFileDTO;
import groupId.DTO.Records.Requests.Commands.LoginDTO;
import groupId.DTO.Records.Requests.Commands.RegisterDTO;
import groupId.DTO.Records.Requests.Responses.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static groupId.Controllers.UserImageControllerETETest.TestCreateImage.invalidClassesStream;
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
    private String baseImageControllerURI;
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;



    @BeforeEach
    void setUp() {
        String userId = setUpUser();
        baseUri = "http://localhost:" + port + "/user/" + userId + "/image";
        baseImageControllerURI = "http://localhost:" + port + "/image";
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());

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
        RegisterDTO registerDTO = new RegisterDTO("testUser", "testUserNickname", "test12345", "test@mail.com");
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
    private static ImageDTOBuilder validClassesExampleTemplate() {
        return new ImageDTOBuilder()
                .withName("Class optimizer")
                .withDescription("does stuff and things")
                .withCode(classesExample)
                .withVariables(
                        new VariableDTO("day_has_class", List.of("Weekday"), "days with classes","Objective Value Alias"),
                        new VariableDTO("selection", List.of("Class", "Weekday", "Time", "Duration"), "Lessons","Objective Value Alias")
                )
                .withConstraintModules(
                        new ConstraintModuleDTO("Overlap", "Force classes to not overlap", Set.of("no_overlap"), false)
                )
                .withPreferenceModules(
                        new PreferenceModuleDTO("minimize days with class", "strive for a minimum days with at least one class",
                                Set.of("(20 * sum <d> in DAYS: day_has_class[d])"), 0.5F)
                ).withSets(
                        new SetDTO(new SetDefinitionDTO("CLASS_OPTIONS", List.of("Class", "Weekday", "Time", "Duration"), "Lessons"), null)
                );
    }
    private static ImageDTOBuilder validSoldiersExampleTemplate() {
        return new ImageDTOBuilder()
                .withName("Soldier optimizer")
                .withDescription("does stuff and things")
                .withCode(soldiersExample)
                .withVariables(
                        new VariableDTO("max_shifts_per_soldier", List.of("Count"), "max shifts per soldier","Objective Value Alias"),
                        new VariableDTO("assignment", List.of("Soldier","Station", "Shift"), "assignments","Objective Value Alias"),
                        new VariableDTO("min_hours_between_shifts", List.of("Count"), "min hours between shifts","Objective Value Alias")
                ).withConstraintModules(
                        new ConstraintModuleDTO("Forward Shift Transition", "Force shifts to be forward", Set.of("forward_shift_transition"), false),
                        new ConstraintModuleDTO("No Simultaneous Duties", "Force no simultaneous duties", Set.of("no_simultaneous_duties"), false),
                        new ConstraintModuleDTO("Minimum Shifts", "Force a minimum number of shifts", Set.of("minimum_shifts"), false),
                        new ConstraintModuleDTO("Backward Shift Transition", "Force shifts to be backward", Set.of("backward_shift_transition"), false)
                ).withPreferenceModules(
                        new PreferenceModuleDTO("Shift Transition Consistency", "Force shift transitions to be consistent",
                                Set.of("(min_hours_between_shifts)**2"), 0.5F)
                ).withParameters(
                        new ParameterDTO(new ParameterDefinitionDTO("MIN_HOURS_BETWEEN_SHIFTS","Length","Minimal hours between shifts"), null)
                ).withSets(
                        new SetDTO(new SetDefinitionDTO("SOLDIERS", List.of("Soldier"), "Soldiers"), null),
                        new SetDTO(new SetDefinitionDTO("STATIONS", List.of("Station"), "Stations"), null)
                );
    }
    private static Stream<ImageDTO> validExampleImagesStream() {
        return Stream.of(
                validClassesExampleTemplate().build(),
                validSoldiersExampleTemplate().build()
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
                                            new VariableDTO("day_has_class", List.of("UNKNOWN"), "day_has_class","Objective Value"),
                                            new VariableDTO("selection", List.of("TEXT","TEXT","INT","FLOAT"), "selection","Objective Value")
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
                                            new VariableDTO("max_shifts_per_soldier", List.of(), "max_shifts_per_soldier", "Objective Value"),
                                            new VariableDTO("assignment", List.of("TEXT", "TEXT", "INT"), "assignment","Objective Value"),
                                            new VariableDTO("min_hours_between_shifts", List.of(), "min_hours_between_shifts","Objective Value"),
                                            new VariableDTO("transition", List.of("TEXT", "TEXT", "INT", "TEXT", "INT") ,"transition","Objective Value"),
                                            new VariableDTO("min_shifts_per_soldier", List.of(), "min_shifts_per_soldier","Objective Value")
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
            List<String> classesValues= List.of(
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
            );
            List<String> soldiersValues= List.of("\"Ben\"", "\"Dan\"", "\"Ron\"", "\"Nir\"", "\"Niv\"", "\"Avi\"", "\"Shlomo\"");
            List<String> stationValues= List.of("\"Shin Gimel\"", "\"Fillbox\"");

            Stream<CreateImageCase> validClassesStream = validClassesImageStream().map(imageBuilder -> new CreateImageCase(imageBuilder.build(),
                    imageBuilder.withSetValues("CLASS_OPTIONS",classesValues).build()));
            Stream<CreateImageCase> validSoldiersStream = validSoldiersImageStream().map(imageBuilder -> new CreateImageCase(imageBuilder.build(),
                    imageBuilder.withSetValues("SOLDIERS",soldiersValues)
                            .withSetValues("STATIONS",stationValues).build()));

            return Stream.concat(validClassesStream,validSoldiersStream);
        }
        static Stream<ImageDTOBuilder> validClassesImageStream() {
            return Stream.of(
                    validClassesExampleTemplate()
                            .withName("Updated Name"),
                    validClassesExampleTemplate()
                            .withDescription("Updated description"),
                    validClassesExampleTemplate()
                            .withVariableAlias("selection", "Updated Alias"),
                    validClassesExampleTemplate()
                            .withVariableObjectiveValueAlias("selection", "Updated Objective Value"),
                    validClassesExampleTemplate()
                            .withConstraintModuleName("Overlap", "Updated Constraint Name"),
                    validClassesExampleTemplate()
                            .withConstraintModuleDescription("Overlap", "Updated constraint description"),
                    validClassesExampleTemplate()
                            .withPreferenceModuleName("minimize days with class", "Updated Preference Name"),
                    validClassesExampleTemplate()
                            .withPreferenceModuleDescription("minimize days with class", "Updated preference description"),
                    validClassesExampleTemplate()
                            .withPreferenceModuleScalar("minimize days with class", 0.5f),
                    validClassesExampleTemplate()
                            .withName("Updated Name")
                            .withDescription("Updated description"),
                    validClassesExampleTemplate()
                            .withVariableAlias("selection", "Updated Alias")
                            .withVariableObjectiveValueAlias("selection", "Updated Objective Value"),
                    validClassesExampleTemplate()
                            .withConstraintModuleName("Overlap", "Updated Constraint")
                            .withConstraintModuleDescription("Overlap", "Updated constraint description"),
                    validClassesExampleTemplate()
                            .withPreferenceModuleName("minimize days with class", "Updated Preference")
                            .withPreferenceModuleDescription("minimize days with class", "Updated preference description"),
                    validClassesExampleTemplate()
                            .withName("x".repeat(255)),
                    validClassesExampleTemplate()
                            .withDescription("x".repeat(4000)),
                    validClassesExampleTemplate()
                            .withVariableAlias("selection", "x".repeat(255)),
                    validClassesExampleTemplate()
                            .withVariableObjectiveValueAlias("selection", "x".repeat(255)),
                    validClassesExampleTemplate()
                            .withConstraintModuleDescription("Overlap", "x".repeat(4000)),
                    validClassesExampleTemplate()
                            .withName("Updated Name")
                            .withVariableAlias("selection", "Updated Alias")
                            .withConstraintModuleName("Overlap", "Updated Constraint"),
                    validClassesExampleTemplate()
                            .withDescription("Updated description")
                            .withVariableObjectiveValueAlias("selection", "Updated Objective")
                            .withPreferenceModuleScalar("minimize days with class", 0.5f),
                    validClassesExampleTemplate()
                            .withName("x".repeat(255))
                            .withDescription("x".repeat(4000)),
                    validClassesExampleTemplate()
                            .withVariableAlias("selection", "x".repeat(255))
                            .withVariableObjectiveValueAlias("selection", "x".repeat(255)),
                    validClassesExampleTemplate()
                            .withName("x".repeat(255))
                            .withVariableAlias("selection", "Updated Alias")
                            .withConstraintModuleName("Overlap", "Updated Constraint"),
                    validClassesExampleTemplate()
                            .withDescription("x".repeat(4000))
                            .withVariableObjectiveValueAlias("selection", "x".repeat(255))
                            .withPreferenceModuleScalar("minimize days with class", 0.5f),
                    validClassesExampleTemplate()
                            .withName("x".repeat(255))
                            .withDescription("x".repeat(4000))
                            .withVariableAlias("selection", "x".repeat(255))
                            .withVariableObjectiveValueAlias("selection", "x".repeat(255))
                            .withConstraintModuleDescription("Overlap", "x".repeat(4000))
                            .withPreferenceModuleScalar("minimize days with class", 0.5f)
            );
        }

        static Stream<ImageDTOBuilder> validSoldiersImageStream() {
            return Stream.of(
                    // 1. Basic atomic changes
                    validSoldiersExampleTemplate()
                            .withName("Updated Soldier Schedule"),
                    validSoldiersExampleTemplate()
                            .withDescription("Updated description"),
                    validSoldiersExampleTemplate()
                            .withParameterValue("MIN_HOURS_BETWEEN_SHIFTS", "12"),
                    validSoldiersExampleTemplate()
                            .withParameterAlias("MIN_HOURS_BETWEEN_SHIFTS", "Rest Period"),
                    validSoldiersExampleTemplate()
                            .withVariableAlias("assignment", "Updated Assignment"),
                    validSoldiersExampleTemplate()
                            .withConstraintModuleName("Forward Shift Transition", "Updated Transition"),

                    // 2. Simple combinations
                    validSoldiersExampleTemplate()
                            .withName("Updated Schedule")
                            .withDescription("Updated description"),
                    validSoldiersExampleTemplate()
                            .withParameterValue("MIN_HOURS_BETWEEN_SHIFTS", "12")
                            .withParameterAlias("MIN_HOURS_BETWEEN_SHIFTS", "Rest Period"),

                    // 3. Edge cases
                    validSoldiersExampleTemplate()
                            .withName("x".repeat(255)),
                    validSoldiersExampleTemplate()
                            .withDescription("x".repeat(4000)),
                    validSoldiersExampleTemplate()
                            .withParameterAlias("MIN_HOURS_BETWEEN_SHIFTS", "x".repeat(255)),

                    // 4. Complex combinations
                    validSoldiersExampleTemplate()
                            .withName("Updated Schedule")
                            .withParameterValue("MIN_HOURS_BETWEEN_SHIFTS", "12")
                            .withVariableAlias("assignment", "New Assignment"),

                    // 5. Maximum complexity
                    validSoldiersExampleTemplate()
                            .withName("x".repeat(255))
                            .withDescription("x".repeat(4000))
                            .withParameterValue("MIN_HOURS_BETWEEN_SHIFTS", "16")
                            .withParameterAlias("MIN_HOURS_BETWEEN_SHIFTS", "x".repeat(255))
                            .withVariableAlias("assignment", "x".repeat(255))
                            .withConstraintModuleName("Forward Shift Transition", "x".repeat(255))
            );
        }
        static Stream<ImageDTO> invalidCaseStream() {
        return Stream.concat(invalidClassesStream(),invalidSoldiersExampleImages());
        }
        static Stream<ImageDTO> invalidClassesStream(){
            return Stream.of(
            validClassesExampleTemplate().withoutVariables("day_has_class","selection").build(), //1 no variables
                    validClassesExampleTemplate().withVariableName("day_has_class","invalid name").build(), //2 invalid variable name
                    validClassesExampleTemplate().withVariables(new VariableDTO(null,List.of(),"","")).build(), //3 nulls
                    validClassesExampleTemplate().withVariables(new VariableDTO("",List.of(),"","")).build(), //4 empty names
                    validClassesExampleTemplate().withVariableStructure("day_has_class",null).build(), //5 null structure
                    validClassesExampleTemplate().withVariableObjectiveValueAlias("day_has_class","x".repeat(300)).build(), //6 stub, last case was valid
                    validClassesExampleTemplate().withVariableObjectiveValueAlias("day_has_class","x".repeat(256)).build(), // 7 long obj alias
                    validClassesExampleTemplate().withConstraintModuleConstraints("Overlap",null).build(), //8 null constraints
                    validClassesExampleTemplate().withConstraintModuleConstraints("Overlap",Set.of()).build(), //9 empty constraints
                    validClassesExampleTemplate().withConstraintModuleConstraints("Overlap",Set.of("invalid")).build(), //10 invalid constraint
                    validClassesExampleTemplate().withConstraintModuleName("Overlap",null).build(), //11 null name
                    validClassesExampleTemplate().withConstraintModuleName("Overlap","x".repeat(256)).build(), //12 long name
                    validClassesExampleTemplate().withConstraintModuleDescription("Overlap",null).build(), //13 null description
                    validClassesExampleTemplate().withConstraintModuleDescription("Overlap","x".repeat(4001)).build(), //14 long description
                    validClassesExampleTemplate().withPreferenceModuleName("minimize days with class",null).build(), //15 null name
                    validClassesExampleTemplate().withPreferenceModuleName("minimize days with class","x".repeat(256)).build(), //16 long name
                    validClassesExampleTemplate().withPreferenceModuleDescription("minimize days with class",null).build(), //17 null description
                    validClassesExampleTemplate().withPreferenceModuleDescription("minimize days with class","x".repeat(4001)).build(), //18 long description
                    validClassesExampleTemplate().withPreferenceModulePreferences("minimize days with class",null).build(), //19 null preferences
                    validClassesExampleTemplate().withPreferenceModulePreferences("minimize days with class",Set.of()).build(), //20 empty preferences
                    validClassesExampleTemplate().withPreferenceModulePreferences("minimize days with class",Set.of("invalid pref")).build(), //21 invalid preference
                    validClassesExampleTemplate().withSetName("CLASS_OPTIONS",null).build(), //22 null name
                    validClassesExampleTemplate().withSetStructureAlias("CLASS_OPTIONS",null).build(), //23 null structure
                    validClassesExampleTemplate().withSetStructureAlias("CLASS_OPTIONS",List.of("x".repeat(256))).build(), //24 long structure alias
                    validClassesExampleTemplate().withSetAlias("CLASS_OPTIONS","x".repeat(256)).build(), //25 long alias
                    validClassesExampleTemplate().withName(null).build(), //32 null image name
                    validClassesExampleTemplate().withName("").build(), //33 empty image name
                    validClassesExampleTemplate().withName("x".repeat(256)).build(), //34 long image name
                    validClassesExampleTemplate().withDescription(null).build(), //35 null image description
                    validClassesExampleTemplate().withDescription("x".repeat(4001)).build(), // 36 long image description
                    validClassesExampleTemplate().withCode(null).build(), //37 null image code
                    validClassesExampleTemplate().withCode("this shouldn't compile!").build() //38 invalid image code
            );
        }
        static Stream<ImageDTO> invalidSoldiersExampleImages(){
            return Stream.of(
                    validSoldiersExampleTemplate().withParameterName("MIN_HOURS_BETWEEN_SHIFTS",null).build(), //27 null name
                    validSoldiersExampleTemplate().withParameterStructureAlias("MIN_HOURS_BETWEEN_SHIFTS","x".repeat(256)).build(), //28 long structure alias
                    validSoldiersExampleTemplate().withParameterAlias("MIN_HOURS_BETWEEN_SHIFTS","x".repeat(256)).build(), //29 long alias
                    validSoldiersExampleTemplate().withParameterStructureAlias("MIN_HOURS_BETWEEN_SHIFTS","x".repeat(256)).build() //31 long structure alias)
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
                    baseUri + "/view?page=0",
                    ImagesDTO.class
            );
            assertTrue(fetchImageResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(fetchImageResponse.getBody());
            ImagesDTO actualImages = fetchImageResponse.getBody();
            assertEquals(1, actualImages.images().size());
            ImageDTO actualImage = actualImages.images().get(imageId);
            //assertEquals(createImageCase.expected, actualImage);
            assertEquals(createImageCase.expected.code(), actualImage.code());
            assertEquals(createImageCase.expected.description(), actualImage.description());
            assertEquals(createImageCase.expected.name(), actualImage.name());
            assertEquals(createImageCase.expected.constraintModules(), actualImage.constraintModules());
            assertEquals(createImageCase.expected.preferenceModules(), actualImage.preferenceModules());
            assertEquals(createImageCase.expected.variables(), actualImage.variables());
            assertEquals(createImageCase.expected.sets().stream().map(SetDTO::setDefinition).collect(Collectors.toSet()), actualImage.sets().stream().map(SetDTO::setDefinition).collect(Collectors.toSet()));
            assertEquals(createImageCase.expected.parameters().stream().map(ParameterDTO::parameterDefinition).collect(Collectors.toSet()), actualImage.parameters().stream().map(ParameterDTO::parameterDefinition).collect(Collectors.toSet()));

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
                    baseUri + "/view?page=0",
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
                    baseUri + "/view?page=0",
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
                        baseUri +"/view?page="+page,
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
                        baseUri +"/view?page="+(saveCount / DEFAULT_PAGE_SIZE),
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
                    baseUri +"/view?page="+(saveCount / DEFAULT_PAGE_SIZE + 1),
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
        record ConfigureImageCase(ImageDTO originalImage, ImageDTO updatedImage) {}
        static Stream<ConfigureImageCase> invalidConfigureCases() {
            Stream<ConfigureImageCase> invalidClassesCases = invalidClassesStream()
                    .map(invalidImage -> new ConfigureImageCase(
                            validClassesExampleTemplate().build(),
                            invalidImage
                    ));

            Stream<ConfigureImageCase> invalidSoldiersCases = TestCreateImage.invalidSoldiersExampleImages()
                    .map(invalidImage -> new ConfigureImageCase(
                            validSoldiersExampleTemplate().build(),
                            invalidImage
                    ));

            return Stream.concat(invalidClassesCases, invalidSoldiersCases);
        }

        static Stream<ConfigureImageCase> validConfigureCases() {
            Stream<ConfigureImageCase> validClassesCases = TestCreateImage.validClassesImageStream()
                    .map(imageBuilder -> new ConfigureImageCase(
                            validClassesExampleTemplate().build(),
                            imageBuilder.build()
                    ));

            Stream<ConfigureImageCase> validSoldiersCases = TestCreateImage.validSoldiersImageStream()
                    .map(imageBuilder -> new ConfigureImageCase(
                            validSoldiersExampleTemplate().build(),
                            imageBuilder.build()
                    ));

            return Stream.concat(validClassesCases, validSoldiersCases);

        }

        @ParameterizedTest
        @MethodSource("validConfigureCases")
        @DisplayName("Given valid image update, when configure image, then should succeed")
        void givenValidImageUpdate_whenConfigureImage_thenSuccess(ConfigureImageCase testCase) {
            // create an image
            ResponseEntity<CreateImageResponseDTO> createImageResponse = restTemplate.postForEntity(
                    baseUri,
                    testCase.originalImage(),
                    CreateImageResponseDTO.class
            );
            assertTrue(createImageResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(createImageResponse.getBody());
            String imageId = createImageResponse.getBody().imageId();

            // Configure the image with new data
            ResponseEntity<Void> configureResponse = restTemplate.exchange(
                    baseUri + "/{imageId}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(testCase.updatedImage()),
                    Void.class,
                    imageId
            );
            assertTrue(configureResponse.getStatusCode().is2xxSuccessful());

            // Verify the changes
            ResponseEntity<ImagesDTO> fetchResponse = restTemplate.getForEntity(
                    baseUri + "/view",
                    ImagesDTO.class
            );
            assertTrue(fetchResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(fetchResponse.getBody());

            ImageDTO actualImage = fetchResponse.getBody().images().get(UUID.fromString(imageId));
            assertNotNull(actualImage);
            assertEquals(testCase.updatedImage.code(), actualImage.code());
            assertEquals(testCase.updatedImage.description(), actualImage.description());
            assertEquals(testCase.updatedImage.name(), actualImage.name());
            assertEquals(testCase.updatedImage.constraintModules(), actualImage.constraintModules());
            assertEquals(testCase.updatedImage.preferenceModules(), actualImage.preferenceModules());
            assertEquals(testCase.updatedImage.variables(), actualImage.variables());
            assertEquals(testCase.updatedImage.sets().stream().map(SetDTO::setDefinition).collect(Collectors.toSet()), actualImage.sets().stream().map(SetDTO::setDefinition).collect(Collectors.toSet()));
            assertEquals(testCase.updatedImage.parameters().stream().map(ParameterDTO::parameterDefinition).collect(Collectors.toSet()), actualImage.parameters().stream().map(ParameterDTO::parameterDefinition).collect(Collectors.toSet()));
            testCase.updatedImage.sets().forEach(expectedSet -> {
                if (expectedSet.values() != null) {
                    actualImage.sets().stream()
                            .filter(actualSet -> actualSet.setDefinition().equals(expectedSet.setDefinition()))
                            .findFirst()
                            .ifPresent(actualSet -> assertEquals(expectedSet.values(), actualSet.values()));
                }
            });
            testCase.updatedImage.parameters().forEach(expectedParam -> {
                if (expectedParam.value() != null) {
                    actualImage.parameters().stream()
                            .filter(actualParam -> actualParam.parameterDefinition().equals(expectedParam.parameterDefinition()))
                            .findFirst()
                            .ifPresent(actualParam -> assertEquals(expectedParam.value(), actualParam.value()));
                }
            });

        }
        @ParameterizedTest
        @MethodSource("invalidConfigureCases")
        @DisplayName("Given invalid image update, when configure image, then should fail")
        void givenInvalidImageUpdate_whenConfigureImage_thenFail(ConfigureImageCase testCase) {
            //  create a valid image
            ResponseEntity<CreateImageResponseDTO> createResponse = restTemplate.postForEntity(
                    baseUri,
                    testCase.originalImage(),
                    CreateImageResponseDTO.class
            );
            assertTrue(createResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(createResponse.getBody());
            String imageId = createResponse.getBody().imageId();

            // Try to update with invalid data
            ResponseEntity<Void> configureResponse = restTemplate.exchange(
                    baseUri + "/{imageId}",
                    HttpMethod.PATCH,
                    new HttpEntity<>(testCase.updatedImage()),
                    Void.class,
                    imageId
            );
            assertTrue(configureResponse.getStatusCode().is4xxClientError());

            // Verify the image wasn't changed
            ResponseEntity<ImagesDTO> fetchResponse = restTemplate.getForEntity(
                    baseUri + "/view",
                    ImagesDTO.class
            );
            assertTrue(fetchResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(fetchResponse.getBody());

            ImageDTO unchangedImage = fetchResponse.getBody().images().get(UUID.fromString(imageId));
            assertNotNull(unchangedImage);
            assertEquals(testCase.originalImage.code(), unchangedImage.code());
            assertEquals(testCase.originalImage.description(), unchangedImage.description());
            assertEquals(testCase.originalImage.name(), unchangedImage.name());
            assertEquals(testCase.originalImage.constraintModules(), unchangedImage.constraintModules());
            assertEquals(testCase.originalImage.preferenceModules(), unchangedImage.preferenceModules());
            assertEquals(testCase.originalImage.variables(), unchangedImage.variables());
            assertEquals(testCase.originalImage.sets().stream().map(SetDTO::setDefinition).collect(Collectors.toSet()), unchangedImage.sets().stream().map(SetDTO::setDefinition).collect(Collectors.toSet()));
            assertEquals(testCase.originalImage.parameters().stream().map(ParameterDTO::parameterDefinition).collect(Collectors.toSet()), unchangedImage.parameters().stream().map(ParameterDTO::parameterDefinition).collect(Collectors.toSet()));

        }

        @ParameterizedTest
        @MethodSource("invalidConfigureCases")
        @DisplayName("Given valid image, when configure image with ignore flag, image updated but data remains the same as original")
        void givenValidImageUpdate_whenConfigureImageWithIgnoreDataFlag_thenDataDoesntChange(ConfigureImageCase testCase) {
            //  create a valid image
            ResponseEntity<CreateImageResponseDTO> createResponse = restTemplate.postForEntity(
                    baseUri,
                    testCase.originalImage(),
                    CreateImageResponseDTO.class
            );
            assertTrue(createResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(createResponse.getBody());
            String imageId = createResponse.getBody().imageId();

            ResponseEntity<ImagesDTO> originalFetchResponse = restTemplate.getForEntity(
                    baseUri + "/view",
                    ImagesDTO.class
            );
            assertTrue(originalFetchResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(originalFetchResponse.getBody());
            ImageDTO originalImage = originalFetchResponse.getBody().images().get(UUID.fromString(imageId));

            ResponseEntity<Void> configureResponse = restTemplate.exchange(
                    baseUri + "/{imageId}?ignoreData=true",
                    HttpMethod.PATCH,
                    new HttpEntity<>(testCase.updatedImage()),
                    Void.class,
                    imageId
            );
            assertTrue(configureResponse.getStatusCode().is4xxClientError());

            ResponseEntity<ImagesDTO> fetchResponse = restTemplate.getForEntity(
                    baseUri + "/view",
                    ImagesDTO.class
            );
            assertTrue(fetchResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(fetchResponse.getBody());

            ImageDTO newImage = fetchResponse.getBody().images().get(UUID.fromString(imageId));
            assertNotNull(newImage);
            assertEquals(originalImage.code(), newImage.code());
            assertEquals(originalImage.description(), newImage.description());
            assertEquals(originalImage.name(), newImage.name());
            assertEquals(originalImage.constraintModules(), newImage.constraintModules());
            assertEquals(originalImage.preferenceModules(), newImage.preferenceModules());
            assertEquals(originalImage.variables(), newImage.variables());
            assertEquals(originalImage.sets().stream().map(SetDTO::setDefinition).collect(Collectors.toSet()), newImage.sets().stream().map(SetDTO::setDefinition).collect(Collectors.toSet()));
            assertEquals(originalImage.parameters().stream().map(ParameterDTO::parameterDefinition).collect(Collectors.toSet()), newImage.parameters().stream().map(ParameterDTO::parameterDefinition).collect(Collectors.toSet()));
            originalImage.sets().forEach(originalSet -> {
                assertNotNull(originalSet.values());
                originalSet.values().forEach(Assertions::assertNotNull);
                    newImage.sets().stream()
                            .filter(actualSet -> actualSet.setDefinition().equals(originalSet.setDefinition()))
                            .findFirst()
                            .ifPresent(actualSet -> assertEquals(originalSet.values(), actualSet.values()));
            });
            originalImage.parameters().forEach(originalParams -> {
                assertNotNull(originalParams.value());
                newImage.parameters().stream()
                            .filter(actualParam -> actualParam.parameterDefinition().equals(originalParams.parameterDefinition()))
                            .findFirst()
                            .ifPresent(actualParam -> assertEquals(originalParams.value(), actualParam.value()));

            });
        }

    }
    @Nested
    @DisplayName("Test Model: PATCH "+baseUriTemplate+"/{imageId}/publish")
    class ImagePublishingTest{
        static Stream<ImageDTO> validPublishImageStream(){
            return validExampleImagesStream();
        }
        @ParameterizedTest
        @MethodSource("validPublishImageStream")
        @DisplayName("Given valid image, when publish image, then should not fail")
        void givenValidImage_WhenPublishImage_thenSuccess(ImageDTO imageDTO) {
            ResponseEntity<CreateImageResponseDTO> createImageResponse = restTemplate.postForEntity(
                    baseUri,
                    imageDTO,
                    CreateImageResponseDTO.class
            );
            assertTrue(createImageResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(createImageResponse.getBody());
            ResponseEntity<ImagesDTO> imagesResponseEntityFullPage = restTemplate.getForEntity(
                    baseUri +"/view",
                    ImagesDTO.class
            );
            assertTrue(imagesResponseEntityFullPage.getStatusCode().is2xxSuccessful());
            assertNotNull(imagesResponseEntityFullPage.getBody());
            assertEquals(1, imagesResponseEntityFullPage.getBody().images().size());
            Collection<ImageDTO> image = imagesResponseEntityFullPage.getBody().images().values();
            ResponseEntity<PublishedImagesDTO> fetchImageResponse = restTemplate.getForEntity(
                    baseImageControllerURI+"/view",
                    PublishedImagesDTO.class
            );
            assertTrue(fetchImageResponse.getStatusCode().is2xxSuccessful());
            assertNotNull(fetchImageResponse.getBody());
            PublishedImagesDTO PublishedImages = fetchImageResponse.getBody();
            for (ImageDTO originalImage : image) {
                for(ImageDataDTO publishedImage : PublishedImages.images().values()){
                    assertEquals(originalImage.name(), publishedImage.name());
                    assertEquals(originalImage.description(), publishedImage.description());
                    assertEquals("testUserNickname", publishedImage.authorName());
                    assertEquals(LocalDate.now(), publishedImage.creationDate());
                }
            }
        }
    }
}
