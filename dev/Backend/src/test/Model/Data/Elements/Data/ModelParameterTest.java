package Model.Data.Elements.Data;

import Model.Data.Elements.Element;
import Model.Data.Types.ModelPrimitives;
import Model.Data.Types.ModelType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelParameterTest {

    @Test
    void testEquals_SameObject() {
        ModelParameter param = new ModelParameter("name", ModelPrimitives.TEXT, "data");
        assertTrue(param.equals(param));
    }

    @Test
    void testEquals_NullObject() {
        ModelParameter param = new ModelParameter("name", ModelPrimitives.TEXT, "data");
        assertFalse(param.equals(null));
    }

    @Test
    void testEquals_DifferentClass() {
        ModelParameter param = new ModelParameter("name", ModelPrimitives.TEXT, "data");
        Object other = new Object();
        assertFalse(param.equals(other));
    }

    @Test
    void testEquals_DifferentNameSuperClass() {
        ModelParameter param1 = new ModelParameter("name1", ModelPrimitives.TEXT, "data");
        ModelParameter param2 = new ModelParameter("name2", ModelPrimitives.TEXT, "data");
        assertFalse(param1.equals(param2));
    }

    @Test
    void testEquals_DifferentTypeSuperClass() {
        ModelParameter param1 = new ModelParameter("name", ModelPrimitives.TEXT, "data");
        ModelParameter param2 = new ModelParameter("name", ModelPrimitives.INT, "data");
        assertFalse(param1.equals(param2));
    }

    @Test
    void testEquals_DifferentData() {
        ModelParameter param1 = new ModelParameter("name", ModelPrimitives.TEXT, "data1");
        ModelParameter param2 = new ModelParameter("name", ModelPrimitives.TEXT, "data2");
        assertFalse(param1.equals(param2));
    }

    @Test
    void testEquals_DifferentAuxiliaryFlag() {
        ModelParameter param1 = new ModelParameter("name", ModelPrimitives.TEXT, "data", false);
        ModelParameter param2 = new ModelParameter("name", ModelPrimitives.TEXT, "data", true);
        assertFalse(param1.equals(param2));
    }

    @Test
    void testEquals_AllFieldsSame() {
        ModelParameter param1 = new ModelParameter("name", ModelPrimitives.TEXT, "data", true);
        ModelParameter param2 = new ModelParameter("name", ModelPrimitives.TEXT, "data", true);
        assertTrue(param1.equals(param2));
    }

    @Test
    void testGetType_ReturnsModelParameter() {
        ModelParameter param = new ModelParameter("name", ModelPrimitives.TEXT, "data");
        assertEquals(Element.ElementType.MODEL_PARAMETER, param.getType());
    }
}