package Model.Data.Elements.Operational;

import Model.Data.Elements.Element;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ConstraintTest {

    @Test
    void testEquals_SameObject() {
        // Arrange
        Constraint constraint = new Constraint("Constraint1");

        // Act & Assert
        assertEquals(constraint, constraint);
    }

    @Test
    void testEquals_NullObject() {
        // Arrange
        Constraint constraint = new Constraint("Constraint1");

        // Act & Assert
        assertNotEquals(null, constraint);
    }

    @Test
    void testEquals_DifferentClass() {
        // Arrange
        Constraint constraint = new Constraint("Constraint1");
        Object other = new Object();

        // Act & Assert
        assertNotEquals(constraint, other);
    }

    @Test
    void testEquals_EqualObjects() {
        // Arrange
        Constraint constraint1 = new Constraint("Constraint1");
        Constraint constraint2 = new Constraint("Constraint1");

        // Act & Assert
        assertEquals(constraint1, constraint2);
    }

    @Test
    void testEquals_DifferentConstraints() {
        // Arrange
        Constraint constraint1 = new Constraint("Constraint1");
        Constraint constraint2 = new Constraint("Constraint2");

        // Act & Assert
        assertNotEquals(constraint1, constraint2);
    }
    @Test
    void testType() {
        // Arrange
        Constraint constraint = new Constraint("Constraint1");

        // Act & Assert
        assertEquals(Element.ElementType.CONSTRAINT, constraint.getType());
    }
}