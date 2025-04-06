package Model;

import Exceptions.InternalErrors.BadRequestException;
import Exceptions.InternalErrors.ModelExceptions.EngineErrorException;
import Exceptions.InternalErrors.ModelExceptions.InvalidModelInputException;
import Exceptions.InternalErrors.ModelExceptions.Parsing.ParsingException;
import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.OperationalElement;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;
import Model.Data.Types.ModelType;
import Model.Parsing.CollectorVisitor;
import Model.Parsing.ModifierVisitor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import parser.FormulationLexer;
import parser.FormulationParser;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
public class Model implements ModelInterface {
    private final String sourceFilePath;
    ParseTree tree;
    private CommonTokenStream tokens;
    private final Map<String, ModelSet> sets = new HashMap<>();
    private final Map<String, ModelParameter> params = new HashMap<>();
    private final Map<String, Constraint> constraints = new HashMap<>();
    private final Map<String, Preference> preferences = new HashMap<>();
    private final Map<String, Variable> variables = new HashMap<>();
    private final Set<String> toggledOffFunctionalities = new HashSet<>();

    public String getSourceFilePath () {
        return sourceFilePath;
    }

    public Map<String,ModelSet> getSetsMap(){
        return sets;
    }
    public Map<String,ModelParameter> getParamsMap(){
        return params;
    }
    public Map<String,Constraint> getConstraintsMap(){
        return constraints;
    }

    public Map<String,Preference> getPreferencesMap(){
        return preferences;
    }

    public Map<String,Variable> getVariablesMap(){
        return variables;
    }

    public ParseTree getTree () {
        return tree;
    }

    public CommonTokenStream getTokens () {
        return tokens;
    }

    public Set<String> getToggledOffFunctionalities () {
        return toggledOffFunctionalities;
    }

    public String getZimplCompilationScript () {
        return zimplCompilationScript;
    }

    public String getZimplSolveScript () {
        return zimplSolveScript;
    }

    public String getOriginalSource () {
        return originalSource;
    }

    private final String zimplCompilationScript = "src/main/resources/zimpl/checkCompilation.sh";
    private final String zimplSolveScript = "src/main/resources/zimpl/solve.sh" ;
    private String originalSource;
    
    public Model(String sourceFilePath) {
        try {
            if (!Files.exists(Paths.get(sourceFilePath))) {
                throw new ParsingException("File does not exist: " + sourceFilePath);
            }
            this.sourceFilePath = sourceFilePath;
            parseSource();
        }
        catch (IOException e) {
            throw new ParsingException("I/O error parsing source file: " + e.getMessage());
        }
    }
    
    private void parseSource() throws IOException {
        originalSource = new String(Files.readAllBytes(Paths.get(sourceFilePath)));
        CharStream charStream = CharStreams.fromString(originalSource);
        FormulationLexer lexer = new FormulationLexer(charStream);
        tokens = new CommonTokenStream(lexer);
        FormulationParser parser = new FormulationParser(tokens);
        tree = parser.program();
        
        // Initial parse to collect all declarations
        CollectorVisitor collector = new CollectorVisitor(this);
        collector.visit(tree);
    }

    @Override
    public String getSourceCode () {
        return originalSource;
    }

    public void appendToSet(ModelSet set, String value) {
        // if (!sets.containsKey(setName)) {
        //     throw new IllegalArgumentException("Set " + setName + " not found");
        // }
        if(!set.isCompatible(value))
            throw new InvalidModelInputException("set "+set.getName()+" is incompatible with given input: "+value+" , expected type: "+set.getType());
        
        ModifierVisitor modifier = new ModifierVisitor(this, tokens, set.getName(), value, ModifierVisitor.Action.APPEND, originalSource);
        modifier.visit(tree);
        
        if (modifier.isModified()) {
            try {
                // Write modified source back to file, preserving original formatting
                String modifiedSource = modifier.getModifiedSource();
                Files.write(Paths.get(sourceFilePath), modifiedSource.getBytes());
                parseSource();
            }
            catch (IOException e) {
                throw new ParsingException("Error writing to source file: " + e.getMessage());
            }
        }
    }
    
