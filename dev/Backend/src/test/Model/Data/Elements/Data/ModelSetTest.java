/*
package Model.Data.Elements.Data;

import Model.Data.Elements.Element;
import Model.Data.Types.ModelPrimitives;
import Model.Data.Types.ModelType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ModelSetTest {

    @Test
    void testGetData_ReturnsData_WhenPrimitive() {
        // Arrange
        String name = "TestModel";
        ModelType type = mock(ModelType.class); // Mocking ModelType to avoid unnecessary dependency
        List<String> data = List.of("Element1", "Element2", "Element3");
        ModelSet modelSet = new ModelSet(name, type, data);

        // Act
        List<String> result = modelSet.getData();

        // Assert
        assertNotNull(result);
        assertEquals(data, result);
    }

    @Test
    void testGetData_ThrowsException_WhenNotPrimitive() {
        // Arrange
        String name = "TestModel";
        ModelType type = mock(ModelType.class); // Mocking ModelType to avoid unnecessary dependency
        ModelSet modelSet = new ModelSet(name, type, false);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, modelSet::getData);
        assertEquals("Cannot get data from complex set", exception.getMessage());
    }

    @Test
    void testGetData_ReturnsNull_WhenPrimitiveAndDataNull() {
        // Arrange
        String name = "TestModel";
        ModelType type = mock(ModelType.class); // Mocking ModelType to avoid unnecessary dependency
        ModelSet modelSet = new ModelSet(name, type, null);

        // Act
        List<String> result = modelSet.getData();

        // Assert
        assertNull(result);
    }

    @Test
    void testGetType_ReturnsModelParameter() {
        // Arrange
        String name = "TestModel";
        ModelType type = mock(ModelType.class);
        ModelSet modelSet = new ModelSet(name, type, false);

        assertEquals(Element.ElementType.MODEL_SET, modelSet.getType());
    }
}*/
