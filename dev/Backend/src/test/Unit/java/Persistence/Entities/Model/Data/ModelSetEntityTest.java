package Unit.java.Persistence.Entities.Model.Data;

import Persistence.Entities.Model.Data.ModelDataKeyPair;
import Persistence.Entities.Model.Data.ModelSetEntity;
import Persistence.Repositories.Model.Data.SetRepository;
import Utilities.TestsConfiguration;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureDataJpa
@ContextConfiguration(classes = {TestsConfiguration.class})
class ModelSetEntityTest {

    @Transactional
    @Test
    public void givenModelSetWithoutAlias_whenSave_thenAliasIsNull () {
        // Given
        UUID imageId = UUID.randomUUID();
        ModelDataKeyPair keyPair = new ModelDataKeyPair(imageId, "mySet");
        ModelSetEntity modelSetEntity = new ModelSetEntity(keyPair, "TestType", List.of("data1"), null);

        // When
        setRepository.save(modelSetEntity);
        ModelSetEntity foundEntity = setRepository.findById(keyPair).orElse(null);

        // Then
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getAlias()).isNull();
    }

    @Transactional
    @Test
    public void givenModelSetWithAlias_whenSave_thenAliasIsPersisted () {
        // Given
        UUID imageId = UUID.randomUUID();
        ModelDataKeyPair keyPair = new ModelDataKeyPair(imageId, "mySet");
        ModelSetEntity modelSetEntity = new ModelSetEntity(keyPair, "TestType", List.of("data1"), "testAlias");

        // When
        setRepository.save(modelSetEntity);
        ModelSetEntity foundEntity = setRepository.findById(keyPair).orElse(null);

        // Then
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getAlias()).isEqualTo("testAlias");
    }

    @Transactional
    @Test
    public void givenModelSetAlias_whenUpdateAlias_thenAliasIsUpdated () {
        // Given
        UUID imageId = UUID.randomUUID();
        ModelDataKeyPair keyPair = new ModelDataKeyPair(imageId, "mySet");
        ModelSetEntity modelSetEntity = new ModelSetEntity(keyPair, "TestType", List.of("data1"), "initialAlias");
        setRepository.save(modelSetEntity);

        // When
        modelSetEntity.setAlias("updatedAlias");
        setRepository.save(modelSetEntity);
        ModelSetEntity updatedEntity = setRepository.findById(keyPair).orElse(null);

        // Then
        assertThat(updatedEntity).isNotNull();
        assertThat(updatedEntity.getAlias()).isEqualTo("updatedAlias");
    }

    @Autowired
    private SetRepository setRepository;


    @AfterEach
    public void cleanDatabase() {
        setRepository.deleteAll();
    }

    @Transactional
    @Test
    public void givenModelSet_whenSave_thenSuccess () {
        // Given
        UUID imageId = UUID.randomUUID();
        ModelDataKeyPair keyPair = new ModelDataKeyPair(imageId, "mySet");
        ModelSetEntity modelSetEntity = new ModelSetEntity(keyPair, "INT", List.of("data1", "data2"));

        // When
        setRepository.save(modelSetEntity);
        ModelSetEntity foundEntity = setRepository.findById(keyPair).orElse(null);

        // Then
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getModelDataKey()).isEqualTo(keyPair);
        assertThat(foundEntity.getType()).isEqualTo("INT");
        assertThat(foundEntity.getData()).containsExactly("data1", "data2");
        assertThat(foundEntity.getAlias()).isNull();
    }

    @Test
    public void givenNullFieldInModelSet_whenSave_thenFailure () {
        // Given


        // When & Then
        try {
            UUID imageId = UUID.randomUUID();
            ModelDataKeyPair keyPair = new ModelDataKeyPair(imageId, "mySet");
            ModelSetEntity modelSetEntity = new ModelSetEntity(keyPair, null, List.of("data1", "data2"));
            setRepository.save(modelSetEntity);
            fail();
        }
        catch (Exception e) {
        }
    }

    @Transactional
    @Test
    public void givenModelSetWithEmptyData_whenSave_thenSuccess () {
        // Given
        UUID imageId = UUID.randomUUID();
        ModelDataKeyPair keyPair = new ModelDataKeyPair(imageId, "mySet");
        ModelSetEntity modelSetEntity = new ModelSetEntity(keyPair, "EMPTY", List.of());

        // When
        setRepository.save(modelSetEntity);
        ModelSetEntity foundEntity = setRepository.findById(keyPair).orElse(null);

        // Then
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getModelDataKey()).isEqualTo(keyPair);
        assertThat(foundEntity.getData()).isEmpty();
        assertThat(foundEntity.getAlias()).isNull();
    }

    @Transactional
    @Test
    public void givenNonExistingKey_whenFindById_thenReturnNull () {
        // Given
        ModelDataKeyPair nonExistentKey = new ModelDataKeyPair(UUID.randomUUID(), "nonexistent");

        // When
        ModelSetEntity foundEntity = setRepository.findById(nonExistentKey).orElse(null);

        // Then
        assertThat(foundEntity).isNull();
    }

    @Transactional
    @Test
    public void givenDuplicateModelSetKey_whenSaveTwice_thenOverrideEntity () {
        // Given
        UUID imageId = UUID.randomUUID();
        ModelDataKeyPair keyPair = new ModelDataKeyPair(imageId, "mySet");
        ModelSetEntity modelSetEntity1 = new ModelSetEntity(keyPair, "TYPE1", List.of("data1"));
        ModelSetEntity modelSetEntity2 = new ModelSetEntity(keyPair, "TYPE2", List.of("data2"));

        // When
        setRepository.save(modelSetEntity1);
        setRepository.save(modelSetEntity2); // Same keyPair being saved

        ModelSetEntity foundEntity = setRepository.findById(keyPair).orElse(null);

        // Then
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getType()).isEqualTo("TYPE2");
        assertThat(foundEntity.getData()).containsExactly("data2");
        assertThat(foundEntity.getAlias()).isNull();
    }

    @Transactional
    @Test
    public void givenModelSet_whenDeleteByKey_thenEntityIsDeleted () {
        // Given
        UUID imageId = UUID.randomUUID();
        ModelDataKeyPair keyPair = new ModelDataKeyPair(imageId, "mySet");
        ModelSetEntity modelSetEntity = new ModelSetEntity(keyPair, "TestType", List.of("data1", "data2"));
        setRepository.save(modelSetEntity);

        // When
        setRepository.deleteById(keyPair);
        ModelSetEntity foundEntity = setRepository.findById(keyPair).orElse(null);

        // Then
        assertThat(foundEntity).isNull();
    }

    @Transactional
    @Test
    public void givenMultipleModelSets_whenFindAll_thenReturnAllEntities () {
        // Given
        ModelDataKeyPair keyPair1 = new ModelDataKeyPair(UUID.randomUUID(), "TestName1");
        ModelSetEntity modelSetEntity1 = new ModelSetEntity(keyPair1, "TestType1", List.of("data1", "data2"));

        ModelDataKeyPair keyPair2 = new ModelDataKeyPair(UUID.randomUUID(), "TestName2");
        ModelSetEntity modelSetEntity2 = new ModelSetEntity(keyPair2, "TestType2", List.of("data3", "data4"));

        setRepository.save(modelSetEntity1);
        setRepository.save(modelSetEntity2);

        // When
        List<ModelSetEntity> allEntities = setRepository.findAll();

        // Then
        assertThat(allEntities).hasSize(2);
        assertThat(allEntities).extracting(ModelSetEntity::getType).containsExactlyInAnyOrder("TestType1", "TestType2");
    }
    
    
}