    public void removeFromSet(ModelSet set, String value) {
        // if (!sets.containsKey(setName)) {
        //     throw new IllegalArgumentException("Set " + setName + " not found");
        // }
        if(!set.isCompatible(value))
            throw new InvalidModelInputException("set "+set.getName()+" is incompatible with given input: "+value+" , expected type: "+set.getType());

        ModifierVisitor modifier = new ModifierVisitor(this, tokens, set.getName(), value,  ModifierVisitor.Action.DELETE, originalSource);
        modifier.visit(tree);
        
        if (modifier.isModified()) {

            try {
                // Write modified source back to file, preserving original formatting
                String modifiedSource = modifier.getModifiedSource();
                Files.write(Paths.get(sourceFilePath), modifiedSource.getBytes());
                parseSource();
            } catch (IOException e) {
                throw new ParsingException("Error writing to source file: " + e.getMessage());
            }
        }
    }

    public void setInput(ModelParameter identifier, String value){

        if(!identifier.isCompatible(value))
            throw new InvalidModelInputException("parameter "+identifier.getName()+" is incompatible with given input: "+value +" expected type: "+identifier.getType());
        
        ModifierVisitor modifier = new ModifierVisitor(this, tokens, identifier.getName(), value,  ModifierVisitor.Action.SET, originalSource);
        modifier.visit(tree);
        
        if (modifier.isModified()) {
            try {
                // Write modified source back to file, preserving original formatting
                String modifiedSource = modifier.getModifiedSource();
                Files.write(Paths.get(sourceFilePath), modifiedSource.getBytes());
                parseSource();
            } catch (IOException e) {
                throw new ParsingException("Error writing to source file: " + e.getMessage());
            }
        }
    }

    public void setInput(ModelSet identifier, String[] values) {

        for(String str : values){
            if(!identifier.isCompatible(str))
                throw new InvalidModelInputException("set "+identifier.getName()+" is incompatible with given input: "+str+" , expected type: "+identifier.getType());

        }
        
        ModifierVisitor modifier = new ModifierVisitor(this, tokens, identifier.getName(), values,  ModifierVisitor.Action.SET, originalSource);
        modifier.visit(tree);
        
        if (modifier.isModified()) {
            try {
                // Write modified source back to file, preserving original formatting
                String modifiedSource = modifier.getModifiedSource();
                Files.write(Paths.get(sourceFilePath), modifiedSource.getBytes());
                parseSource();
            } catch (IOException e) {
                throw new ParsingException("Error writing to source file: " + e.getMessage());
            }
        }
    }
    
    //TODO: the design is fucked up, and it's apparent in getInput methods. I need to make a better design of things.
    @Override
    public List<String> getInput(ModelParameter parameter) {
        if(parameter == null)
            throw new InvalidModelInputException("Trying to get input of a null parameter!");
        if(params.get(parameter.getName()) == null)
            throw new InvalidModelInputException("parameter " + parameter.getName() + " doesnt exist ");
        if(params.get(parameter.getName()).getData() == null)
            throw new InvalidModelInputException("parameter " + parameter.getName() + " is not set to have an input, or is not a declarative parameter");
        parameter = params.get(parameter.getName());

        return ModelType.convertStringToAtoms(parameter.getData()).getFirst(); //TODO FIX THIS SHIT
        
    }

    @Override
    public List<List<String>> getInput(ModelSet set) {
        if(set == null || set.getName() == null)
            throw new InvalidModelInputException("Trying to get input of a null set!");
        if(sets.get(set.getName()) == null)
            throw new InvalidModelInputException("set " + set.getName() + " doesnt exist ");
        if(sets.get(set.getName()).getData() == null)
            throw new InvalidModelInputException("set " + set.getName() + " is not set to have an input, or is not declarative set");
        
        set = sets.get(set.getName());
        List<List<String>> ans = new LinkedList<>();
        for(String element :set.getData() ){
            ans.addAll(ModelType.convertStringToAtoms(element));
        }
        return ans;
        
    }

