package Model.Parsing;

import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Element;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Types.ModelPrimitives;
import Model.Model;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.lang.NonNull;
import parser.FormulationLexer;
import parser.FormulationParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("Unit")
class ModifierVisitorTest {

    private Model model;
    private ModifierVisitor visitor;
    private String originalSource;

    @BeforeEach
    void setUp() {
        originalSource =
                        """
                        #processed_flag = true
                        param p1 := 10;
                        set C := {"Soldier1", "Soldier2"};
                        subto constraint1: x + y <= 5;
                        maximize obj: p1 * sum <i> in C : x[i];
                        """;
        
        model = mock(Model.class);
        CommonTokenStream tokens = setupTokens(originalSource);
        when(model.getTokens()).thenReturn(tokens);
    }

    @NonNull
    private CommonTokenStream setupTokens(@NonNull String source) {
        CharStream charStream = CharStreams.fromString(source);
        FormulationLexer lexer = new FormulationLexer(charStream);
        return new CommonTokenStream(lexer);
    }

    @Nested
    @DisplayName("Parameter Modification Tests")
    class ParameterModificationTests {
        
        @ParameterizedTest
        @ValueSource(strings = {"8",""+Integer.MAX_VALUE,""+Integer.MIN_VALUE, "0", "-1", "-9342433"})
        @DisplayName("Given a modified parameter, when modifyParamContent is called, then parameter value should be updated")
        void givenModifiedParameter_whenVisitParamDecl_thenParameterValueShouldBeUpdated(String newValue) {
            // Given
            String paramName = "p1";
            String testSource = "param p1 := 10;";
            Model realModel = new Model(testSource);
            ModelParameter parameter = new ModelParameter(paramName, ModelPrimitives.INT, newValue);
            Model spyModel = spy(realModel);
            when(spyModel.isModified(paramName, Element.ElementType.MODEL_PARAMETER)).thenReturn(true);
            when(spyModel.getParameterFromAll(paramName)).thenReturn(parameter);
            ModifierVisitor testVisitor = new ModifierVisitor(spyModel, testSource);

            // When - parse and visit
            CharStream charStream = CharStreams.fromString(testSource);
            FormulationLexer lexer = new FormulationLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            FormulationParser parser = new FormulationParser(tokens);
            FormulationParser.ProgramContext programContext = parser.program();

            testVisitor.visit(programContext);

            // Then
            String modifiedSource = testVisitor.getModifiedSource();
            assertTrue(modifiedSource.contains("param p1 := " + newValue),
        "Modified source should have an updated parameter value");
        }
        
        @Test
        @DisplayName("Given an unmodified parameter, when modifyParamContent is called, then parameter value should remain unchanged")
        void givenUnmodifiedParameter_whenVisitParamDecl_thenParameterValueShouldRemainUnchanged() {
            // Given
            String testSource = "param p1 := 10;";

            // Create a real Model instance for this specific test rather than using mocks
            Model realModel = new Model(testSource);
            // Create the visitor with the real model
            ModifierVisitor testVisitor = new ModifierVisitor(realModel, testSource);

            // When - parse and visit
            CharStream charStream = CharStreams.fromString(testSource);
            FormulationLexer lexer = new FormulationLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            FormulationParser parser = new FormulationParser(tokens);
            FormulationParser.ProgramContext programContext = parser.program();

            testVisitor.visit(programContext);
            
            // Then
            String modifiedSource = testVisitor.getModifiedSource();
            assertEquals(testSource, modifiedSource,
                "Source should remain unchanged when parameter is not modified");
        }
    }
    
    @Nested
    @DisplayName("Set Modification Tests")
    class SetModificationTests {
        
