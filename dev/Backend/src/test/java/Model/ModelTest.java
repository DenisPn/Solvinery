package Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.boot.test.context.SpringBootTest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.IOException;


public class ModelTest {
    private Model model;
    private static String source = "/Plan-A/dev/Backend/src/main/resources/zimpl/ExampleZimplProgram.zpl";
    private static String TEST_FILE_PATH = "/Plan-A/dev/Backend/src/test/java/Model/TestFile.zpl";

    private static String[][] expectedParameters = {{"Conditioner","10"}, {"soldiers", "9"}, {"absoluteMinimalRivuah", "8"}};
    
    @BeforeAll
    public static void setUpFile() throws IOException {
        Path sourcePath = Path.of(source);
        Path targetPath = Path.of(TEST_FILE_PATH);
        Files.deleteIfExists(targetPath);
        
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private ModelSet getSet(Model m, String identifier) throws Exception{
        m = new Model(TEST_FILE_PATH);
        return m.getSet(identifier);
    }
    
    private ModelParameter getParameter(Model m, String identifier) throws Exception{
        m = new Model(TEST_FILE_PATH);
        return m.getParameter(identifier);
    }
    
    private ModelVariable getVariable(Model m, String identifier) throws Exception{
        m = new Model(TEST_FILE_PATH);
        return m.getVariable(identifier);
    }
    
    private ModelConstraint getConstraint(Model m, String identifier) throws Exception{
        m = new Model(TEST_FILE_PATH);
        return m.getConstraint(identifier);
    }
    
    private ModelPreference getPreference(Model m, String identifier) throws Exception{
        m = new Model(TEST_FILE_PATH);
        return m.getPreference(identifier);
    }
    
    
    @BeforeEach
    public void setUp() throws IOException {
        model = new Model(TEST_FILE_PATH);
    }
    
    @Test
    public void testModelConstruction() {
        assertNotNull(model);
        assertTrue(model.isCompiling());
    }
    
    @Test
    public void testInvalidFilePath() throws IOException {
        assertThrows(Exception.class, ()->new Model("nonexistent_file6293.txt"));
    }
    
    
    @Test
    public void testSetOperations() throws Exception {
        // Get initial set
        String setName = "Emdot";
        ModelType type = ModelPrimitives.TEXT;

        ModelSet testSet = model.getSet(setName);
        assertNotNull(testSet);
        assertEquals(testSet.identifier,setName);
        assertEquals(testSet.getType(), type);
        
        String addValue = "MyValue";

        // Test append
        model.appendToSet(testSet, addValue);
        testSet = getSet(model, setName);
        assertTrue(testSet.getElements().contains(addValue));
        assertTrue(model.isCompiling());

        // Test remove
        model.removeFromSet(testSet, addValue);
        testSet = getSet(model, setName);
        assertFalse(testSet.getElements().contains(addValue));
        assertTrue(model.isCompiling());
    }
    
    // Input Setting Tests
    @Test
    public void testSetParameterInput() throws Exception {
        String parameter = "soldiers";
        String valueToSet = "543205222";

        ModelParameter param = getParameter(model, parameter);
        assertNotNull(param);
        assertEquals(param.getType(), ModelPrimitives.INT);
        model.setInput(param, valueToSet);
        param = getParameter(model, parameter);
        assertEquals( param.getValue(), valueToSet);
        assertTrue(model.isCompiling());
    }
    
    // Functionality Toggle Tests
    @Test
    public void testToggleFunctionality() throws Exception {
        String testConstraint = "trivial1";

        ModelFunctionality mf = getConstraint(model, testConstraint);
        assertNotNull(mf);
        model.toggleFunctionality(mf, false);
        assertNull(getConstraint(model, testConstraint));
        assertTrue(model.isCompiling());

        model.toggleFunctionality(mf, true);
        assertNotNull(getConstraint(model, testConstraint));
        assertTrue(model.isCompiling());
    }

    @Test
    public void testCompilation(){
        assertFalse(true); //unimplemented
    }
    
    // Collection Getter Tests
    @Test
    public void testParameterParsing() {

        Set<ModelParameter> params = model.getParameters();
        
        
    }
    @Test
    public void testSetParsing() {
    }

    @Test
    public void testConstraintParsing() {
    }

    @Test
    public void testPreferenceParsing() {
    }
    @Test
    public void testVariableParsing() {
    }
    
    // Exception Tests
    @Test
    public void testInvalidSetAppend() throws Exception {
        assertFalse(true); //unimplemented
    }
    
    @Test
    public void testInvalidSetRemove() throws Exception {
        assertFalse(true); //unimplemented
    }
    
    @Test
    public void testInvalidParameterAssignment() throws Exception {
        assertFalse(true); //unimplemented
    }
    

    // @AfterAll
    // public static void cleanUp() throws IOException {
    //     Path targetPath = Path.of(TEST_FILE_PATH);
    //     Files.deleteIfExists(targetPath);
    // }
}