    public void toggleFunctionality(OperationalElement operationalElement, boolean turnOn) {
        if (!turnOn) {
            toggledOffFunctionalities.add(operationalElement.getName());
        } else {
            toggledOffFunctionalities.remove(operationalElement.getName());
        }
    }

    private void commentOutToggledFunctionalities() throws IOException {
        if (toggledOffFunctionalities.isEmpty()) {
            return;
        }

        ModifierVisitor modifier = new ModifierVisitor(this, tokens, null, "", ModifierVisitor.Action.COMMENT_OUT, originalSource);
        modifier.setTargetFunctionalities(toggledOffFunctionalities); // Set functionalities to be commented out
        modifier.visit(tree);
        
        if (modifier.isModified()) {
            String modifiedSource = modifier.getModifiedSource();
            Files.write(Paths.get(sourceFilePath), modifiedSource.getBytes());
        //    parseSource();
        }
    }

    private void restoreToggledFunctionalities() throws IOException {
        if (toggledOffFunctionalities.isEmpty()) {
            return;
        }

        // Read the original file content and restore it
        Files.write(Paths.get(sourceFilePath), originalSource.getBytes());
        parseSource();
    }
    

    public boolean isCompiling(float timeout) {
        boolean ans = false;
        try {
            commentOutToggledFunctionalities();
    
            ProcessBuilder processBuilder = new ProcessBuilder(
                "scip", "-c", "read " + sourceFilePath + " q"
            );
            processBuilder.redirectErrorStream(true);
            
            Process process;
            try {
                process = processBuilder.start();
            } catch (IOException e) {
                // Check if it's specifically a "command not found" error
                if (e.getMessage().contains("Cannot run program \"scip\"") || 
                    e.getMessage().contains("error=2")) {
                    throw new EngineErrorException("SCIP solver not found. Please ensure SCIP is installed and available in system PATH");
                }
                throw e; // Rethrow other IOExceptions
            }
    
            // Executor service to handle timeouts
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Boolean> future = executor.submit(() -> {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    
                    if (line.contains("original problem has")) {
                        return true;
                    } else if (line.contains("error reading file")) {
                        return false;
                    }
                }

                throw new BadRequestException("Error checking compilation: "+ output);
            });
    
            try {
                ans = future.get((long) timeout, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                process.destroy(); // Kill the process if timeout occurs
            } finally {
                executor.shutdown();
            }
    
            restoreToggledFunctionalities();
            return ans;
        } catch (Exception e) {
            try {
                restoreToggledFunctionalities();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            // If it's our SCIP not found exception, rethrow it
            if (e instanceof BadRequestException) {
                throw (BadRequestException) e;
            }
            
            e.printStackTrace();
            return false;
        }
    }
    
    public Solution solve(float timeout, String solutionFileSuffix) {
        if(solutionFileSuffix == null)
            throw new InvalidModelInputException("solutionFileSufix is null");
        Solution ans = null;
        try {
            commentOutToggledFunctionalities();
    
            ProcessBuilder processBuilder = new ProcessBuilder(
                "scip", "-c", "read " + sourceFilePath + " optimize display solution q"
            );
            processBuilder.redirectErrorStream(true);
            
            Process process;
            try {
                process = processBuilder.start();
            } catch (IOException e) {
                // Check if it's specifically a command not found error
                if (e.getMessage().contains("Cannot run program \"scip\"") || 
                    e.getMessage().contains("error=2")) {
                    throw new EngineErrorException("SCIP solver not found. Please ensure SCIP is installed and available in system PATH");
                }
                throw e; // Rethrow other IOExceptions
            }
    
            // Executor service to handle timeouts
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Solution> future = executor.submit(() -> {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                boolean capture = false;
                StringBuilder buffer = new StringBuilder();
                int count = 0;
                while ((line = reader.readLine()) != null) {
                    // Remove lines starting with '@'
                    if (line.startsWith("@@")) continue;
    
                    // Remove excessive newlines
                    if (line.trim().isEmpty() && (output.isEmpty() || output.toString().endsWith("\n"))) {
                        continue;
                    }

                    if(line.matches("file .* not found"))
                        throw new BadRequestException("Error: Tried solving non existing file: "+ line);
                    
                    if (line.matches(".*Error [0-9]{1,4}:.*")){
                        while ((line += reader.readLine()) != null)
                        {}
                        throw new EngineErrorException("Error: Solving is unsuccesful: "+ line);
                    }
                    
                    // Capture lines after "SCIP Status" until the end
                    if (line.contains("SCIP Status")) {
                        buffer.setLength(0); // Clear buffer
                        capture = true;
                    }
                    if (capture) {
                        buffer.append(line).append("\n");
                    }
                }
    
                String filteredOutput = buffer.toString().lines()
                    .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                        return String.join("\n", list);
                    }));
    
                Files.write(Paths.get(sourceFilePath + solutionFileSuffix), filteredOutput.getBytes());
                return new Solution(Paths.get(sourceFilePath + solutionFileSuffix).toString());
            });
    
