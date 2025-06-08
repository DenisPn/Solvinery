package Persistence.Entities.Image.Operational;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PreferenceEntityTest {

    /**
     * Unit tests for the equals method in the PreferenceEntity class.
     * The equals method checks if two PreferenceEntity objects are equal by comparing their names.
     */


    @Test
    public void testEquals_EqualObjects () {
        // Arrange
        PreferenceEntity preferenceEntity1 = new PreferenceEntity("TestName");
        PreferenceEntity preferenceEntity2 = new PreferenceEntity("TestName");

        // Act & Assert
        assertEquals(preferenceEntity1, preferenceEntity2, "The equals method should return true for objects with the same name.");
    }

    @Test
    public void testEquals_DifferentNames () {
        // Arrange
        PreferenceEntity preferenceEntity1 = new PreferenceEntity("NameOne");
        PreferenceEntity preferenceEntity2 = new PreferenceEntity("NameTwo");

        // Act & Assert
        assertNotEquals(preferenceEntity1, preferenceEntity2, "The equals method should return false for objects with different names.");
    }

    @Test
    public void testEquals_NullObject () {
        // Arrange
        PreferenceEntity preferenceEntity = new PreferenceEntity("TestName");

        // Act & Assert
        assertNotEquals(preferenceEntity, null, "The equals method should return false when comparing to null.");
    }

    @Test
    public void testEquals_DifferentClass () {
        // Arrange
        PreferenceEntity preferenceEntity = new PreferenceEntity("TestName");
        String differentClassObject = "TestName";

        // Act & Assert
        assertNotEquals(preferenceEntity, differentClassObject, "The equals method should return false when comparing to an object of a different class.");
    }

    @Test
    public void testEquals_BothNamesNull () {
        // Arrange
        PreferenceEntity preferenceEntity1 = new PreferenceEntity(null);
        PreferenceEntity preferenceEntity2 = new PreferenceEntity(null);

        // Act & Assert
        assertEquals(preferenceEntity1, preferenceEntity2, "The equals method should return true for two objects with null names.");
    }

    @Test
    public void testEquals_OneNameNull () {
        // Arrange
        PreferenceEntity preferenceEntity1 = new PreferenceEntity(null);
        PreferenceEntity preferenceEntity2 = new PreferenceEntity("TestName");

        // Act & Assert
        assertNotEquals(preferenceEntity1, preferenceEntity2, "The equals method should return false when one object has a null name and the other does not.");
    }
}