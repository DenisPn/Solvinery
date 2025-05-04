package Persistence.Entities.Image.Data;

import Persistence.Entities.Image.ImageComponentKey;
import Persistence.Entities.Image.Repositories.ParamRepository;
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

@SpringBootTest
@AutoConfigureDataJpa
@ContextConfiguration(classes = {TestsConfiguration.class})
public class ParameterEntityTest {

    @Autowired
    private ParamRepository paramRepository;
    @AfterEach
    public void cleanDatabase() {
        paramRepository.deleteAll();
    }
    @Transactional
    @Test
    public void givenModelParamWithNonNullAlias_whenSave_thenSuccess () {
        // Given
        UUID imageId = UUID.randomUUID();
        ImageComponentKey keyPair = new ImageComponentKey(imageId, "myParam");
        ParameterEntity paramEntity = new ParameterEntity(keyPair, "STRING", "data");
        paramEntity.setAlias("customAlias");

        // When
        paramRepository.save(paramEntity);
        ParameterEntity foundEntity = paramRepository.findById(keyPair).orElse(null);

        // Then
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getAlias()).isEqualTo("customAlias");
    }

    @Transactional
    @Test
    public void givenModelParamWithNullAlias_whenSave_thenSuccess () {
        // Given
        UUID imageId = UUID.randomUUID();
        ImageComponentKey keyPair = new ImageComponentKey(imageId, "myParam");
        ParameterEntity paramEntity = new ParameterEntity(keyPair, "STRING", "data");
        paramEntity.setAlias(null);

        // When
        paramRepository.save(paramEntity);
        ParameterEntity foundEntity = paramRepository.findById(keyPair).orElse(null);

        // Then
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getAlias()).isNull();
    }



    @Transactional
    @Test
    public void givenModelSet_whenSave_thenSuccess () {
        // Given
        UUID imageId = UUID.randomUUID();
        ImageComponentKey keyPair = new ImageComponentKey(imageId, "myParam");
        ParameterEntity paramEntity = new ParameterEntity(keyPair, "INT", "data");

        // When
        paramRepository.save(paramEntity);
        ParameterEntity foundEntity = paramRepository.findById(keyPair).orElse(null);

        // Then
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getModelDataKey()).isEqualTo(keyPair);
        assertThat(foundEntity.getType()).isEqualTo("INT");
        assertThat(foundEntity.getData()).isEqualTo("data");
        assertThat(foundEntity.getAlias()).isNull();
    }

    @Test
    public void givenNullFieldInModelParam_whenSave_thenFailure () {
        // Given


        // When & Then
        try {
            UUID imageId = UUID.randomUUID();
            ImageComponentKey keyPair = new ImageComponentKey(imageId, "myParam");
            ParameterEntity paramEntity = new ParameterEntity(keyPair, null, "data");
            paramRepository.save(paramEntity);
            fail();
        } catch (Exception e) {
        }
    }

    @Transactional
    @Test
    public void givenModelParamWithEmptyData_whenSave_thenSuccess () {
        // Given
        UUID imageId = UUID.randomUUID();
        ImageComponentKey keyPair = new ImageComponentKey(imageId, "myParam");
        ParameterEntity paramEntity = new ParameterEntity(keyPair, "EMPTY", "");

        // When
        paramRepository.save(paramEntity);
        ParameterEntity foundEntity = paramRepository.findById(keyPair).orElse(null);

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
        ParameterEntity foundEntity = paramRepository.findById(nonExistentKey).orElse(null);

        // Then
        assertThat(foundEntity).isNull();
    }

    @Transactional
    @Test
    public void givenDuplicateModelParamKey_whenSaveTwice_thenOverrideEntity () {
        // Given
        UUID imageId = UUID.randomUUID();
        ImageComponentKey keyPair = new ImageComponentKey(imageId, "myParam");
        ParameterEntity paramEntity1 = new ParameterEntity(keyPair, "TYPE1", "data1");
        ParameterEntity paramEntity2 = new ParameterEntity(keyPair, "TYPE2", "data2");

        // When
        paramRepository.save(paramEntity1);
        paramRepository.save(paramEntity2); // Same keyPair being saved

        ParameterEntity foundEntity = paramRepository.findById(keyPair).orElse(null);

        // Then
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getType()).isEqualTo("TYPE2");
        assertThat(foundEntity.getData()).isEqualTo("data2");
        assertThat(foundEntity.getAlias()).isNull();
    }

    @Transactional
    @Test
    public void givenModelParam_whenDeleteByKey_thenEntityIsDeleted () {
        // Given
        UUID imageId = UUID.randomUUID();
        ImageComponentKey keyPair = new ImageComponentKey(imageId, "myParam");
        ParameterEntity paramEntity = new ParameterEntity(keyPair, "TestType", "data");
        paramRepository.save(paramEntity);

        // When
        paramRepository.deleteById(keyPair);
        ParameterEntity foundEntity = paramRepository.findById(keyPair).orElse(null);

        // Then
        assertThat(foundEntity).isNull();
    }

    @Transactional
    @Test
    public void givenMultipleModelParams_whenFindAll_thenReturnAllEntities () {
        // Given
        ImageComponentKey keyPair1 = new ImageComponentKey(UUID.randomUUID(), "TestName1");
        ParameterEntity paramEntity1 = new ParameterEntity(keyPair1, "TestType1", "data1");

        ImageComponentKey keyPair2 = new ImageComponentKey(UUID.randomUUID(), "TestName2");
        ParameterEntity paramEntity2 = new ParameterEntity(keyPair2, "TestType2", "data2");

        paramRepository.save(paramEntity1);
        paramRepository.save(paramEntity2);

        // When
        List<ParameterEntity> allEntities = paramRepository.findAll();

        // Then
        assertThat(allEntities).hasSize(2);
        assertThat(allEntities).extracting(ParameterEntity::getType).containsExactlyInAnyOrder("TestType1", "TestType2");
    }
}
