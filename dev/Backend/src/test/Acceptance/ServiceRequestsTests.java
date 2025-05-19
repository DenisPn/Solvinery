package Acceptance;
import User.User;
import Utilities.PersistenceTestsConfiguration;
import Utilities.UnitTestsConfiguration;
import config.KafkaConfig;
import groupId.Controllers.ImageController;
import groupId.DTO.Factories.RecordFactory;
import groupId.DTO.Records.Image.*;
import groupId.DTO.Records.Model.ModelData.*;
import groupId.DTO.Records.Model.ModelDefinition.*;
import groupId.DTO.Records.Requests.Commands.CreateImageFromFileDTO;
import groupId.DTO.Records.Requests.Commands.ImageConfigDTO;
import groupId.DTO.Records.Requests.Responses.CreateImageResponseDTO;
import Image.Image;
import groupId.Controllers.MainController;
import groupId.Services.ImageService;
import groupId.Services.MainService;
import org.junit.jupiter.api.*;

import java.nio.file.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureDataJpa
@ContextConfiguration(classes = {UnitTestsConfiguration.class, PersistenceTestsConfiguration.class})
public class ServiceRequestsTests {
    static String SimpleCodeExample = """
          set mySet := {7,6,4};
          param x := 10;
        \s
          var myVar[mySet] >= 0;
       \s
          subto sampleConstraint:
              myVar[1] + myVar[2] + myVar[3] == x;

          maximize myObjective:
              myVar[3];
           \s
           \s""";
    static Path tmpDirPath;
    static String sourcePath = "src/test/Utilities/ZimplExamples/ExampleZimplProgram.zpl";
    @Autowired
    MainService mainService;
    @Autowired
    ImageService imageService;
    MainController mainController;
    ImageController imageController;
    KafkaConfig kafkaConfig;
    User stubUser;
    @BeforeAll
    public static void setup(){
        //try {
            //System default tmp folder, for now I delete it at end of run, not 100% sure if should
           // tmpDirPath= Files.createDirectories(Paths.get(System.getProperty("java.io.tmpdir")));
            //changed to debug
            tmpDirPath= Paths.get("User/Solutions");
        //}
//        catch (IOException e){
//            fail(e.getMessage());
//        }
    }
    @BeforeEach
    public void setUp(ImageService imageService) {
        mainController = new MainController(mainService);
        imageController=new ImageController(imageService);
        stubUser = new User(UUID.randomUUID(), "stub name", "stub@mail.com", "stub nickname");
    }

    @Test
    public void GivenEmptyZimplFIle_WhenCreatingIMageFrom_CreateEmptyImage(){
        try {
            String data="";
            ResponseEntity<CreateImageResponseDTO> response= imageController.createImage(new CreateImageFromFileDTO(stubUser.getId().toString(),data));

            ModelDTO model= response.getBody().model();
            assertEquals(0, model.constraints().size());
            assertEquals(0, model.preferences().size());
            assertEquals(0, model.variables().size());
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }
    @Test
    public void GivenImageDTO_WhenConfigImage_ImageIsCorrect() {
        /**
         * SET UP
         */
        CreateImageFromFileDTO body = new CreateImageFromFileDTO(stubUser.getId().toString(),SimpleCodeExample);
        try {
            ResponseEntity<CreateImageResponseDTO> response= imageController.createImage(body);
            String someUserId= UUID.randomUUID().toString();
            CreateImageResponseDTO expected = new CreateImageResponseDTO(
                    "some imageId", new ModelDTO(
                    Set.of(new ConstraintDTO("sampleConstraint")),
                    Set.of(new PreferenceDTO("myVar[3]")),
                    Set.of(new VariableDTO("myVar",List.of("mySet"),null)),
                    Map.of("mySet", List.of("INT")),
                    Map.of("x", "INT")
            ));
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().imageId());
            assertEquals(response.getBody().model().constraints(), expected.model().constraints());
            assertEquals(response.getBody().model().preferences(), expected.model().preferences());
            assertEquals(response.getBody().model().variables(), expected.model().variables());
            assertEquals(response.getBody().model().setTypes(), expected.model().setTypes());
            assertEquals(response.getBody().model().paramTypes(), expected.model().paramTypes());

            /**
             * TEST
             */
            Set<ConstraintModuleDTO> constraintModuleDTOs = Set.of(
                    new ConstraintModuleDTO("Test module", "PeanutButter",
                            Set.of("sampleConstraint")));
            Set<PreferenceModuleDTO> preferenceModuleDTOs = Set.of(
                    new PreferenceModuleDTO("Test module", "PeanutButter",
                            Set.of("myVar[3]")));
            //VariableModuleDTO variableModuleDTO = new VariableModuleDTO(Set.of("myVar"),Map.of("myVar","test_alias"));
            Set<VariableDTO> variableDTOS = new HashSet<>();
            variableDTOS.add(new VariableDTO("myVar", List.of("mySet"), "test_alias"));
            Set<SetDTO> setDTOs = Set.of(new SetDTO(new SetDefinitionDTO("mySet",List.of("INT"),null),List.of()));
            Set<ParameterDTO> parameterDTOS = Set.of(new ParameterDTO(new ParameterDefinitionDTO("X", "INT",null),""));
            ImageDTO imageDTO = new ImageDTO(variableDTOS, constraintModuleDTOs, preferenceModuleDTOs,setDTOs,parameterDTOS);
            ImageConfigDTO configDTO= new ImageConfigDTO(someUserId,response.getBody().imageId(),imageDTO);
            ResponseEntity<Void> response2= imageController.configureImage(configDTO);
            assertEquals(HttpStatus.OK, response2.getStatusCode());
            Image image= imageService.getImage(response.getBody().imageId());
            assertNotNull(image);
            ImageDTO actual= RecordFactory.makeDTO(image);
            assertEquals(imageDTO, actual);

        } catch (Exception e) {
            fail(e.getMessage());
        }

    }
    @Disabled("Awaiting refactoring finish")
    @Test
    public void testSolve_Simple() {
       /* try {
            CreateImageResponseDTO responseDTO=imageService.createImageFromFile(SimpleCodeExample);
            InputDTO input=new InputDTO(Map.of("mySet",List.of(List.of("1"),List.of("2"),List.of("3"))),
                    Map.of("x",List.of("10")),
                    List.of(),List.of());
            SolveCommandDTO solveCommandDTO=new SolveCommandDTO(responseDTO.imageId(),input,600);

            ImageDTO imageDTO=new ImageDTO(Set.of(),Set.of(),Set.of(),Set.of(),Set.of());
            ImageConfigDTO config= new ImageConfigDTO(responseDTO.imageId(),imageDTO);
            imageController.overrideImage(config);
            SolutionDTO solution= mainController.solve(solveCommandDTO);
            assertEquals(Set.of(new SolutionValueDTO(List.of("3"),10)),solution.solution().get("myVar").solutions());
            assertEquals(List.of("test_alias"),solution.solution().get("myVar").setStructure());

        } catch (Exception e) {
            fail(e.getMessage());
        }*/
    }

}
