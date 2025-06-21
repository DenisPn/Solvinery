package Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class SolutionTest {
    private static final String VALID_STATUS_LINE = "SCIP Status        : problem is solved [optimal solution found]";
    private Solution solution;


    @BeforeEach
    void setUp() {
        solution = new Solution();
    }

    @Test
    void processLine_whenStatusLineReceived_shouldSetSolvedToTrue() {
        solution.processLine("SCIP Status        : problem is solved [optimal solution found]");
        assertTrue(solution.isSolved());
    }

    @ParameterizedTest
    @MethodSource("provideSolvingValidTimeLines")
    void processLine_whenSolvingTimeLine_shouldSetCorrectTime(ValidTime record) {
        // First set solved to true to enable time processing
        solution.processLine(VALID_STATUS_LINE);
        solution.processLine(record.line());
        assertEquals(record.expectedTime(), solution.getSolvingTime(), 0.001);
    }
    @ParameterizedTest
    @MethodSource("provideObjectiveValueLines")
    void processLine_whenObjectiveValueLine_shouldSetCorrectValue(ObjectiveValue record) {
        // First set solved to true to enable objective value processing
        solution.processLine(VALID_STATUS_LINE);
        solution.processLine(record.line());
        assertEquals(record.expectedValue(), solution.getObjectiveValue(), 0.001);
    }

    @ParameterizedTest
    @MethodSource("provideValidVariableLines")
    void GivenLine_whenValidVariableLine_ThenShouldProcessVariable(ValidVariable variable) {
        //Given
        solution.processLine(VALID_STATUS_LINE);
        solution.processLine("objective value: 100");
        //When
        solution.processLine(variable.line());
        //Then
        assertTrue(solution.rawVariableSolution.containsKey(variable.expectedName()));
        assertEquals(variable.expectedValues(), solution.rawVariableSolution.get(variable.expectedName()).getFirst().solution());
        assertEquals(variable.objectiveValue, solution.rawVariableSolution.get(variable.expectedName()).getFirst().objectiveValue());

    }

    @ParameterizedTest
    @MethodSource("provideMalformedVariableLines")
    void processLine_whenMalformedVariableLine_shouldNotThrowException(InvalidVariable variable) {
        //Given
        solution.processLine(VALID_STATUS_LINE);
        solution.processLine("objective value: 100");
        //When
        solution.processLine(variable.line());
        //Then
        assertFalse(solution.rawVariableSolution.containsKey(variable.unexpectedName()));
    }

    @Test
    void processLine_withCompleteSequence_shouldProcessCorrectly() {
        solution.processLine(VALID_STATUS_LINE);
        solution.processLine("Solving Time (sec) : 6.28");
        solution.processLine("objective value: 1187");
        solution.processLine("Soldier_Shift#1$Shin Gimel#0 1 (obj:0)");

        assertTrue(solution.isSolved());
        assertEquals(6.28, solution.getSolvingTime(), 0.001);
        assertEquals(1187, solution.getObjectiveValue(), 0.001);
        assertNotNull(solution.rawVariableSolution.get("Soldier_Shift"));
    }



    static Stream<ValidTime> provideSolvingValidTimeLines() {
        return Stream.of(
                new ValidTime("Solving Time (sec) : 12.34", 12.34),
                new ValidTime("Solving Time (sec)       : 56.78", 56.78),
                new ValidTime("Solving Time (sec) : 90.12", 90.12),
                new ValidTime("Solving Time (sec) : 1", 1.0),
                new ValidTime("Solving Time (sec) : 432113", 432113.0),
                new ValidTime("Solving Time (sec) : 0", 0.0),
                new ValidTime("Solving Time (sec) : 0.0", 0.0)
        );
    }
    static Stream<ObjectiveValue> provideObjectiveValueLines() {
        return Stream.of(
                new ObjectiveValue("objective value: 123.45", 123.45),
                new ObjectiveValue("objective value:    678.90", 678.90),
                new ObjectiveValue("objective value: 45678.0", 45678.0)
        );
    }
    static Stream<ValidVariable> provideValidVariableLines() {
        return Stream.of(
                // Basic variable cases
                new ValidVariable("variable_name#part1$part2 3 (obj:7)",
                        "variable_name", List.of("part1", "part2"), 3f),

                // Multiple delimiter cases
                new ValidVariable("complex_var#part1$part2$part3$part4 1 (obj:3)",
                        "complex_var", List.of("part1", "part2", "part3", "part4"), 1f),

                // Single part case
                new ValidVariable("simple_var#single 2 (obj:4)",
                        "simple_var", List.of("single"), 2f),

                // Variable with numbers in name
                new ValidVariable("var123#value1$value2 5 (obj:6)",
                        "var123", List.of("value1", "value2"), 5f),

                // Variable with special characters in values
                new ValidVariable("special_var#value-1$value_2 4 (obj:8)",
                        "special_var", List.of("value-1", "value_2"), 4f),

                // Variable with large objective value
                new ValidVariable("large_obj#part1$part2 999 (obj:1000)",
                        "large_obj", List.of("part1", "part2"), 999f),

                // Variable with underscore in name and values
                new ValidVariable("my_var_name#part_1$part_2 6 (obj:9)",
                        "my_var_name", List.of("part_1", "part_2"), 6f),

                // Multiple consecutive delimiters
                new ValidVariable("multi_delim#part1$$part2##part3 7 (obj:10)",
                        "multi_delim", List.of("part1", "", "part2", "", "part3"), 7f)
        );
    }

    static Stream<InvalidVariable> provideMalformedVariableLines() {
        return Stream.of(
                new InvalidVariable("malformed_variable", "malformed_variable"),
                new InvalidVariable("%%%%%%", "%%%%%%"),
                new InvalidVariable("@@random_text_here", "random_text_here"),
                new InvalidVariable("", ""),
                new InvalidVariable("     \t  \t", ""),
                new InvalidVariable("     \t  \t", "     \t  \t"),
                new InvalidVariable("Soldier_Shift#5$Shin Gimel#20                       1", "Soldier_Shift"),
                new InvalidVariable("Soldier_Shift#5$Shin Gimel#20                       abc \t(obj:0)", "Soldier_Shift"),
                new InvalidVariable("Soldier_Shift#5$Shin Gimel#20                       1 \t(obj:abc)", "Soldier_Shift"),
                new InvalidVariable("Soldier_Shift#5$Shin Gimel#20                       1 \t(obj:)", "Soldier_Shift"),
                new InvalidVariable("Soldier_Shift#5$Shin Gimel#20                         \t(obj:0)", "Soldier_Shift"),
                new InvalidVariable("Soldier_Shift#5$Shin Gimel#201(obj:0)", "Soldier_Shift"),
                new InvalidVariable("Soldier_Shift#5$Shin Gimel#20                       1 \t(obj:0", "Soldier_Shift"),
                new InvalidVariable("Soldier_Shift#5$Shin Gimel#20                       1 \t[obj:0]", "Soldier_Shift"),
                new InvalidVariable("                                                    1 \t(obj:0)", "")
        );
    }

    record ObjectiveValue(String line, double expectedValue) { }
    record ValidTime(String line, double expectedTime) { }
    record ValidVariable(String line, String expectedName, List<String> expectedValues, float objectiveValue) { }
    record InvalidVariable(String line, String unexpectedName) { }

}

