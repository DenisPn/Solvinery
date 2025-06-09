package Model.Data.Elements;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
//using AI to generate tests with simple cases
class VariableTest {

    @Test
    void givenVariable_whenComparingWithItself_thenEqualsReturnsTrue() {
        // Given
        Variable variable = new Variable("var1", List.of("type1", "type2"), List.of("set1", "set2"));

        // When
        boolean result = variable.equals(variable);

        // Then
        assertTrue(result);
    }

    @Test
    void givenVariable_whenComparingWithNull_thenEqualReturnsFalse() {
        // Given
        Variable variable = new Variable("var1", List.of("type1", "type2"), List.of("set1", "set2"));

        // When
        boolean result = variable.equals(null);

        // Then
        assertFalse(result);
    }

    @Test
    void givenVariable_whenComparingWithDifferentClass_thenEqualReturnsFalse() {
        // Given
        Variable variable = new Variable("var1", List.of("type1", "type2"), List.of("set1", "set2"));
        String other = "Different class object";

        // When
        boolean result = variable.equals(other);

        // Then
        assertFalse(result);
    }

    @Test
    void givenTwoVariables_whenComparingWithDifferentNames_thenEqualReturnsFalse() {
        // Given
        Variable variable1 = new Variable("var1", List.of("type1", "type2"), List.of("set1", "set2"));
        Variable variable2 = new Variable("var2", List.of("type1", "type2"), List.of("set1", "set2"));

        // When
        boolean result = variable1.equals(variable2);

        // Then
        assertFalse(result);
    }

    @Test
    void givenTwoVariables_whenComparingWithDifferentTypeStructures_thenEqualReturnsFalse() {
        // Given
        Variable variable1 = new Variable("var1", List.of("type1", "type2"), List.of("set1", "set2"));
        Variable variable2 = new Variable("var1", List.of("type3", "type4"), List.of("set1", "set2"));

        // When
        boolean result = variable1.equals(variable2);

        // Then
        assertFalse(result);
    }

    @Test
    void givenTwoVariables_whenComparingWithDifferentBasicSets_thenEqualReturnsFalse() {
        // Given
        Variable variable1 = new Variable("var1", List.of("type1", "type2"), List.of("set1", "set2"));
        Variable variable2 = new Variable("var1", List.of("type1", "type2"), List.of("set3", "set4"));

        // When
        boolean result = variable1.equals(variable2);

        // Then
        assertFalse(result);
    }

    @Test
    void givenTwoIdenticalVariables_whenComparing_thenEqualReturnsTrue() {
        // Given
        Variable variable1 = new Variable("var1", List.of("type1", "type2"), List.of("set1", "set2"));
        Variable variable2 = new Variable("var1", List.of("type1", "type2"), List.of("set1", "set2"));

        // When
        boolean result = variable1.equals(variable2);

        // Then
        assertTrue(result);
    }

    @Test
    void givenTwoVariables_whenComparingWithDifferentTypeStructureOrder_thenEqualReturnsFalse() {
        // Given
        Variable variable1 = new Variable("var1", List.of("type1", "type2"), List.of("set1", "set2"));
        Variable variable2 = new Variable("var1", List.of("type2", "type1"), List.of("set1", "set2"));

        // When
        boolean result = variable1.equals(variable2);

        // Then
        assertFalse(result);
    }

    @Test
    void givenTwoVariables_whenComparingWithDifferentBasicSetsOrder_thenEqualReturnsFalse() {
        // Given
        Variable variable1 = new Variable("var1", List.of("type1", "type2"), List.of("set1", "set2"));
        Variable variable2 = new Variable("var1", List.of("type1", "type2"), List.of("set2", "set1"));

        // When
        boolean result = variable1.equals(variable2);

        // Then
        assertFalse(result);
    }

    @Test
    void givenTwoVariablesWithEmptySets_whenComparing_thenEqualReturnsTrue() {
        // Given
        Variable variable1 = new Variable("var1", List.of(), List.of());
        Variable variable2 = new Variable("var1", List.of(), List.of());

        // When
        boolean result = variable1.equals(variable2);

        // Then
        assertTrue(result);
    }
}