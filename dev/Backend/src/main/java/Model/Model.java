package Model;

import Exceptions.InternalErrors.ModelExceptions.InvalidModelInputException;
import Exceptions.InternalErrors.ModelExceptions.InvalidModelStateException;
import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Element;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.OperationalElement;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;
import Model.Data.Types.ModelPrimitives;
import Model.Data.Types.ModelType;
import Model.Parsing.CollectorVisitor;
import Model.Parsing.ModifierVisitor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import parser.FormulationLexer;
import parser.FormulationParser;

import java.io.*;
import java.nio.file.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Model implements ModelInterface {
    private static final String PROCESSED_FLAG="#processed_flag = true\n";
    private static final String USER_NOTE="#This file has been parsed and processed by the Solvinery educational project.\n#Not all code is human written.\n";

    ParseTree tree;
    private CommonTokenStream tokens;
    private final Map<String, ModelSet> sets = new HashMap<>();
    private final Map<String, ModelParameter> params = new HashMap<>();
    private final Map<String, Constraint> constraints = new HashMap<>();
    private final Map<String, Preference> preferences = new HashMap<>();
    private final Map<String, Variable> variables = new HashMap<>();

    private final Set<String> uneditedPreferences = new HashSet<>();
    private final Map<Preference,ModelParameter> preferenceToScalar = new HashMap<>();
    private final Set<Element> modifiedElements= new HashSet<>();


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
    public Set<String> getUneditedPreferences(){
        return uneditedPreferences;
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

    public String getOriginalSource () {
        return originalSource;
    }

    private final String originalSource;
    private String currentSource;

    @Deprecated
    public Model(Path sourceFilePath) {
        /*try {
            if (!Files.exists(sourceFilePath)) {
                throw new ParsingException("File does not exist: " + sourceFilePath);
            }
            this.sourceFilePath = sourceFilePath;
            parseSource();
        }
        catch (IOException e) {
            throw new ParsingException("I/O error parsing source file: " + e.getMessage());
        }*/
        this.originalSource= null;
    }
    public Model(String sourceCode) {
            this.originalSource = sourceCode;
            parseSource();
    }
    
    private void parseSource() {
        //originalSource = new String(Files.readAllBytes(Paths.get(sourceFilePath)));
        CharStream charStream = CharStreams.fromString(originalSource);
        FormulationLexer lexer = new FormulationLexer(charStream);
        tokens = new CommonTokenStream(lexer);
        FormulationParser parser = new FormulationParser(tokens);
        tree = parser.program();
        // Initial parse to collect all declarations
        CollectorVisitor collector = new CollectorVisitor(this);
        collector.visit(tree);
        currentSource = originalSource;
        if(!currentSource.startsWith(PROCESSED_FLAG)) {
            parsePreferences();
            currentSource = PROCESSED_FLAG.concat(USER_NOTE).concat(currentSource);
        }
        else {
            readPreferences();
        }
    }

    private void readPreferences(){
        for(String preferenceBody: uneditedPreferences){
            String paramName=extractScalarParam(preferenceBody);
            if(!params.containsKey(paramName)){
                throw new InvalidModelInputException(String.format("Scalar parameters don't match preferences in previously parsed code, " +
                        "Preference %s does not have corresponding scalar param",preferenceBody));
            }
            ModelParameter scalarParam= params.get(paramName);
            Preference preference=new Preference(preferenceBody);
            try {
                preference.setScalar(Float.parseFloat(scalarParam.getData()));
            } catch (NumberFormatException e) {
                throw new InvalidModelInputException("Invalid scalar parameter value while parsing: "+e.getMessage());
            }
            preferenceToScalar.put(preference,scalarParam);
            preferences.put(preference.getName(),preference);
        }
    }
    private String extractScalarParam(String preferenceBody){
        // Matches pattern: (<anything>) * scalar<numbers>
        Pattern pattern = Pattern.compile("\\((.+?)\\)\\s*\\*\\s*(scalar\\d+)");
        Matcher matcher = pattern.matcher(preferenceBody);
        if (!matcher.find()) {
            throw new InvalidModelInputException(
                    "Previously parsed preferences must be in format '(<expression>) * scalar<number>', got: " + preferenceBody);
        }
        String originalBody = matcher.group(1);
        String scalarParam = matcher.group(2);
        String expectedHash= hashPreference(originalBody);
        if(!expectedHash.equals(scalarParam)){
            throw new InvalidModelInputException(String.format("Scalar parameters don't match preferences in previously parsed code, " +
                    "expected: %s, got: %s\n",expectedHash,scalarParam));
        }
        return scalarParam;
    }
    private void parsePreferences(){
        for(String body : uneditedPreferences){
            //build new data
            String paramName=hashPreference(body);
            String newBody="(" +body+ ") * " + paramName;
            String paramDeclaration= "param " + paramName + " := 1;\n";

            //replace preference and add scalar parameter
            currentSource = paramDeclaration.concat(currentSource);
            Preference editedPreference=new Preference(newBody);
            preferences.put(newBody,editedPreference);
            ModelParameter preferenceScalar=new ModelParameter(paramName, ModelPrimitives.FLOAT,"1",true);
            params.put(paramName,preferenceScalar);
            preferenceToScalar.put(editedPreference,preferenceScalar);
        }
    }

    public static String hashPreference(String input) {
        return "scalar"+ Math.abs(input.hashCode());
    }

    @Override
    public String writeToSource(Set<ModelSet> sets, Set<ModelParameter> params, Set<Constraint> disabledConstraints, Set<Preference> preferencesScalars) {
        //set values in model
        sets.forEach(set ->
        {
            this.sets.get(set.getName()).setData(set.getData());
            this.modifiedElements.add(set);
        });
        params.forEach(param ->
        {
            this.params.get(param.getName()).setData(param.getData());
            this.modifiedElements.add(param);
        });
        disabledConstraints.forEach(constraint ->
        {
            if(!constraint.isOn()) {
                this.constraints.get(constraint.getName()).toggle(false);
                this.modifiedElements.add(constraint);
            }
        });
        preferencesScalars.forEach(preference ->
        {
            ModelParameter scalar=this.preferenceToScalar.get(preference);
            scalar.setData(Float.toString(preference.getScalar()));
            this.modifiedElements.add(scalar);
        });
        //wrtie to file
        updateParser();
        ModifierVisitor modifier = new ModifierVisitor(this, currentSource);
        modifier.visit(tree);
        currentSource = modifier.getModifiedSource();
        /*try {
               *//* // Write the modified source back to file
                String modifiedSource = modifier.getModifiedSource();
                Files.write(Paths.get(sourceFilePath), modifiedSource.getBytes());
                parseSource();*//*
            }
            catch (IOException e) {
                throw new ParsingException("Error writing to source file: " + e.getMessage());
            }*/
        return currentSource;
    }
    @Override
    public String getSourceCode () {
        return originalSource;
    }
    @Deprecated(forRemoval = true)
    public void appendToSet(ModelSet set, String value) {
        // if (!sets.containsKey(setName)) {
        //     throw new IllegalArgumentException("Set " + setName + " not found");
        // }
        if(!set.isCompatible(value))
            throw new InvalidModelInputException("set "+set.getName()+" is incompatible with given input: "+value+" , expected type: "+set.getDataType());
        
        ModifierVisitor modifier = new ModifierVisitor(this, tokens, set.getName(), value, originalSource);
        modifier.visit(tree);
        
        if (modifier.isModified()) {
           /* try {
                // Write modified source back to file, preserving original formatting
                String modifiedSource = modifier.getModifiedSource();
                Files.write(Paths.get(sourceFilePath), modifiedSource.getBytes());
                parseSource();
            }
            catch (IOException e) {
                throw new ParsingException("Error writing to source file: " + e.getMessage());
            }
        */
        }
    }
    @Deprecated(forRemoval = true)
    public void removeFromSet(ModelSet set, String value) {
        // if (!sets.containsKey(setName)) {
        //     throw new IllegalArgumentException("Set " + setName + " not found");
        // }
        if(!set.isCompatible(value))
            throw new InvalidModelInputException("set "+set.getName()+" is incompatible with given input: "+value+" , expected type: "+set.getDataType());

        ModifierVisitor modifier = new ModifierVisitor(this, tokens, set.getName(), value, originalSource);
        modifier.visit(tree);
        
        if (modifier.isModified()) {
/*
            try {
                // Write modified source back to file, preserving original formatting
                String modifiedSource = modifier.getModifiedSource();
                Files.write(Paths.get(sourceFilePath), modifiedSource.getBytes());
                parseSource();
            } catch (IOException e) {
                throw new ParsingException("Error writing to source file: " + e.getMessage());
            }*/
        }
    }
    @Deprecated(forRemoval = true)
    public void setInput(ModelParameter parameter){

        if(!parameter.isCompatible(parameter.getData()))
            throw new InvalidModelInputException("parameter "+parameter.getName()+" is incompatible with given input: "+parameter.getData() +" expected type: "+parameter.getDataType());
        
        ModifierVisitor modifier = new ModifierVisitor(this, tokens, parameter.getName(), parameter.getData(), originalSource);
        modifier.visit(tree);
        
        if (modifier.isModified()) {
           /* try {
                // Write modified source back to file, preserving original formatting
                String modifiedSource = modifier.getModifiedSource();
                Files.write(Paths.get(sourceFilePath), modifiedSource.getBytes());
                parseSource();
            } catch (IOException e) {
                throw new ParsingException("Error writing to source file: " + e.getMessage());
            }*/
        }
    }
    @Override
    @Deprecated(forRemoval = true)
    public void setInput(ModelSet modelSet) {

        for(String str : modelSet.getData()){
            if(!modelSet.isCompatible(str))
                throw new InvalidModelInputException("set "+modelSet.getName()+" is incompatible with given input: "+str+" , expected type: "+modelSet.getDataType());

        }
        
        ModifierVisitor modifier = new ModifierVisitor(this, tokens, modelSet.getName(),
                modelSet.getData().toArray(new String[0]), originalSource);
        modifier.visit(tree);
        
        if (modifier.isModified()) {
            /*try {
                // Write the modified source back to file, preserving its original formatting
                String modifiedSource = modifier.getModifiedSource();
                Files.write(Paths.get(sourceFilePath), modifiedSource.getBytes());
                parseSource();
            } catch (IOException e) {
                throw new ParsingException("Error writing to source file: " + e.getMessage());
            }*/
        }
    }
    
    @Override
    @Deprecated(forRemoval = true) //real values are held directly in image
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
    @Deprecated(forRemoval = true) //real values are held directly in image
    public List<List<String>> getInput(ModelSet set) {
        if(set == null || set.getName() == null)
            throw new InvalidModelInputException("Trying to get input of a null set!");
        if(!set.isPrimitive())
            throw new InvalidModelInputException("Trying to get input of a non primitive set!");
        if(!sets.containsKey(set.getName()))
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

    public void toggleFunctionality(OperationalElement operationalElement, boolean toggle) {
        Optional.ofNullable(constraints.get(operationalElement.getName()))
                .ifPresentOrElse(
                        constraint -> constraint.toggle(toggle),
                        () -> {throw new IllegalArgumentException("Constraint not found: " + operationalElement.getName());}
        );

    }
    @Deprecated(forRemoval = true) //done in
    private void commentOutToggledFunctionalities() throws IOException {
        /*HashSet<String> toToggle = new HashSet<>();
        constraints.values().stream()
                .filter(Constraint::isOn)
                .forEach(constraint -> toToggle.add(constraint.getName()));

        ModifierVisitor modifier = new ModifierVisitor(this, tokens, null, "",originalSource);
        modifier.setTargetFunctionalities(toToggle); // Set functionalities to be commented out
        modifier.visit(tree);
        
        if (modifier.isModified()) {
            String modifiedSource = modifier.getModifiedSource();
            Files.write(Paths.get(sourceFilePath), modifiedSource.getBytes());
        //    parseSource();
        }*/
    }
    @Deprecated
    private void restoreToggledFunctionalities() throws IOException {
       /* if (toggledOffFunctionalities.isEmpty()) {
            return;
        }

        // Read the original file content and restore it
        Files.write(Paths.get(sourceFilePath), originalSource.getBytes());
        parseSource();*/
    }
    
    @Deprecated(forRemoval = true)
    //to be implement as a Kafka event
    public boolean isCompiling(float timeout) {
        /*boolean ans = false;
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
        }*/
        return false;
    }
    
    public Solution solve(float timeout, String solutionFileSuffix) {
        throw new InvalidModelInputException("Calling deprecated solve method in model.");
        /*if(solutionFileSuffix == null)
            throw new InvalidModelInputException("solutionFileSufix is null");
        Solution ans;
        try {
            commentOutToggledFunctionalities();

            Process process = this.getProcess();

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

    private Process getProcess () throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "scip", "-c", "read " + sourceFilePath + " optimize display solution q"
        );
        processBuilder.redirectErrorStream(true);

        try {
            return processBuilder.start();
        } catch (IOException e) {
            // Check if it's specifically a command not found error
            if (e.getMessage().contains("Cannot run program \"scip\"") ||
                    e.getMessage().contains("error=2")) {
                throw new EngineErrorException("SCIP solver not found. Please ensure SCIP is installed and available in system PATH");
            }
            throw e; // Rethrow other IOExceptions
        }*/
    }

    /*public List<FormulationParser.UExprContext> findComponentContexts(FormulationParser.NExprContext ctx) {
        List<FormulationParser.UExprContext> components = new ArrayList<>();
        findComponentContextsRecursive(ctx.uExpr(), components);
        return components;
    }

    private void findComponentContextsRecursive(FormulationParser.UExprContext ctx, List<FormulationParser.UExprContext> components) {
        if(ctx == null)
            return;
        if (components.isEmpty() && ctx.uExpr() != null && ctx.uExpr(1) != null) {
            String a = ctx.uExpr(1).getText();
            components.add(ctx.uExpr(1));
        } else if (components.isEmpty()){
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
    }*/
    public List<FormulationParser.UExprContext> findComponentContexts(FormulationParser.NExprContext ctx) {
        List<FormulationParser.UExprContext> components = new ArrayList<>();
        findComponentContextsRecursive(ctx.uExpr(), components);
        return components;
    }

    private void findComponentContextsRecursive(FormulationParser.UExprContext ctx, List<FormulationParser.UExprContext> components) {
        if (ctx == null) {
            return;
        }
        // Check if this is a + or - operation
        if (ctx.op != null && (ctx.op.getText().equals("+") || ctx.op.getText().equals("-"))) {
            // Process left side recursively
            findComponentContextsRecursive(ctx.uExpr(0), components);
            // Add right side as a component
            components.add(ctx.uExpr(1));
        } else if (components.isEmpty()) {
            // If this is not a + or - operation and we haven't added any components yet,
            // add the entire expression as one component
            components.add(ctx);
        }
    }



    public ModelSet getSet(String identifier) {
        return sets.get(identifier);
    }
    
    public ModelParameter getParameterFromAll(String identifier) {
        return Optional.ofNullable(params.get(identifier))
                .orElseThrow(() -> new InvalidModelInputException("Parameter " + identifier + " not found"));
    }
    public ModelParameter getParameter(String identifier) {
        if(!params.containsKey(identifier))
            return null; //don't like this, but this has to be here due to legacy code (max)
        ModelParameter param= params.get(identifier);
        if(param.isAuxiliary())
            throw new InvalidModelInputException("parameter "+ identifier +" not user defined, and can not be used in image.");
        return param;
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
    public boolean hasPreference(String identifier) {
        return preferences.containsKey(identifier);
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
        return this.sets.values().stream().filter(ModelSet::isPrimitive).toList();
    }
    
    @Override
    public Collection<ModelParameter> getParameters(){
        return this.params.values().stream().filter(
                param -> !param.isAuxiliary())
                .toList();
    }
    public Collection<ModelParameter> getAllParameters(){
        return this.params.values();
    }
    public boolean containsParameter(String paramName){
        return this.params.containsKey(paramName);
    }
    @Override
    public String modifySource() {
        return originalSource;
    }



    @Override
    public Collection<Variable> getVariables(Collection<String> identifiers){
        HashSet<Variable> set = new HashSet<>();
        for (String identifier : identifiers) {
            set.add(getVariable(identifier));
        }
        return set;
    }
    public boolean isModified(String name, Element.ElementType type) {
        return switch(type) {
            case MODEL_PARAMETER -> params.containsKey(name) && modifiedElements.contains(params.get(name));
            case MODEL_SET -> sets.containsKey(name) && modifiedElements.contains(sets.get(name));
            case CONSTRAINT -> constraints.containsKey(name) && modifiedElements.contains(constraints.get(name));
            case PREFERENCE -> preferences.containsKey(name) && modifiedElements.contains(preferences.get(name));
            case VARIABLE -> variables.containsKey(name) && modifiedElements.contains(variables.get(name));
        };
    }
    private void updateParser(){
        CharStream charStream = CharStreams.fromString(currentSource);
        FormulationLexer lexer = new FormulationLexer(charStream);
        tokens = new CommonTokenStream(lexer);
        FormulationParser parser = new FormulationParser(tokens);
        tree = parser.program();
    }

    public Map<String, ModelParameter> getParams() {
        return params;
    }

    public Set<Element> getModifiedElements() {
        return modifiedElements;
    }
    public boolean hasScalar(Preference preference){
        return preferenceToScalar.containsKey(preference);
    }
}