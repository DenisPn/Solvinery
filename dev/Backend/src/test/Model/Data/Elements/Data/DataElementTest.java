package Model.Data.Elements.Data;

import Model.Data.Types.ModelType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.lang.NonNull;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataElementTest {

    @Test
    public void testIsCompatibleWithDataElement_CompatibleElements() {
        // Arrange
        ModelType type1 = Mockito.mock(ModelType.class);
        ModelType type2 = Mockito.mock(ModelType.class);
        DataElement element1 = new TestableDataElement("Element1", type1);
        DataElement element2 = new TestableDataElement("Element2", type2);

        Mockito.when(type1.isCompatible(type2)).thenReturn(true);

        // Act
        boolean result = element1.isCompatible(element2);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsCompatibleWithDataElement_IncompatibleElements() {
        // Arrange
        ModelType type1 = Mockito.mock(ModelType.class);
        ModelType type2 = Mockito.mock(ModelType.class);
        DataElement element1 = new TestableDataElement("Element1", type1);
        DataElement element2 = new TestableDataElement("Element2", type2);

        Mockito.when(type1.isCompatible(type2)).thenReturn(false);

        // Act
        boolean result = element1.isCompatible(element2);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testIsCompatibleWithString_CompatibleString() {
        // Arrange
        ModelType type = Mockito.mock(ModelType.class);
        DataElement element = new TestableDataElement("Element", type);
        String compatibleString = "CompatibleString";

        Mockito.when(type.isCompatible(compatibleString)).thenReturn(true);

        // Act
        boolean result = element.isCompatible(compatibleString);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsCompatibleWithString_IncompatibleString() {
        // Arrange
        ModelType type = Mockito.mock(ModelType.class);
        DataElement element = new TestableDataElement("Element", type);
        String incompatibleString = "IncompatibleString";

        Mockito.when(type.isCompatible(incompatibleString)).thenReturn(false);

        // Act
        boolean result = element.isCompatible(incompatibleString);

        // Assert
        assertFalse(result);
    }

    private static class TestableDataElement extends DataElement {

        public TestableDataElement(String name, ModelType type) {
            super(name, type);
        }

        @NonNull
        @Override
        public ElementType getType() {
            return ElementType.MODEL_PARAMETER;
        }
    }
}