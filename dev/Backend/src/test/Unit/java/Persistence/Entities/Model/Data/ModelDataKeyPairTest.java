package Unit.java.Persistence.Entities.Model.Data;

import static org.junit.jupiter.api.Assertions.*;

import Persistence.Entities.Model.Data.ModelDataKeyPair;
import org.junit.jupiter.api.Test;
import java.util.UUID;

class ModelDataKeyPairTest {

    @Test
    void givenSameParentAndName_whenEquals_thenReturnsTrue() {
        UUID parentId = UUID.randomUUID();
        ModelDataKeyPair key1 = new ModelDataKeyPair(parentId, "uniqueName");
        ModelDataKeyPair key2 = new ModelDataKeyPair(parentId, "uniqueName");

        assertTrue(key1.equals(key2));
    }

    @Test
    void givenSameNameDifferentParent_whenEquals_thenReturnsFalse() {
        ModelDataKeyPair key1 = new ModelDataKeyPair(UUID.randomUUID(), "uniqueName");
        ModelDataKeyPair key2 = new ModelDataKeyPair(UUID.randomUUID(), "uniqueName");

        assertFalse(key1.equals(key2));
    }

    @Test
    void givenSameParentDifferentName_whenEquals_thenReturnsFalse() {
        UUID parentId = UUID.randomUUID();
        ModelDataKeyPair key1 = new ModelDataKeyPair(parentId, "uniqueName1");
        ModelDataKeyPair key2 = new ModelDataKeyPair(parentId, "uniqueName2");

        assertFalse(key1.equals(key2));
    }

    @Test
    void givenNull_whenEquals_thenReturnsFalse() {
        ModelDataKeyPair key = new ModelDataKeyPair(UUID.randomUUID(), "uniqueName");

        assertFalse(key.equals(null));
    }

    @Test
    void givenDifferentClass_whenEquals_thenReturnsFalse() {
        ModelDataKeyPair key = new ModelDataKeyPair(UUID.randomUUID(), "uniqueName");

        assertFalse(key.equals("SomeString"));
    }
}