        @ParameterizedTest
        @CsvSource({
                "\"NewSoldier1\", \"NewSoldier2\"",

                "\"Test@#$\", \"!&*\"",

                "\"123\", \"ABC123\"",

                "\"שלום\", \"你好\"",

                "\"     \", \"     \"",

                "\"VeryVeryVeryLongSoldierName1\", \"VeryVeryVeryLongSoldierName2\"",

                "\"Soldier\\\"Quote\", \"Another\\\"Quote\"",

                "\"Soldier,1\", \"Soldier,2\"",

                // Empty strings
                "\"\", \"\"",
        })
        @DisplayName("Given a modified set, when modifySetContent is called, then set content should be updated")
        void givenModifiedSet_whenVisitSetDefExpr_thenSetContentShouldBeUpdated(String val1, String val2) {
            // Given
            String testSource = "set C := {\"Soldier1\", \"Soldier2\"};";
            String setName = "C";
            List<String> newData = Arrays.asList(val1, val2);
            ModelSet modelSet = new ModelSet(setName, ModelPrimitives.TEXT,true);
            modelSet.setData(newData);


            Model realModel = new Model(testSource);
            Model spyModel = spy(realModel);
            when(spyModel.isModified(setName, Element.ElementType.MODEL_SET)).thenReturn(true);
            when(spyModel.getSet(setName)).thenReturn(modelSet);
            ModifierVisitor testVisitor = new ModifierVisitor(spyModel, testSource);
            
            // When
            CharStream charStream = CharStreams.fromString(testSource);
            FormulationLexer lexer = new FormulationLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            FormulationParser parser = new FormulationParser(tokens);
            FormulationParser.ProgramContext programContext = parser.program();

            testVisitor.visit(programContext);
            
            // Then
            String expectedModified= "set C := {"+val1+", "+val2+"};";
            String modifiedSource = testVisitor.getModifiedSource();
            assertEquals(expectedModified,modifiedSource,
                "Modified source should have updated set content");
        }
        
        @Test
        @DisplayName("Given an unmodified set, when modifySetContent is called, then set content should remain unchanged")
        void givenUnmodifiedSet_whenVisitSetDefExpr_thenSetContentShouldRemainUnchanged() {
            // Given
            String setName = "C";
            String testSource = "set C := {\"Soldier1\", \"Soldier2\"};";

            Model realModel = new Model(testSource);
            Model spyModel = spy(realModel);
            when(spyModel.isModified(setName, Element.ElementType.MODEL_SET)).thenReturn(false);
            ModifierVisitor testVisitor = new ModifierVisitor(spyModel, testSource);

            // When
            CharStream charStream = CharStreams.fromString(testSource);
            FormulationLexer lexer = new FormulationLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            FormulationParser parser = new FormulationParser(tokens);
            FormulationParser.ProgramContext programContext = parser.program();

            testVisitor.visit(programContext);
            
            // Then
            String modifiedSource = testVisitor.getModifiedSource();
            assertEquals(testSource, modifiedSource,
                "Source should remain unchanged when set is not modified");
        }
    }
    
    @Nested
    @DisplayName("Constraint Modification Tests")
    class ConstraintModificationTests {
        
        @Test
        @DisplayName("Given a modified constraint, when commentOutConstraint is called, then constraint should be commented out")
        void givenModifiedConstraint_whenVisitConstraint_thenConstraintShouldBeCommentedOut() {
            // Given
            String constraintName = "constraint1";
            String testSource = "subto constraint1: x + y <= 5;";
            Constraint constraint = new Constraint(constraintName);
            Model realModel = new Model(testSource);
            Model spyModel = spy(realModel);
            when(spyModel.getConstraint(constraintName)).thenReturn(constraint);
            when(spyModel.isModified(constraintName, Element.ElementType.CONSTRAINT)).thenReturn(true);
            ModifierVisitor testVisitor = new ModifierVisitor(spyModel, testSource);

            // When
            CharStream charStream = CharStreams.fromString(testSource);
            FormulationLexer lexer = new FormulationLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            FormulationParser parser = new FormulationParser(tokens);
            FormulationParser.ProgramContext programContext = parser.program();

            testVisitor.visit(programContext);
            
            // Then
            String modifiedSource = testVisitor.getModifiedSource();
            assertEquals("# subto constraint1: x + y <= 5;", modifiedSource,
                "Modified source should be commented out constraint");
        }
        
