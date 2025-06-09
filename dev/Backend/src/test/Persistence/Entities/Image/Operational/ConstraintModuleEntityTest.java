package Persistence.Entities.Image.Operational;

import Persistence.Entities.Image.ImageComponentKey;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConstraintModuleEntityTest {


    @Test
    void testEquals_DifferentObjectsWithSameData_ReturnsTrue () {
        // Arrange
        ImageComponentKey imageComponentKey = new ImageComponentKey(UUID.randomUUID(), "TestImage");
        ConstraintModuleEntity moduleEntity1 = new ConstraintModuleEntity(imageComponentKey, "Test description");
        ConstraintModuleEntity moduleEntity2 = new ConstraintModuleEntity(imageComponentKey, "Test description");

        // Act & Assert
        assertEquals(moduleEntity1, moduleEntity2, "Objects with the same data should be equal.");
    }

    @Test
    void testEquals_DifferentObjectsWithDifferentData_ReturnsFalse () {
        // Arrange
        ImageComponentKey imageComponentKey1 = new ImageComponentKey(UUID.randomUUID(), "TestImage1");
        ImageComponentKey imageComponentKey2 = new ImageComponentKey(UUID.randomUUID(), "TestImage2");
        ConstraintModuleEntity moduleEntity1 = new ConstraintModuleEntity(imageComponentKey1, "Test description 1");
        ConstraintModuleEntity moduleEntity2 = new ConstraintModuleEntity(imageComponentKey2, "Test description 2");

        // Act & Assert
        assertNotEquals(moduleEntity1, moduleEntity2, "Objects with different data should not be equal.");
    }

    @Test
    void testAddConstraint_AddsConstraintSuccessfully () {
        // Arrange
        ImageComponentKey imageComponentKey = new ImageComponentKey( UUID.randomUUID(),"TestImage");
        ConstraintModuleEntity moduleEntity = new ConstraintModuleEntity(imageComponentKey, "Test description");
        ConstraintEntity constraint = new ConstraintEntity("TestConstraint");

        // Act
        moduleEntity.addConstraint(constraint);

        // Assert
        assertTrue(moduleEntity.getConstraints().contains(constraint),
                "The constraint should be added to the ConstraintModuleEntity.");
    }

    @Test
    void testAddConstraint_AddingDuplicateConstraint () {
        // Arrange
        ImageComponentKey imageComponentKey = new ImageComponentKey(UUID.randomUUID(),"TestImage");
        ConstraintModuleEntity moduleEntity = new ConstraintModuleEntity(imageComponentKey, "Test description");
        ConstraintEntity constraint = new ConstraintEntity("DuplicateConstraint");

        // Act
        moduleEntity.addConstraint(constraint);
        moduleEntity.addConstraint(constraint);

        // Assert
        assertEquals(1, moduleEntity.getConstraints().size(),
                "Duplicate constraints should not be added. The set size should remain 1.");
    }

    @Test
    void testEquals_AddConstraint_ChangesEquality () {
        // Arrange
        ImageComponentKey imageComponentKey = new ImageComponentKey(UUID.randomUUID(), "TestImage");
        ConstraintModuleEntity moduleEntity1 = new ConstraintModuleEntity(imageComponentKey, "Test description");
        ConstraintModuleEntity moduleEntity2 = new ConstraintModuleEntity(imageComponentKey, "Test description");
        ConstraintEntity constraint = new ConstraintEntity("NewConstraint");

        // Act
        moduleEntity1.addConstraint(constraint);

        // Assert
        assertNotEquals(moduleEntity1, moduleEntity2,
                "Adding a constraint to one module should make it unequal to the other module.");
    }
}