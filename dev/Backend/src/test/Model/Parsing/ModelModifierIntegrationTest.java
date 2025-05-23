package Model.Parsing;

import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Types.ModelPrimitives;
import Model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
