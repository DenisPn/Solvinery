package Persistence.Entities.Image;

import Persistence.Entities.Image.Data.ParameterEntity;
import Persistence.Entities.Image.Data.SetEntity;
import Persistence.Entities.Image.Data.VariableEntity;
import Persistence.Entities.Image.Operational.ConstraintEntity;
import Persistence.Entities.Image.Operational.ConstraintModuleEntity;
import Persistence.Entities.Image.Operational.PreferenceEntity;
import Persistence.Entities.Image.Operational.PreferenceModuleEntity;
import Persistence.Entities.UserEntity;
import Persistence.Repositories.ImageRepository;
import Persistence.Repositories.UserRepository;
import Utilities.Configs.PersistenceTestsConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureDataJpa
@ContextConfiguration(classes = {PersistenceTestsConfiguration.class})
class ImageEntityTest {

    @Autowired
    ImageRepository imageRepository;
    @Autowired
    UserRepository userRepository;

    @NonNull
    public UserEntity makeUserStub () {
        String username = "stub username";
        String email = "stub@stubber.stubbinson";
        String password = "stubpass123456";
        UserEntity userEntity= new UserEntity(username, email, password);
        userEntity= userRepository.save(userEntity);
        return userEntity;
    }

    @NonNull
    public ImageEntity makeStub () {
        // Persist parent entity without child entities to generate ID
        ImageEntity stub = new ImageEntity();
        imageRepository.save(stub); // Save empty image to generate id
        UUID imageId = stub.getId();
        UserEntity userStub = makeUserStub();
        // Validate entity is saved correctly
        assertNotNull(imageRepository.findById(imageId).orElse(null));

        // Initialize children with the auto-generated ID
        LinkedList<String> setData=new LinkedList<>();
        setData.add("data1"); setData.add("data2");
        ParameterEntity param = new ParameterEntity(imageId, "paramName", "p-structure", "p-data", "p-alias");
        SetEntity set = new SetEntity(imageId, "setName", "s-structure", setData, "s-alias");
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");
        VariableEntity var = new VariableEntity(imageId, "varName", "v-alias",structure);
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
        String emptyZimplCode= "";
        String nameStub = "stub name";
        String descriptionStub = "stub description";
        LocalDateTime dateStub = LocalDateTime.now();
        // Add children to the parent
        stub.setAll(nameStub,descriptionStub,dateStub,preferenceModules, constraintModules, variables, params, sets,emptyZimplCode,userStub);
        return stub;
    }
    @Test
    @Transactional
    void GivenNonExistentImageEntityId_WhenFindById_ThenResultIsNull () {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        ImageEntity result = imageRepository.findById(nonExistentId).orElse(null);

        // Assert
        assertNull(result);
    }

    @Test
    @Transactional
    void GivenValidImageEntity_WhenSavedWithChildren_ThenChildrenArePersisted () {
        // Arrange
        ImageEntity validEntity = makeStub();
        imageRepository.save(validEntity);

        // Act
        ImageEntity retrievedEntity = imageRepository
                .findById(validEntity.getId())
                .orElse(null);

        // Assert
        assertNotNull(retrievedEntity);
        assertEquals(validEntity.getActiveParams(), retrievedEntity.getActiveParams());
        assertEquals(validEntity.getActiveSets(), retrievedEntity.getActiveSets());
    }


    @Test
    @Transactional
    void GivenImageEntity_WhenUpdatingChildSet_ThenPersistenceIsReflected () {
        // Arrange
        ImageEntity validEntity = makeStub();
        imageRepository.save(validEntity);
        List<String> updatedData = List.of("updated1", "updated2");
        SetEntity updatedSet = new SetEntity(validEntity.getId(), "updatedSet", "updated-structure", updatedData, "updatedAlias");
        validEntity.addSet(updatedSet);

        // Act
        imageRepository.save(validEntity);
        ImageEntity retrievedEntity = imageRepository.findById(validEntity.getId()).orElse(null);

        // Assert
        assertNotNull(retrievedEntity);
        assertTrue(retrievedEntity.getActiveSets().stream().anyMatch(set -> "updatedSet".equals(set.getName())));
    }



    
    @Test
    @Transactional
    void GivenTwoIdenticalImageEntities_WhenEqualsIsCalled_ThenTheyAreEqual () {
        // Arrange
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");
        List<String> structure1= new ArrayList<>();
        structure1.add("structure1"); structure1.add("structure2");
        UUID imageId = UUID.randomUUID();
        ImageEntity entity1 = new ImageEntity();
        entity1.addVariable(new VariableEntity(imageId, "varName", "varAlias",structure) );

        ImageEntity entity2 = new ImageEntity();
        entity2.addVariable(new VariableEntity(imageId, "varName","varAlias",structure1) );

        // Act & Assert
        assertEquals(entity1, entity2);
    }

