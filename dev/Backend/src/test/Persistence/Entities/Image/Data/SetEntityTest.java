package Persistence.Entities.Image.Data;

import Persistence.Entities.Image.ImageComponentKey;
import Persistence.Entities.Image.Repositories.SetRepository;
import Utilities.TestsConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
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
class SetEntityTest {

    @Transactional
    @Test
    public void givenModelSetWithoutAlias_whenSave_thenAliasIsNull () {
        // Given
        UUID imageId = UUID.randomUUID();
        SetEntity setEntity = new SetEntity(imageId, "mySet", "TestType", List.of("data1"), null);

        // When
        ImageComponentKey keyPair = new ImageComponentKey(imageId, "mySet");
        setRepository.save(setEntity);
        SetEntity foundEntity = setRepository.findById(keyPair).orElse(null);

        // Then
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getAlias()).isNull();
    }

    @Transactional
    @Test
    public void givenModelSetWithAlias_whenSave_thenAliasIsPersisted () {
        // Given
        UUID imageId = UUID.randomUUID();
        SetEntity setEntity = new SetEntity(imageId, "mySet", "TestType", List.of("data1"), "testAlias");

        // When
        ImageComponentKey keyPair = new ImageComponentKey(imageId, "mySet");
        setRepository.save(setEntity);
        SetEntity foundEntity = setRepository.findById(keyPair).orElse(null);

        // Then
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getAlias()).isEqualTo("testAlias");
    }

    @Transactional
    @Test
    public void givenModelSetAlias_whenUpdateAlias_thenAliasIsUpdated () {
        // Given
        UUID imageId = UUID.randomUUID();
        ImageComponentKey keyPair = new ImageComponentKey(imageId, "mySet");
        SetEntity setEntity = new SetEntity(keyPair, "TestType", List.of("data1"), "initialAlias");
        setRepository.save(setEntity);

        // When
        setEntity.setAlias("updatedAlias");
        setRepository.save(setEntity);
        SetEntity updatedEntity = setRepository.findById(new ImageComponentKey(imageId, "mySet")).orElse(null);

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
        SetEntity setEntity = new SetEntity(imageId, "mySet", "INT", List.of("data1", "data2"));

        // When
        setRepository.save(setEntity);
        SetEntity foundEntity = setRepository.findById(new ImageComponentKey(imageId, "mySet")).orElse(null);
        ImageComponentKey keyPair = new ImageComponentKey(imageId, "mySet");
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
            ImageComponentKey keyPair = new ImageComponentKey(imageId, "mySet");
            SetEntity setEntity = new SetEntity(keyPair, null, List.of("data1", "data2"));
            setRepository.save(setEntity);
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
        SetEntity setEntity = new SetEntity(imageId, "mySet", "EMPTY", List.of());

        // When
        ImageComponentKey keyPair = new ImageComponentKey(imageId, "mySet");
        setRepository.save(setEntity);
        SetEntity foundEntity = setRepository.findById(keyPair).orElse(null);

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
        ImageComponentKey nonExistentKey = new ImageComponentKey(UUID.randomUUID(), "nonexistent");

        // When
        SetEntity foundEntity = setRepository.findById(nonExistentKey).orElse(null);

        // Then
        assertThat(foundEntity).isNull();
    }

    @Transactional
    @Test
    public void givenDuplicateModelSetKey_whenSaveTwice_thenOverrideEntity () {
        // Given
        UUID imageId = UUID.randomUUID();
        SetEntity setEntity1 = new SetEntity(imageId, "mySet", "TYPE1", List.of("data1"));
        SetEntity setEntity2 = new SetEntity(imageId, "mySet", "TYPE2", List.of("data2"));

        // When
        ImageComponentKey keyPair = new ImageComponentKey(imageId, "mySet");
        setRepository.save(setEntity1);
        setRepository.save(setEntity2); // Same keyPair being saved

        SetEntity foundEntity = setRepository.findById(keyPair).orElse(null);

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
        ImageComponentKey keyPair = new ImageComponentKey(imageId, "mySet");
        SetEntity setEntity = new SetEntity(keyPair, "TestType", List.of("data1", "data2"));
        setRepository.save(setEntity);

        // When
        setRepository.deleteById(new ImageComponentKey(imageId, "mySet"));
        SetEntity foundEntity = setRepository.findById(new ImageComponentKey(imageId, "mySet")).orElse(null);

        // Then
        assertThat(foundEntity).isNull();
    }

    @Transactional
    @Test
    public void givenMultipleModelSets_whenFindAll_thenReturnAllEntities () {
        // Given
        UUID imageId1 = UUID.randomUUID();
        SetEntity setEntity1 = new SetEntity(imageId1, "TestName1", "TestType1", List.of("data1", "data2"));

        UUID imageId2 = UUID.randomUUID();
        SetEntity setEntity2 = new SetEntity(imageId2, "TestName2", "TestType2", List.of("data3", "data4"));

        setRepository.save(setEntity1);
        setRepository.save(setEntity2);

        // When
        List<SetEntity> allEntities = setRepository.findAll();

        // Then
        assertThat(allEntities).hasSize(2);
        assertThat(allEntities).extracting(SetEntity::getType).containsExactlyInAnyOrder("TestType1", "TestType2");
    }
    
    
}