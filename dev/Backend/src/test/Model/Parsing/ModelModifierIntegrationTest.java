package Model.Parsing;

import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Types.ModelPrimitives;
import Model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Integration")
public class ModelModifierIntegrationTest {

    private Model model;
    @BeforeEach
    void setUp() {
        try {
            Path path = Paths.get("src/test/Utilities/ZimplExamples/ExampleZimplProgram.zpl");
            String exampleCode = Files.readString(path);
            model = new Model(exampleCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /*
    param weight := 10;
    param absoluteMinimalSpacing := 8;
    param soldiers := 10;

    set C := {"Ben","Dan","Ron","Nir","Niv","Avi","Shlomo"};
    set Stations := {"Shin Gimel", "Fillbox"};
    #Hours from 0:00 to 20:00 in 4 hour intervals
    set Times := {0,4,8,12,16,20};
    set S := Stations * Times;
    set Possible_Soldier_Shifts := C * S; # [<Ben, <Fillbox, 4>> , <Ron, 8>]
    set Possible_Transitions := {<i,a,b,c,d> in C * S * S | b < d };
     */
    @ParameterizedTest
    @MethodSource("testCaseStream")
    void givenModelInput_WhenModifying_FileCorrect(TestInput input) {
        //Given
        Model testModel= new Model(input.zplCode);
        verifyModelPreconditions(testModel,input);
        //When
        String output= testModel.writeToSource(input.sets,input.parameters,input.constraints,input.preferenceScalars);
        Model editedModel= new Model(output);
        //Then
        verifyModelPostconditions(editedModel,input);

    }
    @Disabled("replaced with parameterized tests")
    @Test
    void givenParamEdit_WhenModifying_FileCorrect() {
        HashSet<ModelSet> sets= new HashSet<>();
        HashSet<ModelParameter> params= new HashSet<>();
        HashSet<Constraint> constraints= new HashSet<>();
        HashSet<Preference> preferences= new HashSet<>();
        params.add(new ModelParameter("soldiers", ModelPrimitives.INT,"8"));
        String output= model.writeToSource(sets,params,constraints,preferences);
        assertFalse(output.contains("soldiers := 10;"));
        assertTrue(output.contains("soldiers := 8;"));
    }
    @Disabled("replaced with parameterized tests")
    @Test
    void givenSameParamEdit_WhenModifying_FileCorrect() {
        HashSet<ModelSet> sets= new HashSet<>();
        HashSet<ModelParameter> params= new HashSet<>();
        HashSet<Constraint> constraints= new HashSet<>();
        HashSet<Preference> preferences= new HashSet<>();
        params.add(new ModelParameter("soldiers",ModelPrimitives.INT,"8"));
        String output1= model.writeToSource(sets,params,constraints,preferences);
        String output2= model.writeToSource(sets,params,constraints,preferences);
        assertFalse(output1.contains("soldiers := 10;"));
        assertTrue(output1.contains("soldiers := 8;"));
        assertFalse(output2.contains("soldiers := 10;"));
        assertTrue(output2.contains("soldiers := 8;"));
    }
    @Disabled("replaced with parameterized tests")
    @Test
    void givenSomeParamEdit_WhenModifying_FileCorrect() {
        HashSet<ModelSet> sets= new HashSet<>();
        HashSet<ModelParameter> params= new HashSet<>();
        HashSet<Constraint> constraints= new HashSet<>();
        HashSet<Preference> preferences= new HashSet<>();
        params.add(new ModelParameter("soldiers",ModelPrimitives.INT,"8"));
        params.add(new ModelParameter("absoluteMinimalSpacing",ModelPrimitives.INT,"6"));
        params.add(new ModelParameter("weight",ModelPrimitives.INT,"5"));
        String output= model.writeToSource(sets,params,constraints,preferences);
        assertFalse(output.contains("soldiers := 10;"));
        assertTrue(output.contains("soldiers := 8;"));
        assertFalse(output.contains("absoluteMinimalSpacing := 8;"));
        assertTrue(output.contains("absoluteMinimalSpacing := 6;"));
        assertFalse(output.contains("weight := 10;"));
        assertTrue(output.contains("weight := 5;"));
    }

    /**
     * Define test cases using TestInput inner record
     * @return stream of cases, for use in Parameterized tests
     */
    private static Stream<TestInput> testCaseStream(){

        try {
            Path path = Paths.get("src/test/Utilities/ZimplExamples/ExampleZimplProgram.zpl");
            String SoldiersExampleCode = Files.readString(path);
            path = Paths.get("src/test/Utilities/ZimplExamples/BasicZimplProgram.zpl");
            String BasicExampleCode = Files.readString(path);
            return Stream.of(
                    new TestInput(
                            Set.of(), //params
                            Set.of(), //sets
                            Set.of(), //constraints
                            Set.of(), //scalars
                            SoldiersExampleCode),
                    new TestInput(
                            Set.of(new ModelParameter("soldiers", ModelPrimitives.INT, "8")), //params
                            Set.of(), //sets
                            Set.of(), //constraints
                            Set.of(), //scalars
                            SoldiersExampleCode),
                    new TestInput(
                            Set.of(new ModelParameter("soldiers", ModelPrimitives.INT, "0"),
                                    new ModelParameter("absoluteMinimalSpacing", ModelPrimitives.INT, "1")), //params
                            Set.of(), //sets
                            Set.of(), //constraints
                            Set.of(),//scalars
                            SoldiersExampleCode),
                    new TestInput(
                            Set.of(), //params
                            Set.of(new ModelSet("C",ModelPrimitives.TEXT, List.of("Ben","Dan"))), //sets
                            Set.of(), //constraints
                            Set.of(), //scalars
                            SoldiersExampleCode), //scalars
                    new TestInput(
                            Set.of(), //params
                            Set.of(new ModelSet("C",ModelPrimitives.TEXT, List.of())), //sets
                            Set.of(), //constraints
                            Set.of(), //scalars
                            SoldiersExampleCode), //scalars
                    new TestInput(
                            Set.of(), //params
                            Set.of(new ModelSet("C",ModelPrimitives.TEXT, List.of("1","2","3","4","5","6","7","8","9","0","11","12","13","14","15","16","17","18","19","000"))), //sets
                            Set.of(), //constraints
                            Set.of(), //scalars
                            SoldiersExampleCode)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    private static void verifyModelPreconditions(Model model, TestInput input) {
        input.parameters().forEach(param -> {
            try {
                ModelParameter existingParam = model.getParameterFromAll(param.getName());
                assertNotNull(existingParam,
                        String.format("Parameter '%s' should exist in original model", param.getName()));
            } catch (Exception e) {
                fail("Error thrown: "+ e.getMessage()+ String.format("\nParameter '%s' should exist in original model", param.getName()));
            }
        });

        input.sets().forEach(set -> {
            try {
                ModelSet existingSet = model.getSet(set.getName());
                assertNotNull(existingSet,
                        String.format("Set '%s' should exist in original model", set.getName()));
            }
            catch (Exception e){
                fail("Error thrown: "+ e.getMessage()+ String.format("\nSet '%s' should exist in original model", set.getName()));
            }
        });

        input.constraints().forEach(constraint -> {
            try {
                Constraint existingConstraint = model.getConstraint(constraint.getName());
                assertNotNull(existingConstraint,
                        String.format("Constraint '%s' should exist in original model", constraint.getName()));
            } catch (Exception e) {
                fail("Error thrown: "+ e.getMessage()+ String.format("\nConstraint '%s' should exist in original model", constraint.getName()));
            }
        });

        input.preferenceScalars().forEach(preference -> {
            try {
                Preference existingPreference = model.getPreference(preference.getName());
                assertTrue(model.hasScalar(preference),
                        String.format("Preference '%s' should have a scalar in original model", preference.getName()));
                assertNotNull(existingPreference,
                        String.format("Preference '%s' should exist in original model", preference.getName()));
            } catch (Exception e) {
                fail("Error thrown: "+ e.getMessage()+ String.format("\nPreference '%s' should exist in original model", preference.getName()));
            }
        });
    }

    private static void verifyModelPostconditions(Model model, TestInput input) {
        input.parameters().forEach(param -> {
            try {
                ModelParameter existingParam = model.getParameterFromAll(param.getName());
                assertNotNull(existingParam,
                        String.format("Parameter '%s' should exist in original model", param.getName()));
                assertEquals(existingParam.getData(),param.getData(),
                        String.format("Parameter '%s' value of '%s' should be %s.", param.getName(),existingParam.getData(),param.getData()));
            } catch (Exception e) {
                fail("Error thrown: "+ e.getMessage()+ String.format("\nWhen checking post-condition for parameter: %s", param.getName()));
            }
        });

        input.sets().forEach(set -> {
            try {
                ModelSet existingSet = model.getSet(set.getName());
                assertNotNull(existingSet,
                        String.format("Set '%s' should exist in original model", set.getName()));
                assertEquals(existingSet.getData(),set.getData(),
                        String.format("Set '%s' value of '%s' should be %s.", set.getName(),existingSet.getData(),set.getData()));
            } catch (Exception e) {
                fail("Error thrown: "+ e.getMessage()+ String.format("\nWhen checking post-condition for set: %s", set.getName()));
            }
        });

        input.constraints().forEach(constraint -> {
            try {
                Constraint existingConstraint = model.getConstraint(constraint.getName());
                assertNotNull(existingConstraint,
                        String.format("Constraint '%s' should exist in original model", constraint.getName()));
                assertEquals(existingConstraint.isOn(),constraint.isOn());
            } catch (Exception e) {
                fail("Error thrown: "+ e.getMessage()+ String.format("\nWhen checking post-condition for constraint: %s", constraint.getName()));
            }
        });

        input.preferenceScalars().forEach(preference -> {
            try {
                Preference existingPreference = model.getPreference(preference.getName());
                assertTrue(model.hasScalar(preference),
                        String.format("Preference '%s' should have a scalar in original model", preference.getName()));
                assertNotNull(existingPreference,
                        String.format("Preference '%s' should exist in original model", preference.getName()));
                assertEquals(existingPreference.getScalar(),preference.getScalar(),
                        String.format("Preference '%s' scalar should be %s, got: %s",existingPreference.getName(),preference.getScalar(),existingPreference.getScalar()));
            } catch (Exception e) {
                fail("Error thrown: "+ e.getMessage()+ String.format("\nWhen checking post-condition for preference: %s", preference.getName()));
            }
        });
    }
    private record TestInput(
            Set<ModelParameter> parameters,
            Set<ModelSet> sets,
            Set<Constraint> constraints,
            Set<Preference> preferenceScalars,
            String zplCode
    )
    { }
}
