package Model.Parsing;

import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Types.ModelPrimitives;
import Model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Integration")
public class ModelModifierIntegrationTest {

   // private Model model;
    @BeforeEach
    void setUp() {
        /*try {
            Path path = Paths.get("src/test/Utilities/ZimplExamples/ExampleZimplProgram.zpl");
            //String exampleCode = Files.readString(path);
           // model = new Model(exampleCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
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
    (((maxGuards-minGuards)+weight)**3) * scalar1354200841
    ((minimalSpacing)**2) * scalar1883673267
    (sum<i,a,b> in Possible_Soldier_Shifts: sum<m,n> in S | m != a or b!=n :(Soldier_Shift[i,a,b] * Soldier_Shift[i,m,n] * (b-n))) * scalar692447860
     */
    @ParameterizedTest
    @MethodSource("testCaseStream")
    void givenModelInput_WhenModifying_FileCorrect(@NonNull TestInput input) {
        //Given
        Model testModel= new Model(input.zplCode);
        verifyModelPreconditions(testModel,input);
        //When
        String output= testModel.writeToSource(input.sets,input.parameters,input.constraints,input.preferenceScalars);
        Model editedModel= new Model(output);
        //Then
        verifyModelPostconditions(editedModel,input);

    }


    /**
     * Define test cases using TestInput inner record
     * @return stream of cases, for use in Parameterized tests
     */
    @NonNull
    private static Stream<TestInput> testCaseStream(){

        try {
            Path path = Paths.get("src/test/Utilities/ZimplExamples/ExampleZimplProgram.zpl");
            String SoldiersExampleCode = Files.readString(path);
            path = Paths.get("src/test/Utilities/ZimplExamples/BasicZimplProgram.zpl");
            String BasicExampleCode = Files.readString(path);
            return Stream.of(
                    new TestInput( //1
                            Set.of(), //params
                            Set.of(), //sets
                            Set.of(), //constraints
                            Map.of(), //scalars
                            Map.of(),
                            SoldiersExampleCode),
                    new TestInput( //2
                            Set.of(), //params
                            Set.of(), //sets
                            Set.of(), //constraints
                            Map.of(), //scalars
                            Map.of(),
                            ""),
                    new TestInput( //3
                            Set.of(new ModelParameter("soldiers", ModelPrimitives.INT, "8")), //params
                            Set.of(), //sets
                            Set.of(), //constraints
                            Map.of(), //scalars
                            Map.of(),
                            SoldiersExampleCode),
                    new TestInput( //4
                            Set.of(new ModelParameter("soldiers", ModelPrimitives.INT, "0"),
                                    new ModelParameter("absoluteMinimalSpacing", ModelPrimitives.INT, "1")), //params
                            Set.of(), //sets
                            Set.of(), //constraints
                            Map.of(),//scalars
                            Map.of(),
                            SoldiersExampleCode),
                    new TestInput( //5
                            Set.of(), //params
                            Set.of(new ModelSet("C",ModelPrimitives.TEXT, List.of("Ben","Dan"))), //sets
                            Set.of(), //constraints
                            Map.of(), //scalars
                            Map.of(),
                            SoldiersExampleCode),
                    new TestInput( //6
                            Set.of(), //params
                            Set.of(new ModelSet("C",ModelPrimitives.TEXT, List.of())), //sets
                            Set.of(), //constraints
                            Map.of(), //scalars
                            Map.of(),
                            SoldiersExampleCode),
                    new TestInput( //7
                            Set.of(), //params
                            Set.of(new ModelSet("C",ModelPrimitives.TEXT, List.of("1","2","3","4","5","6","7","8","9","0","11","12","13","14","15","16","17","18","19","000"))), //sets
                            Set.of(), //constraints
                            Map.of(), //scalars
                            Map.of(),
                            SoldiersExampleCode),
                    new TestInput( //8
                            Set.of(), //params
                            Set.of(), //sets
                            Set.of(new Constraint("trivial1")), //constraints
                            Map.of(), //scalars
                            Map.of(),
                            SoldiersExampleCode),
                    new TestInput( //9
                            Set.of(), //params
                            Set.of(), //sets
                            Set.of(new Constraint("trivial1")), //constraints
                            Map.of(), //scalars
                            Map.of(),
                            SoldiersExampleCode),
                    new TestInput( //10
                            Set.of(), //params
                            Set.of(), //sets
                            Set.of(new Constraint("minGuardsCons"),new Constraint("maxGuardsCons")), //constraints
                            Map.of(), //scalars
                            Map.of(),
                            SoldiersExampleCode),
                    new TestInput( //11
                            Set.of(), //params
                            Set.of(), //sets
                            Set.of(new Constraint("minGuardsCons"),new Constraint("maxGuardsCons"),
                                    new Constraint("trivial1"),new Constraint("trivial2"),
                                    new Constraint("trivial3"),new Constraint("trivial4"),
                                    new Constraint("trivial5"),new Constraint("Soldier_Not_In_Two_Stations_Concurrently"),
                                    new Constraint("All_Stations_One_Soldier"),new Constraint("minimalSpacingCons")), //constraints
                            Map.of(), //scalars
                            Map.of(),
                            SoldiersExampleCode),
                    new TestInput( //12
                            Set.of(), //params
                            Set.of(), //sets
                            Set.of(new Constraint("minGuardsCons"),new Constraint("maxGuardsCons"),
                                    new Constraint("trivial1"),new Constraint("trivial2"),
                                    new Constraint("trivial3"),new Constraint("trivial4"),
                                    new Constraint("trivial5"),new Constraint("Soldier_Not_In_Two_Stations_Concurrently"),
                                    new Constraint("All_Stations_One_Soldier"),new Constraint("minimalSpacingCons")), //constraints
                            Map.of(), //scalars
                            Map.of(),
                            SoldiersExampleCode),
                    new TestInput( //13
                            Set.of(), //params
                            Set.of(), //sets
                            Set.of(), //constraints
                            Map.of("((maxGuards-minGuards)+weight)**3", 0.5F), //scalars
                            Map.of(),
                            SoldiersExampleCode),
                    new TestInput( //14
                            Set.of(), //params
                            Set.of(), //sets
                            Set.of(), //constraints
                            Map.of("((maxGuards-minGuards)+weight)**3", 0F), //scalars
                            Map.of(),
                            SoldiersExampleCode),
                    new TestInput( //15
                            Set.of(), //params
                            Set.of(), //sets
                            Set.of(), //constraints
                            Map.of("((maxGuards-minGuards)+weight)**3", 1F), //scalars
                            Map.of("(((maxGuards-minGuards)+weight)**3) * scalar1354200841",1F),
                            SoldiersExampleCode),
                    new TestInput( //16
                            Set.of(), //params
                            Set.of(), //sets
                            Set.of(), //constraints
                            Map.of("((maxGuards-minGuards)+weight)**3", 0.5F,
                                   "(minimalSpacing)**2", 0.5F,
                                   "sum<i,a,b> in Possible_Soldier_Shifts: sum<m,n> in S | m != a or b!=n :(Soldier_Shift[i,a,b] * Soldier_Shift[i,m,n] * (b-n))", 0.5F),//scalars
                            Map.of("(((maxGuards-minGuards)+weight)**3) * scalar1354200841",0.5F,
                                    "((minimalSpacing)**2) * scalar1883673267",0.5F,
                                    "(sum<i,a,b> in Possible_Soldier_Shifts: sum<m,n> in S | m != a or b!=n :(Soldier_Shift[i,a,b] * Soldier_Shift[i,m,n] * (b-n))) * scalar692447860",0.5F),
                            SoldiersExampleCode),
                    new TestInput( //17
                            Set.of(new ModelParameter("soldiers", ModelPrimitives.INT, "0"),
                                    new ModelParameter("absoluteMinimalSpacing", ModelPrimitives.INT, "1")), //params
                            Set.of(new ModelSet("C",ModelPrimitives.TEXT, List.of("1","2","3","4","5","6","7","8","9","0","11","12","13","14","15","16","17","18","19","000")),
                                    new ModelSet("Stations",ModelPrimitives.TEXT, List.of("1","2","3","4","5","6","7","8","9","0","11","12","13","14","15","16","17","18","19","000")),
                                    new ModelSet("Times",ModelPrimitives.INT, List.of("1","2","3","4","5","6","7","8","9","0","11","12","13","14","15","16","17","18","19","000"))),//sets
                            Set.of(new Constraint("minGuardsCons"),new Constraint("maxGuardsCons"),
                                    new Constraint("trivial1"),new Constraint("trivial2"),
                                    new Constraint("trivial3"),new Constraint("trivial4"),
                                    new Constraint("trivial5"),new Constraint("Soldier_Not_In_Two_Stations_Concurrently"),
                                    new Constraint("All_Stations_One_Soldier"),new Constraint("minimalSpacingCons")), //constraints
                            Map.of("((maxGuards-minGuards)+weight)**3", 0.5F,
                                   "(minimalSpacing)**2", 0.5F,
                                   "sum<i,a,b> in Possible_Soldier_Shifts: sum<m,n> in S | m != a or b!=n :(Soldier_Shift[i,a,b] * Soldier_Shift[i,m,n] * (b-n))", 0.5F),//scalars
                            Map.of("(((maxGuards-minGuards)+weight)**3) * scalar1354200841",0.5F,
                                    "((minimalSpacing)**2) * scalar1883673267",0.5F,
                                    "(sum<i,a,b> in Possible_Soldier_Shifts: sum<m,n> in S | m != a or b!=n :(Soldier_Shift[i,a,b] * Soldier_Shift[i,m,n] * (b-n))) * scalar692447860",0.5F),
                            SoldiersExampleCode)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    private static void verifyModelPreconditions(@NonNull Model model, @NonNull TestInput input) {
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

        input.modifiedPreferences.keySet().forEach(preference -> {
            try {
                Preference existingPreference = model.getPreference(preference);
                assertNotNull(existingPreference,
                        String.format("Preference '%s' should exist in original model", existingPreference.getName()));
                assertTrue(model.hasScalar(existingPreference),
                        String.format("Preference '%s' should have a scalar in original model", existingPreference.getName()));
            } catch (Exception e) {
                fail("Error thrown: "+ e.getMessage()+ String.format("\n When check pre-condition for preference %s", preference));
            }
        });
    }

    private static void verifyModelPostconditions(@NonNull Model model, @NonNull TestInput input) {
        input.parameters().forEach(param -> {
            try {
                ModelParameter existingParam = model.getParameterFromAll(param.getName());
                assertNotNull(existingParam,
                        String.format("Parameter '%s' should exist in modified model", param.getName()));
                assertEquals(existingParam.getData(),param.getData(),
                        String.format("Parameter '%s' value of '%s' should be %s.", param.getName(),existingParam.getData(),param.getData()));
            } catch (Exception e) {
                fail("Error thrown: "+ e.getMessage()+ String.format("\nWhen checking pre-condition for parameter: %s", param.getName()));
            }
        });

        input.sets().forEach(set -> {
            try {
                ModelSet existingSet = model.getSet(set.getName());
                assertNotNull(existingSet,
                        String.format("Set '%s' should exist in modified model", set.getName()));
                assertEquals(existingSet.getData(),set.getData(),
                        String.format("Set '%s' value of '%s' should be %s.", set.getName(),existingSet.getData(),set.getData()));
            } catch (Exception e) {
                fail("Error thrown: "+ e.getMessage()+ String.format("\nWhen checking post-condition for set: %s", set.getName()));
            }
        });

        input.constraints().forEach(constraint -> {
            try {
               // Constraint existingConstraint = model.getConstraint(constraint.getName());
                assertNull(model.getConstraint(constraint.getName()));
                /*if(constraint.isOn()) {
                    assertNotNull(existingConstraint,
                            String.format("Constraint '%s' should exist in modified model", constraint.getName()));
                    assertEquals(existingConstraint.isOn(), constraint.isOn());
                }
                else assertNull(existingConstraint);*/
            } catch (Exception e) {
                fail("Error thrown: "+ e.getMessage()+ String.format("\nWhen checking post-condition for constraint: %s", constraint.getName()));
            }
        });

        input.modifiedPreferences.keySet().forEach(preference -> {
            try {
                Preference existingPreference = model.getPreference(preference);
                assertNotNull(existingPreference,
                        String.format("Preference '%s' should exist in modified model", preference));
                assertTrue(model.hasScalar(existingPreference),
                        String.format("Preference '%s' should have a scalar in modified model", existingPreference.getName()));
                Float scalar = model.getScalarValue(existingPreference);
                assertEquals(scalar,input.modifiedPreferences.get(preference),
                        String.format("Preference '%s' scalar should be %s, got: %s",existingPreference.getName(),input.preferenceScalars().get(preference),scalar));
            } catch (Exception e) {
                fail("Error thrown: "+ e.getMessage()+ String.format("\nWhen checking post-condition for preference: %s", preference));
            }
        });
    }
    private record TestInput(
            Set<ModelParameter> parameters,
            Set<ModelSet> sets,
            Set<Constraint> constraints,
            Map<String,Float> preferenceScalars,
            Map<String,Float> modifiedPreferences,
            String zplCode
    )
    { }
    private record TestModifiedPreferences(
           Set<String> preferences
    )
    { }
}
