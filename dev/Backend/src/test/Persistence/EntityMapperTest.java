package Persistence;

import Image.Image;
import Image.Modules.Grouping.ConstraintModule;
import Image.Modules.Grouping.PreferenceModule;
import Image.Modules.Single.ParameterModule;
import Image.Modules.Single.SetModule;
import Image.Modules.Single.VariableModule;
import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;
import Model.Data.Types.ModelPrimitives;
import Persistence.Entities.Image.Data.ParameterEntity;
import Persistence.Entities.Image.Data.SetEntity;
import Persistence.Entities.Image.Data.VariableEntity;
import Persistence.Entities.Image.ImageEntity;
import Persistence.Entities.Image.Operational.ConstraintEntity;
import Persistence.Entities.Image.Operational.ConstraintModuleEntity;
import Persistence.Entities.Image.Operational.PreferenceEntity;
import Persistence.Entities.Image.Operational.PreferenceModuleEntity;
import Persistence.Entities.UserEntity;
import Persistence.Repositories.ImageRepository;
import Persistence.Repositories.UserRepository;
import User.User;
import Utilities.Configs.PersistenceTestsConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the `toDomain` method of the `EntityMapper` class.
 * <p>
 * The `toDomain` method takes various entity objects (e.g., `UserEntity`, `ModelParamEntity`, etc.)
 * and converts them into domain objects (e.g., `User`, `ModelParameter`, etc.)
 * that can be used in other parts of the system. These tests confirm that the conversions
 * are working as expected by validating the mappings between fields in the entity and domain objects.
 * Relevant Tests will also check for persistence validity (domain -> entity -> saveDB -> readDB -> entity -> domain)
 */
