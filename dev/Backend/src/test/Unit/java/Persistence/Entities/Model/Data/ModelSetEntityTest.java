package Unit.java.Persistence.Entities.Model.Data;

import Persistence.Entities.Model.Data.ModelDataKeyPair;
import Persistence.Entities.Model.Data.ModelSetEntity;
import Persistence.Repositories.Model.Data.SetRepository;
import Utilities.TestsConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = {TestsConfiguration.class})
class ModelSetEntityTest {

    @Autowired
    private SetRepository setRepository;

    @Test
    public void givenModelSet_whenSave_thenSuccess() {
        // Given
        UUID imageId = UUID.randomUUID();
        ModelDataKeyPair keyPair = new ModelDataKeyPair(imageId, "TestName");
        ModelSetEntity modelSetEntity = new ModelSetEntity(keyPair, "TestType", List.of("data1", "data2"));

        // When
        setRepository.save(modelSetEntity);
        ModelSetEntity foundEntity = setRepository.findById(keyPair).orElse(null);

        // Then
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getModelDataKey()).isEqualTo(keyPair);
        assertThat(foundEntity.getType()).isEqualTo("TestType");
        assertThat(foundEntity.getData()).containsExactly("data1", "data2");
    }
}