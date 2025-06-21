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

        VariableEntity variableEntity1 = new VariableEntity(uuid, "varName", "alias",structure,"objectiveValueAlias");
        VariableEntity variableEntity2 = new VariableEntity(uuid, "varName", "alias",structure,"objectiveValueAlias");

        assertEquals(variableEntity1, variableEntity2);
    }

    @Test
    void equals_DifferentUUID_ReturnsFalse () {
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");

        VariableEntity variableEntity1 = new VariableEntity(UUID.randomUUID(), "varName", "alias",structure,"objectiveValueAlias");
        VariableEntity variableEntity2 = new VariableEntity(UUID.randomUUID(), "varName", "alias",structure,"objectiveValueAlias");

        assertNotEquals(variableEntity1, variableEntity2);
    }

    @Test
    void equals_DifferentAlias_ReturnsFalse () {
        UUID uuid = UUID.randomUUID();
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");

        VariableEntity variableEntity1 = new VariableEntity(uuid, "varName", "alias1",structure,"objectiveValueAlias");
        VariableEntity variableEntity2 = new VariableEntity(uuid, "varName", "alias2",structure,"objectiveValueAlias");

        assertNotEquals(variableEntity1, variableEntity2);
    }

    @Test
    void equals_DifferentClass_ReturnsFalse () {
        UUID uuid = UUID.randomUUID();
        List<String> structure= new ArrayList<>();
        structure.add("structure1"); structure.add("structure2");
        VariableEntity variableEntity = new VariableEntity(uuid, "varName", "alias",structure,"objectiveValueAlias");
        Object other = new Object();

        assertNotEquals(variableEntity, other);
    }
}