    @Test
    @Transactional
    void GivenTwoDifferentImageEntities_WhenEqualsIsCalled_ThenTheyAreNotEqual () {
        // Arrange
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");
        List<String> structure1= new ArrayList<>();
        structure1.add("structure1"); structure1.add("structure2");
        UUID imageId = UUID.randomUUID();
        ImageEntity entity1 = new ImageEntity();
        entity1.addVariable(new VariableEntity(imageId, "varName1", "varAlias1",structure) );

        ImageEntity entity2 = new ImageEntity();
        entity2.addVariable(new VariableEntity(imageId, "varName2","varAlias2",structure1) );

        // Act & Assert
        assertNotEquals(entity1, entity2);
    }
    
    @Test
    @Transactional
    void GivenValidImageEntity_WhenSave_ThenEntityIsPersisted () {
        //Given
        ImageEntity validEntity = makeStub(); 
        imageRepository.save(validEntity);

        // When - Save parent with children
        ImageEntity retrievedEntity = imageRepository.findById(validEntity.getId()).orElse(null);

        // Then
        assertEquals(validEntity, retrievedEntity);
    }

    @Test
    @Transactional
    void GivenValidImageEntity_WhenDelete_ThenEntityIsDeleted () {
        //given
        ImageEntity validEntity = makeStub();
        imageRepository.save(validEntity);

        // When
        imageRepository.deleteById(validEntity.getId());
        ImageEntity retrievedEntity = imageRepository.findById(validEntity.getId()).orElse(null);

        // Then
        assertNull(retrievedEntity);
    }

    @Test
    @Transactional
    void GivenValidImageEntity_WhenUpdateSet_ThenEntityIsUpdated () {
        //given
        ImageEntity validEntity = makeStub();
        imageRepository.save(validEntity);

        // When
        ArrayList<String> data = new ArrayList<>();
        data.add("newData");
        data.add("newData2");
        SetEntity newSet = new SetEntity(validEntity.getId(), "newSet", "s-structure", data, "newAlias");
        validEntity.addSet(newSet);
        imageRepository.save(validEntity);

        ImageEntity retrievedEntity = imageRepository.findById(validEntity.getId()).orElse(null);

        // Then
        assertEquals(validEntity, retrievedEntity);

        // Ensure new set is updated in the entity
        assertNotNull(retrievedEntity);
        assertTrue(retrievedEntity.getActiveSets().stream().anyMatch(set -> set.getName().equals("newSet")));
    }


    @Test
    @Transactional
    void GivenValidImageEntity_WhenUpdateChildEntity_ThenUpdateIsPersisted () {
        // Arrange
        ImageEntity validEntity = makeStub();
        imageRepository.save(validEntity);

        // Update a child entity
        ParameterEntity paramToUpdate = validEntity.getActiveParams().iterator().next();
        paramToUpdate.setAlias("updatedAlias");

        imageRepository.save(validEntity);

        // Act
        ImageEntity retrievedEntity = imageRepository.findById(validEntity.getId()).orElse(null);

        // Assert - ensure update is reflected in the persisted entity
        assertNotNull(retrievedEntity);
        assertTrue(retrievedEntity.getActiveParams().stream()
                .anyMatch(param -> "updatedAlias".equals(param.getAlias())));
    }

    @Test
    @Transactional
    void GivenChildEntityAttribute_WhenSearchByAttribute_ThenParentIsRetrieved () {
        // Arrange
        ImageEntity validEntity = makeStub();
        imageRepository.save(validEntity);

        // Use a child entity attribute to fetch the parent
        ParameterEntity targetParam = validEntity.getActiveParams().iterator().next();
        UUID imageId = targetParam.getUUID();

        // Act
        ImageEntity retrievedEntity = imageRepository.findById(imageId).orElse(null);

        // Assert
        assertNotNull(retrievedEntity);
        assertEquals(validEntity.getId(), retrievedEntity.getId());
    }

    @Test
    @Transactional
    void GivenValidImageEntity_WhenDeleteAllChildren_ThenNoChildEntitiesRemain () {
        // Arrange
        ImageEntity validEntity = makeStub();
        imageRepository.save(validEntity);

        // Act - remove all child entities from parent
        validEntity.setAll("","",LocalDateTime.now(),new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(),"",makeUserStub());
        imageRepository.save(validEntity);

        ImageEntity retrievedEntity = imageRepository.findById(validEntity.getId()).orElse(null);

        // Assert - ensure parent is retained but child entities are removed
        assertNotNull(retrievedEntity);
        assertTrue(retrievedEntity.getActiveParams().isEmpty());
        assertTrue(retrievedEntity.getActiveSets().isEmpty());
        assertTrue(retrievedEntity.getVariables().isEmpty());
        assertTrue(retrievedEntity.getConstraintModules().isEmpty());
        assertTrue(retrievedEntity.getPreferenceModules().isEmpty());
        assertTrue(retrievedEntity.getOriginalCode().isEmpty());
    }
}