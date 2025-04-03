package Persistence.Entities.Image.Data;

import static org.junit.jupiter.api.Assertions.*;

import Persistence.Entities.Image.ImageComponentKey;
import org.junit.jupiter.api.Test;
import java.util.UUID;

class ImageComponentKeyTest {

    @Test
    void givenSameParentAndName_whenEquals_thenReturnsTrue() {
        UUID parentId = UUID.randomUUID();
        ImageComponentKey key1 = new ImageComponentKey(parentId, "uniqueName");
        ImageComponentKey key2 = new ImageComponentKey(parentId, "uniqueName");

        assertEquals(key1, key2);
    }

    @Test
    void givenSameNameDifferentParent_whenEquals_thenReturnsFalse() {
        ImageComponentKey key1 = new ImageComponentKey(UUID.randomUUID(), "uniqueName");
        ImageComponentKey key2 = new ImageComponentKey(UUID.randomUUID(), "uniqueName");

        assertNotEquals(key1, key2);
    }

    @Test
    void givenSameParentDifferentName_whenEquals_thenReturnsFalse() {
        UUID parentId = UUID.randomUUID();
        ImageComponentKey key1 = new ImageComponentKey(parentId, "uniqueName1");
        ImageComponentKey key2 = new ImageComponentKey(parentId, "uniqueName2");
        assertNotEquals(key1, key2);
    }

    @Test
    void givenNull_whenEquals_thenReturnsFalse() {
        ImageComponentKey key = new ImageComponentKey(UUID.randomUUID(), "uniqueName");

        assertNotEquals(null, key);
    }

    @Test
    void givenDifferentClass_whenEquals_thenReturnsFalse() {
        ImageComponentKey key = new ImageComponentKey(UUID.randomUUID(), "uniqueName");

        assertNotEquals("SomeString", key);
    }
}