            try {
                ans = future.get((long) timeout, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                process.destroy(); // Kill the process if timeout occurs
                ans = null;
            } finally {
                executor.shutdown();
            }
    
            restoreToggledFunctionalities();
            return ans;
        } catch (Exception e) {
            try {
                restoreToggledFunctionalities();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return null;
        }
    }

    public List<FormulationParser.UExprContext> findComponentContexts(FormulationParser.NExprContext ctx) {
        List<FormulationParser.UExprContext> components = new ArrayList<>();
        findComponentContextsRecursive(ctx.uExpr(), components);
        return components;
    }

    private void findComponentContextsRecursive(FormulationParser.UExprContext ctx, List<FormulationParser.UExprContext> components) {
        String all = ctx == null ? null : ctx.getText();
        if(ctx == null)
            return;
        // if(ctx.basicExpr() != null){
        //     components.add(ctx);
        //     return;
        // }
        // findComponentContextsRecursive(ctx.uExpr(0), components);
        // findComponentContextsRecursive(ctx.uExpr(1), components);
        if (components.size() == 0 && ctx.uExpr() != null && ctx.uExpr(1) != null) {
            String a = ctx.uExpr(1).getText();
            components.add(ctx.uExpr(1));
        } else if (components.size() == 0){
            components.add(ctx);
        }
        String b = ctx.getText();
        if (ctx.uExpr(0) != null && ctx.uExpr(0).uExpr(1) != null) {
            String c = ctx.uExpr(0).uExpr(1).getText();
            if(ctx.uExpr(0).uExpr(0).basicExpr() != null){
                components.add(ctx.uExpr(0));    
                return;
            }
            else
                components.add(ctx.uExpr(0).uExpr(1));
        } else if(ctx.uExpr(0) != null){
            components.add(ctx.uExpr(0));
        } else {
            components.add(ctx);
        }
        
        findComponentContextsRecursive(ctx.uExpr(0), components);
    }


    public ModelSet getSet(String identifier) {
        return sets.get(identifier);
    }
    
    public ModelParameter getParameter(String identifier) {
        return params.get(identifier);
    }
    
    public Constraint getConstraint(String identifier) {
        return constraints.get(identifier);
    }

    @Override
    public Collection<Constraint> getConstraints() {
        return constraints.values();
    }

    public Preference getPreference(String identifier) {
        return preferences.get(identifier);
    }

    @Override
    public Collection<Preference> getPreferences() {
        return preferences.values();
    }

    public Variable getVariable(String identifier) {
        return variables.get(identifier);
    }

    @Override
    public Collection<Variable> getVariables() {
        return variables.values();
    }
    @Override
    public Collection<ModelSet> getSets(){
        return this.sets.values();
    }
    
    @Override
    public Collection<ModelParameter> getParameters(){
        return this.params.values();
    }
    @Override
    public Collection<Variable> getVariables(Collection<String> identifiers){
        HashSet<Variable> set = new HashSet<>();
        for (String identifier : identifiers) {
            set.add(getVariable(identifier));
        }
        return set;
    }


}