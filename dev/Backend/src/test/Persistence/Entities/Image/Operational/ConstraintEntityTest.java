package Persistence.Entities.Image.Operational;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ConstraintEntityTest {

    @Test
    void testEquals_SameInstance () {
        ConstraintEntity entity = new ConstraintEntity();
        assertEquals(entity, entity, "An instance should be equal to itself.");
    }

    @Test
    void testEquals_NullComparison () {
        ConstraintEntity entity = new ConstraintEntity();
        assertNotEquals(null, entity, "An instance should not be equal to null.");
    }

    @Test
    void testEquals_DifferentClass () {
        ConstraintEntity entity = new ConstraintEntity();
        Object someOtherObject = new Object();
        assertNotEquals(entity, someOtherObject, "An instance should not be equal to an object of a different class.");
    }

    @Test
    void testEquals_EqualObjects () {
        ConstraintEntity entity1 = new ConstraintEntity();
        ConstraintEntity entity2 = new ConstraintEntity();
        entity1.setName("ConstraintName");
        entity2.setName("ConstraintName");

        assertEquals(entity1, entity2, "Two instances with the same name should be equal.");
    }

    @Test
    void testEquals_DifferentObjects () {
        ConstraintEntity entity1 = new ConstraintEntity();
        ConstraintEntity entity2 = new ConstraintEntity();
        entity1.setName("ConstraintName1");
        entity2.setName("ConstraintName2");

        assertNotEquals(entity1, entity2, "Two instances with different names should not be equal.");
    }

    @Test
    void testEquals_OneNullName () {
        ConstraintEntity entity1 = new ConstraintEntity();
        ConstraintEntity entity2 = new ConstraintEntity();
        entity1.setName("ConstraintName");
        entity2.setName(null);

        assertNotEquals(entity1, entity2, "An instance with a name should not be equal to one with a null name.");
    }

    @Test
    void testEquals_BothNullNames () {
        ConstraintEntity entity1 = new ConstraintEntity();
        ConstraintEntity entity2 = new ConstraintEntity();
        entity1.setName(null);
        entity2.setName(null);
        assertEquals(entity1, entity2, "Two instances with null names should be equal.");
    }
}