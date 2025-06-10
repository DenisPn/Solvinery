package Model;

import Exceptions.InternalErrors.ModelExceptions.InvalidModelInputException;
import Exceptions.InternalErrors.ModelExceptions.InvalidModelStateException;
import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Element;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;
import Model.Data.Types.ModelPrimitives;
import Model.Parsing.CollectorVisitor;
import Model.Parsing.ModifierVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.Nullable;
import org.springframework.lang.NonNull;
import parser.FormulationLexer;
import parser.FormulationParser;

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
    private final Map<String, Preference> modifiedPreferences = new HashMap<>();
    private final Map<String, Preference> originalToModifiedDereferences = new HashMap<>();
    private final Map<String, Variable> variables = new HashMap<>();

    private final Set<String> uneditedPreferences = new HashSet<>();
    private final Map<Preference,ModelParameter> preferenceToScalar = new HashMap<>();
    private final Set<Element> modifiedElements= new HashSet<>();


    @NonNull
    public Map<String,ModelSet> getSetsMap(){
        return sets;
    }
    @NonNull
    public Map<String,ModelParameter> getParamsMap(){
        return params;
    }
    @NonNull
    public Map<String,Constraint> getConstraintsMap(){
        return constraints;
    }

    @NonNull
    public Map<String,Preference> getPreferencesMap(){
        return modifiedPreferences;
    }
    @NonNull
    public Set<String> getUneditedPreferences(){
        return uneditedPreferences;
    }
    @NonNull
    public Map<String,Variable> getVariablesMap(){
        return variables;
    }

    public ParseTree getTree () {
        return tree;
    }

    public CommonTokenStream getTokens () {
        return tokens;
    }

    @NonNull
    public String getOriginalSource () {
        return originalSource;
    }

    private final @NonNull String originalSource;
    private String currentSource;

    public Model(@NonNull String sourceCode) {
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
            //throw new InvalidModelStateException("Model has already been parsed and processed");
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
            /*try {
                preference.setScalar(Float.parseFloat(scalarParam.getData()));
            } catch (NumberFormatException e) {
                throw new InvalidModelInputException("Invalid scalar parameter value while parsing: "+e.getMessage());
            }*/
            preferenceToScalar.put(preference,scalarParam);
            modifiedPreferences.put(preference.getName(),preference);
            originalToModifiedDereferences.put(preferenceBody,preference);
        }
    }
    @NonNull
    private String extractScalarParam(@NonNull String preferenceBody){
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
            modifiedPreferences.put(newBody,editedPreference);
            ModelParameter preferenceScalar=new ModelParameter(paramName, ModelPrimitives.FLOAT,"1",true);
            params.put(paramName,preferenceScalar);
            preferenceToScalar.put(editedPreference,preferenceScalar);
            originalToModifiedDereferences.put(body,editedPreference);
        }
    }

    @NonNull
    public static String hashPreference(@NonNull String input) {
        return "scalar"+ Math.abs(input.hashCode());
    }

    @Override
    public String writeToSource(@NonNull Set<ModelSet> sets, @NonNull Set<ModelParameter> params, @NonNull Set<Constraint> disabledConstraints, @NonNull Map<String,Float> preferencesScalars) {
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

        this.modifiedElements.addAll(disabledConstraints);

        preferencesScalars.keySet().forEach(preference ->
        {
            ModelParameter scalar;
            if(modifiedPreferences.containsKey(preference))
                scalar = preferenceToScalar.get(modifiedPreferences.get(preference));
            else scalar=this.preferenceToScalar.get(originalToModifiedDereferences.get(preference));
            if(scalar == null)
                throw new InvalidModelStateException("Preference "+preference+" does not have a corresponding scalar parameter");
            scalar.setData(Float.toString(preferencesScalars.get(preference)));
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
    @NonNull
    @Override
    public String getSourceCode () {
        return originalSource;
    }


    @NonNull
    public List<FormulationParser.UExprContext> findComponentContexts(@NonNull FormulationParser.NExprContext ctx) {
        List<FormulationParser.UExprContext> components = new ArrayList<>();
        findComponentContextsRecursive(ctx.uExpr(), components);
        return components;
    }

    private void findComponentContextsRecursive(FormulationParser.@Nullable UExprContext ctx, @NonNull List<FormulationParser.UExprContext> components) {
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


    public ModelParameter getScalarParam(String preferenceName){
        return preferenceToScalar.get(originalToModifiedDereferences.get(preferenceName));
    }
    public ModelParameter getScalarParam(Preference preference){
        return preferenceToScalar.get(preference);
    }
    @NonNull
    public Float getScalarValue(@NonNull Preference preference){
        try {
            return Float.valueOf(getScalarParam(preference).getData());
        }
        catch (NumberFormatException e){
            throw new InvalidModelInputException("Preference "+preference.getName()+" does not have a scalar value");
        }
    }
    @NonNull
    public Float getScalarValue(String preferenceName){
        try {
            return Float.valueOf(getScalarParam(preferenceName).getData());
        }
        catch (NumberFormatException e){
            throw new InvalidModelInputException("Preference "+preferenceName+" does not have a scalar value");
        }
    }
    public ModelSet getSet(String identifier) {
        return sets.get(identifier);
    }
    
    public ModelParameter getParameterFromAll(String identifier) {
        return Optional.ofNullable(params.get(identifier))
                .orElseThrow(() -> new InvalidModelInputException("Parameter " + identifier + " not found"));
    }
    public @Nullable ModelParameter getParameter(String identifier) {
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

    @NonNull
    @Override
    public Collection<Constraint> getConstraints() {
        return constraints.values();
    }

    public Preference getPreference(String identifier) {
        return modifiedPreferences.get(identifier);
    }

    /**
     * For use in testing only.
     */
    public @Nullable String getOriginalBody(@NonNull Preference preference){
        for(String original: originalToModifiedDereferences.keySet()){
            if(originalToModifiedDereferences.get(original).getName().equals(preference.getName())){
                return original;
            }
        }
        return null;
    }
    public boolean hasPreference(String identifier) {
        return modifiedPreferences.containsKey(identifier);
    }

    @NonNull
    @Override
    public Collection<Preference> getModifiedPreferences() {
        return modifiedPreferences.values();
    }

    public Variable getVariable(String identifier) {
        return variables.get(identifier);
    }

    @NonNull
    @Override
    public Collection<Variable> getVariables() {
        return variables.values();
    }
    @NonNull
    @Override
    public Collection<ModelSet> getSets(){
        return this.sets.values().stream().filter(ModelSet::isPrimitive).toList();
    }
    
    @NonNull
    @Override
    public Collection<ModelParameter> getParameters(){
        return this.params.values().stream().filter(
                param -> !param.isAuxiliary())
                .toList();
    }
    @NonNull
    public Collection<ModelParameter> getAllParameters(){
        return this.params.values();
    }
    public boolean containsParameter(String paramName){
        return this.params.containsKey(paramName);
    }


    @NonNull
    @Override
    public Collection<Variable> getVariables(@NonNull Collection<String> identifiers){
        HashSet<Variable> set = new HashSet<>();
        for (String identifier : identifiers) {
            set.add(getVariable(identifier));
        }
        return set;
    }
    public boolean isModified(String name, @NonNull Element.ElementType type) {
        return switch(type) {
            case MODEL_PARAMETER -> params.containsKey(name) && modifiedElements.contains(params.get(name));
            case MODEL_SET -> sets.containsKey(name) && modifiedElements.contains(sets.get(name));
            case CONSTRAINT -> constraints.containsKey(name) && modifiedElements.contains(constraints.get(name));
            case PREFERENCE -> modifiedPreferences.containsKey(name) && modifiedElements.contains(modifiedPreferences.get(name));
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

    @NonNull
    public Map<String, ModelParameter> getParams() {
        return params;
    }

    @NonNull
    public Set<Element> getModifiedElements() {
        return modifiedElements;
    }
    public boolean hasScalar(Preference preference){
        return preferenceToScalar.containsKey(preference);
    }
}