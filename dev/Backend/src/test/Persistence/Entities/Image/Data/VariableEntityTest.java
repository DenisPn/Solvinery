package Persistence.Entities.Image.Data;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VariableEntityTest {

    @Test
    void equals_EqualObjects_ReturnsTrue () {
        UUID uuid = UUID.randomUUID();
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");

        VariableEntity variableEntity1 = new VariableEntity(uuid, "varName",structure, "alias");
        VariableEntity variableEntity2 = new VariableEntity(uuid, "varName",structure, "alias");

        assertEquals(variableEntity1, variableEntity2);
    }

    @Test
    void equals_DifferentUUID_ReturnsFalse () {
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");

        VariableEntity variableEntity1 = new VariableEntity(UUID.randomUUID(), "varName",structure, "alias");
        VariableEntity variableEntity2 = new VariableEntity(UUID.randomUUID(), "varName",structure, "alias");

        assertNotEquals(variableEntity1, variableEntity2);
    }

    @Test
    void equals_DifferentAlias_ReturnsFalse () {
        UUID uuid = UUID.randomUUID();
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");

        VariableEntity variableEntity1 = new VariableEntity(uuid, "varName",structure, "alias1");
        VariableEntity variableEntity2 = new VariableEntity(uuid, "varName",structure, "alias2");

        assertNotEquals(variableEntity1, variableEntity2);
    }

    @Test
    void equals_DifferentClass_ReturnsFalse () {
        UUID uuid = UUID.randomUUID();
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");
        VariableEntity variableEntity = new VariableEntity(uuid, "varName",structure, "alias");
        Object other = new Object();

        assertNotEquals(variableEntity, other);
    }
}