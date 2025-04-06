package Persistence.Entities.Image.Data;

import Persistence.Entities.Image.ImageComponentKey;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VariableEntityTest {

    @Test
    void equals_SameObject_ReturnsTrue () {
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");
        UUID uuid = UUID.randomUUID();
        VariableEntity variableEntity = new VariableEntity(uuid, "varName",structure, "alias");

        assertTrue(variableEntity.equals(variableEntity));
    }

    @Test
    void equals_EqualObjects_ReturnsTrue () {
        UUID uuid = UUID.randomUUID();
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");
        VariableEntity variableEntity1 = new VariableEntity(uuid, "varName",structure, "alias");
        VariableEntity variableEntity2 = new VariableEntity(uuid, "varName",structure, "alias");

        assertTrue(variableEntity1.equals(variableEntity2));
    }

    @Test
    void equals_DifferentUUID_ReturnsFalse () {
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");
        VariableEntity variableEntity1 = new VariableEntity(UUID.randomUUID(), "varName",structure, "alias");
        VariableEntity variableEntity2 = new VariableEntity(UUID.randomUUID(), "varName",structure, "alias");

        assertFalse(variableEntity1.equals(variableEntity2));
    }

    @Test
    void equals_DifferentAlias_ReturnsFalse () {
        UUID uuid = UUID.randomUUID();
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");
        VariableEntity variableEntity1 = new VariableEntity(uuid, "varName",structure, "alias1");
        VariableEntity variableEntity2 = new VariableEntity(uuid, "varName",structure, "alias2");

        assertFalse(variableEntity1.equals(variableEntity2));
    }

    @Test
    void equals_NullObject_ReturnsFalse () {
        UUID uuid = UUID.randomUUID();
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");
        VariableEntity variableEntity = new VariableEntity(uuid, "varName",structure, "alias");

        assertFalse(variableEntity.equals(null));
    }

    @Test
    void equals_DifferentClass_ReturnsFalse () {
        UUID uuid = UUID.randomUUID();
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");
        VariableEntity variableEntity = new VariableEntity(uuid, "varName",structure, "alias");
        Object other = new Object();

        assertFalse(variableEntity.equals(other));
    }
}