        @Test
        @DisplayName("Given an unmodified constraint, when commentOutConstraint is called, then constraint should remain unchanged")
        void givenUnmodifiedConstraint_whenVisitConstraint_thenConstraintShouldRemainUnchanged() {
            // Given
            String constraintName = "constraint1";
            String testSource = "subto constraint1: x + y <= 5;";
            Model realModel = new Model(testSource);
            Model spyModel = spy(realModel);
            when(spyModel.isModified(constraintName, Element.ElementType.CONSTRAINT)).thenReturn(false);
            ModifierVisitor testVisitor = new ModifierVisitor(spyModel,testSource);

            // When
            CharStream charStream = CharStreams.fromString(testSource);
            FormulationLexer lexer = new FormulationLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            FormulationParser parser = new FormulationParser(tokens);
            FormulationParser.ProgramContext programContext = parser.program();
            testVisitor.visit(programContext);
            
            // Then
            String modifiedSource = testVisitor.getModifiedSource();
            assertEquals(testSource, modifiedSource,
                "Source should remain unchanged when constraint is not modified");
        }
    }
    
    @Nested
    @DisplayName("Preference Replacement Tests")
    class PreferenceReplacementTests {
        
        @Test
        @DisplayName("Given a preference not in model, when replacePreference is called, then preference should be replaced")
        void givenPreferenceNotInModel_whenVisitObjective_thenPreferenceShouldBeReplaced() {
            // Given
            String oldPreference = "p1 * sum <i> in C : x[i]";
            String newPreference = "(p1 * sum <i> in C : x[i]) * scalar123";
            String testSource = "maximize obj: p1 * sum <i> in C : x[i];";

            Preference preference = new Preference(newPreference);
            List<Preference> preferences = Collections.singletonList(preference);

            Model realModel = new Model(testSource);
            Model spyModel = spy(realModel);
            when(spyModel.hasPreference(oldPreference)).thenReturn(false);
            when(spyModel.getModifiedPreferences()).thenReturn(preferences);
            when(spyModel.getPreference(oldPreference)).thenReturn(preference);
            ModifierVisitor testVisitor = new ModifierVisitor(spyModel, testSource);
            
            //When

            CharStream charStream = CharStreams.fromString(testSource);
            FormulationLexer lexer = new FormulationLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            FormulationParser parser = new FormulationParser(tokens);
            FormulationParser.ProgramContext programContext = parser.program();
            testVisitor.visit(programContext);

            //Then
            String expected = "maximize obj: (p1 * sum <i> in C : x[i]) * scalar123;";
            String modifiedSource = testVisitor.getModifiedSource();
            assertEquals(expected,modifiedSource);
        }
        @Test
        @DisplayName("Given a preference not in model, when replacePreference is called, then preference should be replaced")
        void givenPreferenceInModel_whenVisitObjective_thenPreferenceShouldNot() {
            // Given
            String preferenceBody = "(p1 * sum <i> in C : x[i]) * scalar123";
            String testSource = "maximize obj: (p1 * sum <i> in C : x[i]) * scalar123;";

            Model realModel = new Model(testSource);
            Model spyModel = spy(realModel);
            when(spyModel.hasPreference(preferenceBody)).thenReturn(true);
            ModifierVisitor testVisitor = new ModifierVisitor(spyModel, testSource);

            //When

            CharStream charStream = CharStreams.fromString(testSource);
            FormulationLexer lexer = new FormulationLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            FormulationParser parser = new FormulationParser(tokens);
            FormulationParser.ProgramContext programContext = parser.program();
            testVisitor.visit(programContext);

            //Then
            String modifiedSource = testVisitor.getModifiedSource();
            assertEquals(testSource,modifiedSource);
        }
    }
    
    @Nested
    @DisplayName("Utility Method Tests")
    class UtilityMethodTests {

        
        @Test
        @DisplayName("Given an unmodified source, when isModified is called, then should return false")
        void givenUnmodifiedSource_whenIsModified_thenShouldReturnFalse() {
            // Given
            visitor = new ModifierVisitor(model, originalSource);
            
            // Then
            assertFalse(visitor.isModified(), "isModified should return false for unmodified source");
        }
    }
    
    private FormulationParser getParser(@NonNull String source) {
        CharStream charStream = CharStreams.fromString(source);
        FormulationLexer lexer = new FormulationLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        return new FormulationParser(tokens);
    }
}