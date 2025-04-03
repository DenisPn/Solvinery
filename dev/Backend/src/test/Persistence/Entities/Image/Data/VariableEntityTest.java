package Persistence.Entities.Image.Data;

import Persistence.Entities.Image.ImageComponentKey;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VariableEntityTest {

    @Test
    void equals_SameObject_ReturnsTrue () {
        UUID uuid = UUID.randomUUID();
        VariableEntity variableEntity = new VariableEntity(uuid, "varName", "alias");

        assertTrue(variableEntity.equals(variableEntity));
    }

    @Test
    void equals_EqualObjects_ReturnsTrue () {
        UUID uuid = UUID.randomUUID();
        VariableEntity variableEntity1 = new VariableEntity(uuid, "varName", "alias");
        VariableEntity variableEntity2 = new VariableEntity(uuid, "varName", "alias");

        assertTrue(variableEntity1.equals(variableEntity2));
    }

    @Test
    void equals_DifferentUUID_ReturnsFalse () {
        VariableEntity variableEntity1 = new VariableEntity(UUID.randomUUID(), "varName", "alias");
        VariableEntity variableEntity2 = new VariableEntity(UUID.randomUUID(), "varName", "alias");

        assertFalse(variableEntity1.equals(variableEntity2));
    }

    @Test
    void equals_DifferentAlias_ReturnsFalse () {
        UUID uuid = UUID.randomUUID();
        VariableEntity variableEntity1 = new VariableEntity(uuid, "varName", "alias1");
        VariableEntity variableEntity2 = new VariableEntity(uuid, "varName", "alias2");

        assertFalse(variableEntity1.equals(variableEntity2));
    }

    @Test
    void equals_NullObject_ReturnsFalse () {
        UUID uuid = UUID.randomUUID();
        VariableEntity variableEntity = new VariableEntity(uuid, "varName", "alias");

        assertFalse(variableEntity.equals(null));
    }

    @Test
    void equals_DifferentClass_ReturnsFalse () {
        UUID uuid = UUID.randomUUID();
        VariableEntity variableEntity = new VariableEntity(uuid, "varName", "alias");
        Object other = new Object();

        assertFalse(variableEntity.equals(other));
    }
}