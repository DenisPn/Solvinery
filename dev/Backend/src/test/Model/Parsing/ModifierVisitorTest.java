package Model.Parsing;

import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Model;
import com.jayway.jsonpath.internal.function.text.Concatenate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ModifierVisitorTest {
    private String exampleCode;
    private Model model;
    @BeforeEach
    void setUp() {
        try {
            Path path = Paths.get("src/test/Utilities/ZimplExamples/ExampleZimplProgram.zpl");
            this.exampleCode = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        model = new Model(exampleCode);
        HashSet<ModelSet> sets= new HashSet<>();
        HashSet<ModelParameter> params= new HashSet<>();
        HashSet<Constraint> constraints= new HashSet<>();
        HashSet<Preference> preferences= new HashSet<>();
        String output= model.writeToSource(sets,params,constraints,preferences);
        System.out.println(output);
    }

    @Test
    void visitParamDecl() {
    }

    @Test
    void visitSetDefExpr() {
    }

    @Test
    void visitConstraint() {
    }
}