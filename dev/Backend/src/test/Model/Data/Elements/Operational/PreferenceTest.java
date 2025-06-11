package Model.Data.Elements.Operational;

import Model.Data.Elements.Element;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PreferenceTest {

    @Test
    void testEquals_SameObject() {
        Preference preference = new Preference("Body1");
        assertEquals(preference, preference, "The 'equals' method should return true when comparing the same object.");
    }

    @Test
    void testEquals_NullObject() {
        Preference preference = new Preference("Body1");
        assertNotEquals(null, preference, "The 'equals' method should return false when comparing with a null object.");
    }

    @Test
    void testEquals_DifferentClass() {
        Preference preference = new Preference("Body1");
        Object differentObject = new Object();
        assertNotEquals(preference, differentObject, "The 'equals' method should return false when comparing with an object of a different class.");
    }

    @Test
    void testEquals_EqualPreferences() {
        Preference preference1 = new Preference("Body1");
        Preference preference2 = new Preference("Body1");
        assertEquals(preference1, preference2, "The 'equals' method should return true for two Preference objects with the same body.");
    }

    @Test
    void testEquals_DifferentPreferences() {
        Preference preference1 = new Preference("Body1");
        Preference preference2 = new Preference("Body2");
        assertNotEquals(preference1, preference2, "The 'equals' method should return false for two Preference objects with different bodies.");
    }
    @Test
    void testType() {
        // Arrange
        Preference preference = new Preference("Preference");

        // Act & Assert
        assertEquals(Element.ElementType.PREFERENCE, preference.getType());
    }
}