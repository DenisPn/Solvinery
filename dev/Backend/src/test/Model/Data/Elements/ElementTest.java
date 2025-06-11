package Model.Data.Elements;

import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import static org.junit.jupiter.api.Assertions.*;

//using AI to generate tests with simple cases
class ElementTest {

    @Test
    void testEquals_sameObject() {
        Element element = new Element("test") {
            @NonNull
            @Override
            public ElementType getType() {
                return ElementType.MODEL_SET;
            }
        };

        assertEquals(element, element);
    }

    @Test
    void testEquals_differentObjects() {
        Element element1 = new Element("test1") {
            @NonNull
            @Override
            public ElementType getType() {
                return ElementType.MODEL_SET;
            }
        };

        Element element2 = new Element("test2") {
            @NonNull
            @Override
            public ElementType getType() {
                return ElementType.MODEL_PARAMETER;
            }
        };

        assertNotEquals(element1, element2);
    }

    @Test
    void testEquals_nullObject() {
        Element element = new Element("test") {
            @NonNull
            @Override
            public ElementType getType() {
                return ElementType.MODEL_SET;
            }
        };

        assertNotEquals(null, element);
    }

    @Test
    void testEquals_differentClass() {
        Element element = new Element("test") {
            @NonNull
            @Override
            public ElementType getType() {
                return ElementType.MODEL_SET;
            }
        };

        Object other = new Object();

        assertNotEquals(element, other);
    }
}