@SpringBootTest
@AutoConfigureDataJpa
@ContextConfiguration(classes = {PersistenceTestsConfiguration.class})
public class EntityMapperTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    static String simpleCodeExample = """
                param x := 2;
                set mySet := {1,2,3};

                var myVar[mySet];

                subto sampleConstraint:
                    myVar[x] == mySet[1];

                maximize myObjective:
                    1;
            """;
    public UserEntity makeUserStub () {
        String username = "stub username";
        String email = "stub@stubber.stubbinson";
        String password = "stubpass123456";
        UserEntity userEntity= new UserEntity(username, email, password);
        userEntity= userRepository.save(userEntity);
        return userEntity;
    }
    public Image makeImageStub () {
        // Initialize children with appropriate data
        LinkedList<String> setData = new LinkedList<>();
        setData.add("data1");
        setData.add("data2");
        ParameterModule param = new ParameterModule(new ModelParameter("paramName", ModelPrimitives.INT, "p-data"), "p-alias");
        SetModule set = new SetModule(new ModelSet("setName", ModelPrimitives.INT, setData), "s-alias");
        List<String> structure = new ArrayList<>();
        structure.add("structure1");
        structure.add("structure2");
        List<String> basicSets= new ArrayList<>();
        basicSets.add("set1"); basicSets.add("set2");
        VariableModule var = new VariableModule(new Variable("varName", structure,basicSets), "v-alias");
        Constraint constraint = new Constraint("constraint");
        Preference preference = new Preference("preference");

        Set<SetModule> sets = new HashSet<>();
        sets.add(set);
        Set<ParameterModule> params = new HashSet<>();
        params.add(param);
        Set<VariableModule> variables = new HashSet<>();
        variables.add(var);
        Set<Constraint> constraints = new HashSet<>();
        constraints.add(constraint);
        Set<Preference> preferences = new HashSet<>();
        preferences.add(preference);

        ConstraintModule constraintModule = new ConstraintModule("conModule", "conModDesc", constraints);
        PreferenceModule preferenceModule = new PreferenceModule("preModule", "preModDesc", preferences);

        Set<ConstraintModule> constraintModules = new HashSet<>();
        Set<PreferenceModule> preferenceModules = new HashSet<>();
        constraintModules.add(constraintModule);
        preferenceModules.add(preferenceModule);
        String name= "stub name";
        String description= "stub description";
        LocalDateTime creationDate= LocalDateTime.now();
        // Create and return the Image object
        return new Image(simpleCodeExample,name,description,creationDate, constraintModules, preferenceModules, sets, params, variables);
    }

    public ImageEntity makeEntityStub() {

        // Persist parent entity without child entities to generate ID
        UserEntity userStub = makeUserStub();

        ImageEntity stub = new ImageEntity();
        imageRepository.save(stub); // Save empty image to generate id
        UUID imageId = stub.getId();

        // Validate entity is saved correctly
        assertNotNull(imageRepository.findById(imageId).orElse(null));

        // Initialize children with the auto-generated ID
        LinkedList<String> setData=new LinkedList<>();
        setData.add("data1"); setData.add("data2");
        ParameterEntity param = new ParameterEntity(imageId, "paramName", "INT", "p-data", "p-alias");
        SetEntity set = new SetEntity(imageId, "setName", "INT", setData, "s-alias");
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");
        List<String> basicSets= new ArrayList<>();
        basicSets.add("set1"); basicSets.add("set2");
        VariableEntity var = new VariableEntity(imageId, "varName",structure,basicSets, "v-alias");
        ConstraintEntity constraint = new ConstraintEntity("constraint");
        PreferenceEntity preference = new PreferenceEntity("preference");
        HashSet<SetEntity> sets= new HashSet<>();
        sets.add(set);
        HashSet<ParameterEntity> params= new HashSet<>();
        params.add(param);
        HashSet<VariableEntity> variables = new HashSet<>();
        variables.add(var);
        HashSet<ConstraintEntity> constraints = new HashSet<>();
        constraints.add(constraint);
        HashSet<PreferenceEntity> preferences = new HashSet<>();
        preferences.add(preference);
        ConstraintModuleEntity constraintModule = new ConstraintModuleEntity(imageId, "conModule", "conModDesc", constraints);
        PreferenceModuleEntity preferenceModule = new PreferenceModuleEntity(imageId, "preModule", "preModDesc", preferences);
        HashSet<ConstraintModuleEntity> constraintModules = new HashSet<>();
        HashSet<PreferenceModuleEntity> preferenceModules = new HashSet<>();
        constraintModules.add(constraintModule);
        preferenceModules.add(preferenceModule);
        String nameStub = "stub name";
        String descriptionStub = "stub description";
        LocalDateTime dateStub = LocalDateTime.now();
        // Add children to the parent
        //    public void setAll(String name, String description, LocalDateTime creationDate, Set<PreferenceModuleEntity> preferenceModuleEntities, Set<ConstraintModuleEntity> constraintModuleEntities, Set<VariableEntity> variableEntities, Set<ParameterEntity> paramEntities, Set<SetEntity> setEntities, String sourceCode, UserEntity user)
        stub.setAll(nameStub,descriptionStub,dateStub,preferenceModules, constraintModules, variables, params, sets,simpleCodeExample,userStub);
        return stub;
    }
    @Transactional
    @Test
    public void givenUserEntity_whenConvertedToDomain_thenShouldMatchFields () {
        // Given
        UserEntity entity = new UserEntity("testUser", "test@example.com", "password123");

        // When
        User user = EntityMapper.toDomain(entity);

        // Then
        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
    }
    @Transactional
    @Test
    public void givenImageEntity_whenConvertedToDomain_thenShouldMatchFields () {
        // Given
        ImageEntity entity = makeEntityStub();

        // When
        Image image = EntityMapper.toDomain(entity);

        // Then
        assertNotNull(image);
        assertEquals(simpleCodeExample, image.getSourceCode());
    }
    @Transactional
    @Test
    public void givenImage_whenConvertedToEntity_thenShouldMatchFields () {
        // Given
        Image image = makeImageStub();
        UUID imageId = UUID.randomUUID();
        UserEntity user = new UserEntity("testUser", "test@example.com", "password123");

        // When
        ImageEntity entity = EntityMapper.toEntity(user,image, imageId);

        // Then
        assertNotNull(entity);
        assertEquals(simpleCodeExample, entity.getZimplCode());
    }

    @Transactional
    @Test
    public void givenImage_whenPersistedThroughEntityMapper_thenShouldMatch () {
        // Given
        Image image = makeImageStub();
        UUID imageId = UUID.randomUUID();
        UserEntity user = makeUserStub();
        ImageEntity imageEntity = EntityMapper.toEntity(user,image, imageId);

        // When
        imageRepository.save(imageEntity);
        ImageEntity savedEntity = imageRepository.findById(imageEntity.getId()).orElse(null);
        Image convertedImage = null;
        if (savedEntity != null) {
            convertedImage = EntityMapper.toDomain(savedEntity);
        }

        // Then
        assertNotNull(savedEntity);
        assertNotNull(convertedImage);
        assertEquals(image.getSourceCode(), convertedImage.getSourceCode());
    }

    @Test
    public void givenUser_whenPersistedThroughEntityMapper_thenShouldMatch () {
        // Given
        User user = new User(UUID.randomUUID(),"testUser", "testUser@example.com");
        UserEntity userEntity = EntityMapper.toEntity(user, "password123");

        // When
        userRepository.save(userEntity);
        UserEntity savedEntity = userRepository.findById(userEntity.getId()).orElse(null);
        User convertedUser = null;
        if (savedEntity != null) {
            convertedUser = EntityMapper.toDomain(savedEntity);
        }

        // Then
        assertNotNull(savedEntity);
        assertNotNull(convertedUser);
        assertEquals(user.getUsername(), convertedUser.getUsername());
        assertEquals(user.getEmail(), convertedUser.getEmail());
    }

    @Test
    public void givenUserEntityWithNullUsername_whenConverted_toDomain_thenShouldHaveNullUsername () {
        // Given
        UserEntity entity = new UserEntity(null, "test@example.com", "password123");

        // When
        User user = EntityMapper.toDomain(entity);

        // Then
        assertNotNull(user);
        assertNull(user.getUsername());
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    public void givenUserEntityWithNullEmail_whenConverted_toDomain_thenShouldHaveNullEmail () {
        // Given
        UserEntity entity = new UserEntity("testUser", null, "password123");

        // When
        User user = EntityMapper.toDomain(entity);

        // Then
        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
        assertNull(user.getEmail());
    }

    @Test
    public void givenUserEntityWithEmptyValues_whenConverted_toDomain_thenShouldHaveEmptyValues () {
        // Given
        UserEntity entity = new UserEntity("", "", "password123");

        // When
        User user = EntityMapper.toDomain(entity);

        // Then
        assertNotNull(user);
        assertEquals("", user.getUsername());
        assertEquals("", user.getEmail());
    }

}