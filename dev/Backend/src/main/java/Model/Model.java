package Model;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import DTO.Solution;
import parser.*;
import parser.FormulationBaseVisitor;
import parser.FormulationLexer;
import parser.FormulationParser;
import parser.FormulationParser.ExprContext;
import parser.FormulationParser.ParamDeclContext;
import parser.FormulationParser.SetDeclContext;
import parser.FormulationParser.SetDefExprContext;
import parser.FormulationParser.SetDescStackContext;
import parser.FormulationParser.SetExprContext;
import parser.FormulationParser.SetExprStackContext;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.TreeWizard;

public class Model {
    private final String sourceFilePath;
    private ParseTree tree;
    private CommonTokenStream tokens;
    private final Set<ModelSet> sets = new HashSet<>();
    private final Set<ModelParameter> params = new HashSet<>();
    private final Set<ModelConstraint> constraints = new HashSet<>();
    private final Set<ModelPreference> preferences = new HashSet<>();
    private final Set<ModelVariable> variables = new HashSet<>();
    private final Set<String> toggledOffFunctionalities = new HashSet<>();
    private boolean loadElementsToRam = true;
    private final String zimplCompilationScript = "/Plan-A/dev/Backend/src/main/resources/zimpl/checkCompilation.sh";
    private final String zimplSolveScript = "/Plan-A/dev/Backend/src/main/resources/zimpl/solve.sh" ;
    private String originalSource;
    
    public Model(String sourceFilePath) throws IOException {
        this.sourceFilePath = sourceFilePath;
        parseSource();
    }
    
    private void parseSource() throws IOException {
        originalSource = new String(Files.readAllBytes(Paths.get(sourceFilePath)));
        CharStream charStream = CharStreams.fromString(originalSource);
        FormulationLexer lexer = new FormulationLexer(charStream);
        tokens = new CommonTokenStream(lexer);
        FormulationParser parser = new FormulationParser(tokens);
        tree = parser.program();
        
        // Initial parse to collect all declarations
        CollectorVisitor collector = new CollectorVisitor();
        collector.visit(tree);
    }
    
    public void appendToSet(ModelSet set, String value) throws Exception {
        // if (!sets.containsKey(setName)) {
        //     throw new IllegalArgumentException("Set " + setName + " not found");
        // }
        if(!set.isCompatible(value))
            throw new Exception("Incompatible types!");
        ModifierVisitor modifier = new ModifierVisitor(tokens, set.getIdentifier(), value, ModifierVisitor.Action.APPEND, originalSource);
        modifier.visit(tree);
        
        if (modifier.isModified()) {
            // Write modified source back to file, preserving original formatting
            String modifiedSource = modifier.getModifiedSource();
            Files.write(Paths.get(sourceFilePath), modifiedSource.getBytes());
            parseSource();
        }
    }
    
    public void removeFromSet(ModelSet set, String value) throws Exception {
        // if (!sets.containsKey(setName)) {
        //     throw new IllegalArgumentException("Set " + setName + " not found");
        // }
        if(!set.isCompatible(value))
            throw new Exception("Incompatible types!");
        ModifierVisitor modifier = new ModifierVisitor(tokens, set.getIdentifier(), value,  ModifierVisitor.Action.DELETE, originalSource);
        modifier.visit(tree);
        
        if (modifier.isModified()) {
            // Write modified source back to file, preserving original formatting
            String modifiedSource = modifier.getModifiedSource();
            Files.write(Paths.get(sourceFilePath), modifiedSource.getBytes());
            parseSource();
        }
    }

    public void setInput(ModelInput identifier, String value) throws Exception {

        if(!identifier.isCompatible(value))
            throw new Exception("incompatible type");
        if(identifier instanceof ModelSet)
            throw new Exception("unimplemented for sets yet!");
        
        ModifierVisitor modifier = new ModifierVisitor(tokens, identifier.getIdentifier(), value,  ModifierVisitor.Action.SET, originalSource);
        modifier.visit(tree);
        
        if (modifier.isModified()) {
            // Write modified source back to file, preserving original formatting
            String modifiedSource = modifier.getModifiedSource();
            Files.write(Paths.get(sourceFilePath), modifiedSource.getBytes());
            parseSource();
        }
    }
   
    public void toggleFunctionality(ModelFunctionality mf, boolean turnOn) {
        if (!turnOn) {
            toggledOffFunctionalities.add(mf.getIdentifier());
        } else {
            toggledOffFunctionalities.remove(mf.getIdentifier());
        }
    }

    private void commentOutToggledFunctionalities() throws IOException {
        if (toggledOffFunctionalities.isEmpty()) {
            return;
        }

        ModifierVisitor modifier = new ModifierVisitor(tokens, null, null, ModifierVisitor.Action.COMMENT_OUT, originalSource);
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
        try {
            commentOutToggledFunctionalities();
            
            String[] command = {"/bin/bash", zimplCompilationScript, sourceFilePath, String.valueOf(timeout)};
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            boolean result = exitCode == 0 && output.toString().contains("Compilation Successful");
            
            restoreToggledFunctionalities();
            return result;
        } catch (Exception e) {
            try {
                restoreToggledFunctionalities();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    public Solution solve(float timeout) {
        try {
            commentOutToggledFunctionalities();
            
            String[] command = {"/bin/bash", zimplSolveScript, sourceFilePath, String.valueOf(timeout)};
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            Solution result = exitCode == 0 ? new Solution(sourceFilePath+"SOLUTION") : null;
            
            restoreToggledFunctionalities();
            return result;
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

    private class CollectorVisitor extends FormulationBaseVisitor<Void> {


        public Void visitParamDecl(FormulationParser.ParamDeclContext ctx){
            String paramName = extractName(ctx.sqRef().getText());
            TypeVisitor typer = new TypeVisitor();
            typer.visit(ctx.expr());
            ModelParameter param = new ModelParameter(paramName,typer.getType());
            if(loadElementsToRam){
                param.setValue(ctx.expr().getText());
            }
            params.add(param);
            return super.visitParamDecl(ctx);
        }

        @Override
        public Void visitSetDecl(FormulationParser.SetDeclContext ctx) {
            String setName = extractName(ctx.sqRef().getText());
            
            sets.add(new ModelSet(setName,ModelPrimitives.UNKNOWN));
            return super.visitSetDecl(ctx);
        }
        
        @Override
        public Void visitSetDefExpr(FormulationParser.SetDefExprContext ctx) {
            String setName = extractName(ctx.sqRef().getText());
            
            TypeVisitor typer = new TypeVisitor();
            typer.visit(ctx.setExpr());
            ModelSet set = new ModelSet(setName,typer.getType());
            if(loadElementsToRam){
                java.util.List<String> elements = parseSetElements(ctx.setExpr());
                set.setElements(elements);
            }
            sets.add(set);
            return super.visitSetDefExpr(ctx);
        }

        @Override
        public Void visitConstraint(FormulationParser.ConstraintContext ctx) {
            String constName = extractName(ctx.name.getText());
            constraints.add(new ModelConstraint(constName));
            return super.visitConstraint(ctx);
        }

        @Override
        public Void visitObjective(FormulationParser.ObjectiveContext ctx) {
            //String constName = extractName(ctx.name.getText());
            //preferences.add(new ModelConstraint(constName,));
            //return super.visitSetDefExpr(ctx);
            return super.visitObjective(ctx);
        }
        
        public Void visitVariable(FormulationParser.VariableContext ctx){
            String varName = extractName(ctx.sqRef().getText());
            variables.add(new ModelVariable(varName));
            return super.visitVariable(ctx);
        }

        private String extractName(String sqRef) {
            // Handle indexed sets by taking the base name
            int bracketIndex = sqRef.indexOf('[');
            return bracketIndex == -1 ? sqRef : sqRef.substring(0, bracketIndex);
        }
        
        private java.util.List<String> parseSetElements(FormulationParser.SetExprContext ctx) {
            java.util.List<String> elements = new ArrayList<>();
            // Parse set elements based on the context type
            
            if (ctx instanceof FormulationParser.SetExprStackContext) {
                FormulationParser.SetExprStackContext stackCtx = (FormulationParser.SetExprStackContext) ctx;
                if (stackCtx.setDesc() != null) {
                    // Handle explicit set descriptions
                    if (stackCtx.setDesc() instanceof FormulationParser.SetDescStackContext) {
                        FormulationParser.SetDescStackContext descCtx = (FormulationParser.SetDescStackContext) stackCtx.setDesc();
                        if (descCtx.csv() != null) {
                            String[] values = descCtx.csv().getText().split(",");
                            elements.addAll(Arrays.asList(values));
                        }
                    }
                }
            }
            return elements;
        }
    }
    
    private class ModifierVisitor extends FormulationBaseVisitor<Void> {
        private final CommonTokenStream tokens;
        private String targetIdentifier; // For single-target operations
        private String targetValue; // For single-target operations
        private Set<String> targetFunctionalities; // For multi-target operations
        private final Action act;
        private final String originalSource;
        private boolean modified = false;
        private StringBuilder modifiedSource;

        enum Action {
            APPEND,
            DELETE,
            SET,
            COMMENT_OUT,
            UNCOMMENT
        }

        // Original constructor for backward compatibility
        public ModifierVisitor(CommonTokenStream tokens, String targetIdentifier, String value, Action act, String originalSource) {
            this.tokens = tokens;
            this.targetIdentifier = targetIdentifier;
            this.targetValue = value;
            this.act = act;
            this.originalSource = originalSource;
            this.modifiedSource = new StringBuilder(originalSource);
        }

        // Method to set target functionalities for commenting out
        public void setTargetFunctionalities(Set<String> functionalities) {
            this.targetFunctionalities = functionalities;
        }

        private void modifyParamContent(FormulationParser.ExprContext ctx) {
            // Get the original text with its formatting
            int startIndex = ctx.start.getStartIndex();
            int stopIndex = ctx.stop.getStopIndex();
            String originalLine = originalSource.substring(startIndex, stopIndex + 1);
            
            // Preserve indentation
            String indentation = "";
            int lineStart = originalSource.lastIndexOf('\n', startIndex);
            if (lineStart != -1) {
                indentation = originalSource.substring(lineStart + 1, startIndex);
            }

            // Modify the set content while preserving formatting
            String modifiedLine = originalLine.replaceFirst(ctx.getText(), targetValue);
            
            if (!originalLine.equals(modifiedLine)) {
                modifiedSource.replace(startIndex, stopIndex + 1, modifiedLine);
                modified = true;
            }
        }

        private void modifySetContent(FormulationParser.SetDefExprContext ctx, 
                                    FormulationParser.SetExprStackContext stackCtx) {
            // Get the original text with its formatting
            int startIndex = ctx.start.getStartIndex();
            int stopIndex = ctx.stop.getStopIndex();
            String originalLine = originalSource.substring(startIndex, stopIndex + 1);
            
            // Preserve indentation
            String indentation = "";
            int lineStart = originalSource.lastIndexOf('\n', startIndex);
            if (lineStart != -1) {
                indentation = originalSource.substring(lineStart + 1, startIndex);
            }
            String modifiedLine = originalLine;
            // Modify the set content while preserving formatting
            if(act == Action.APPEND)
                modifiedLine = modifySetLine(originalLine, targetValue, true);
            else if (act == Action.DELETE)
                modifiedLine = modifySetLine(originalLine, targetValue, false);
            else
                System.out.println("ERROR - shouldnt reach this line (Model.java - modifySetContent(...))");

            if (!originalLine.equals(modifiedLine)) {
                modifiedSource.replace(startIndex, stopIndex + 1, indentation + modifiedLine);
                modified = true;
            }
        }
        
        private void commentOutParameter(FormulationParser.ParamDeclContext ctx) {
            // Get the original text with its formatting
            int startIndex = ctx.start.getStartIndex();
            int stopIndex = ctx.stop.getStopIndex();
            String originalLine = originalSource.substring(startIndex, stopIndex + 1);
            
            // Preserve indentation
            String indentation = "";
            int lineStart = originalSource.lastIndexOf('\n', startIndex);
            if (lineStart != -1) {
                indentation = originalSource.substring(lineStart + 1, startIndex);
            }
            
            // Add comment marker while preserving indentation
            modifiedSource.replace(startIndex, stopIndex + 1, 
                indentation + "# " + originalLine.substring(indentation.length()));
            modified = true;
        }
        
        private void commentOutSet(FormulationParser.SetDefExprContext ctx) {
            // Get the original text with its formatting
            int startIndex = ctx.start.getStartIndex();
            int stopIndex = ctx.stop.getStopIndex();
            String originalLine = originalSource.substring(startIndex, stopIndex + 1);
            
            // Preserve indentation
            String indentation = "";
            int lineStart = originalSource.lastIndexOf('\n', startIndex);
            if (lineStart != -1) {
                indentation = originalSource.substring(lineStart + 1, startIndex);
            }
            
            // Add comment marker while preserving indentation
            modifiedSource.replace(startIndex, stopIndex + 1, 
                indentation + "# " + originalLine.substring(indentation.length()));
            modified = true;
        }

        @Override
        public Void visitParamDecl(FormulationParser.ParamDeclContext ctx) {
            String paramName = extractName(ctx.sqRef().getText());
            if (paramName.equals(targetIdentifier)) {
                if(act == Action.SET)
                    modifyParamContent(ctx.expr());
                else if (act == Action.COMMENT_OUT)
                    commentOutParameter(ctx);
            }
            return super.visitParamDecl(ctx);
        }

        @Override
        public Void visitSetDefExpr(FormulationParser.SetDefExprContext ctx) {
            String setName = extractName(ctx.sqRef().getText());
            if (setName.equals(targetIdentifier)) {
                if (act == Action.COMMENT_OUT)
                    commentOutSet(ctx);
                else if (ctx.setExpr() instanceof FormulationParser.SetExprStackContext) {
                    FormulationParser.SetExprStackContext stackCtx = 
                        (FormulationParser.SetExprStackContext) ctx.setExpr();
                    if (stackCtx.setDesc() instanceof FormulationParser.SetDescStackContext) {
                        modifySetContent(ctx, stackCtx);
                    }
                }
            }
            return super.visitSetDefExpr(ctx);
        }

        @Override
        public Void visitConstraint(FormulationParser.ConstraintContext ctx) {
            String constraintName = extractName(ctx.name.getText());
            if ((targetFunctionalities != null && targetFunctionalities.contains(constraintName)) ||
                (targetIdentifier != null && constraintName.equals(targetIdentifier))) {
                if (act == Action.COMMENT_OUT)
                    commentOutConstraint(ctx);
            }
            return super.visitConstraint(ctx);
        }

        @Override
        public Void visitObjective(FormulationParser.ObjectiveContext ctx) {
            if (ctx.name != null) {
                String objectiveName = extractName(ctx.name.getText());
                if ((targetFunctionalities != null && targetFunctionalities.contains(objectiveName)) ||
                    (targetIdentifier != null && objectiveName.equals(targetIdentifier))) {
                    if (act == Action.COMMENT_OUT)
                        commentOutPreference(ctx);
                }
            }
            return super.visitObjective(ctx);
        }

        // ... keep all existing helper methods (modifyParamContent, commentOutParameter, etc.) ...

        private String modifySetLine(String line, String value, boolean isAppend) {
            // Find the set content between braces
            int openBrace = line.indexOf('{');
            int closeBrace = line.lastIndexOf('}');
            
            if (openBrace != -1 && closeBrace != -1) {
                String beforeBraces = line.substring(0, openBrace + 1);
                String afterBraces = line.substring(closeBrace);
                String content = line.substring(openBrace + 1, closeBrace).trim();
                
                if (isAppend) {
                    // Add value
                    content = content.isEmpty() ? value : content + ", " + value;
                } else {
                    // Remove value
                    content = Arrays.stream(content.split(","))
                                  .map(String::trim)
                                  .filter(s -> !s.equals(value))
                                  .collect(Collectors.joining(", "));
                }
                
                return beforeBraces + content + afterBraces;
            }
            return line;
        }

        private void commentOutConstraint(FormulationParser.ConstraintContext ctx) {
            int startIndex = ctx.start.getStartIndex();
            int stopIndex = ctx.stop.getStopIndex();
            String fullStatement = originalSource.substring(startIndex, stopIndex + 1);
            
            // Split into lines while preserving the original line endings
            String[] lines = fullStatement.split("(?<=\n)");
            StringBuilder commentedOut = new StringBuilder();
            
            // Get the initial indentation from the first line
            String initialIndent = "";
            int lineStart = originalSource.lastIndexOf('\n', startIndex);
            if (lineStart != -1) {
                initialIndent = originalSource.substring(lineStart + 1, startIndex);
            }
            
            // Comment out each line while preserving its relative indentation
            for (String line : lines) {
                // If it's not the last line (which won't have a newline)
                if (line.endsWith("\n")) {
                    commentedOut.append(initialIndent).append("# ").append(line.substring(0, line.length()-1)).append("\n");
                } else {
                    commentedOut.append(initialIndent).append("# ").append(line);
                }
            }
            
            modifiedSource.replace(startIndex, stopIndex + 1, commentedOut.toString());
            modified = true;
        }

        private void commentOutPreference(FormulationParser.ObjectiveContext ctx) {
            int startIndex = ctx.start.getStartIndex();
            int stopIndex = ctx.stop.getStopIndex();
            String originalLine = originalSource.substring(startIndex, stopIndex + 1);
            
            String indentation = "";
            int lineStart = originalSource.lastIndexOf('\n', startIndex);
            if (lineStart != -1) {
                indentation = originalSource.substring(lineStart + 1, startIndex);
            }
            
            modifiedSource.replace(startIndex, stopIndex + 1, 
                indentation + "# " + originalLine.substring(indentation.length()));
            modified = true;
        }

        private String extractName(String sqRef) {
            int bracketIndex = sqRef.indexOf('[');
            return bracketIndex == -1 ? sqRef : sqRef.substring(0, bracketIndex);
        }

        public boolean isModified() {
            return modified;
        }

        public String getModifiedSource() {
            return modifiedSource.toString();
        }
    }
    private class TypeVisitor extends FormulationBaseVisitor<Void> {
        ModelType type = ModelPrimitives.UNKNOWN;

        public ModelType getType(){
            return type;
        }

        public Void visitStrExprToken(FormulationParser.StrExprTokenContext ctx){
            if(type == ModelPrimitives.UNKNOWN)
                type = ModelPrimitives.TEXT;
            else if( type instanceof Tuple){
                ((Tuple)type).append(ModelPrimitives.TEXT);
            } else {
                //nothing
            }
            return super.visitStrExprToken(ctx);
        }

        public Void visitBasicExprToken(FormulationParser.BasicExprTokenContext ctx){
            ModelPrimitives tmp = ModelPrimitives.UNKNOWN;
            if(ctx.FLOAT() != null){
                tmp = ModelPrimitives.FLOAT;
            } else if(ctx.INFINITY() != null){
                tmp = ModelPrimitives.INFINITY;
            } else if(ctx.INT() != null){
                tmp = ModelPrimitives.INT;
            }
            if(type == ModelPrimitives.UNKNOWN){
                type = tmp;
            } else if ( type instanceof Tuple){
                ((Tuple) type).append(tmp);
            } else {

            }
            return super.visitBasicExprToken(ctx);
        }
        public Void visitTuple(FormulationParser.TupleContext ctx){
            if(type == ModelPrimitives.UNKNOWN){
                type = new Tuple();
            } else if (type instanceof Tuple) {

            }
            return super.visitTuple(ctx);
        }
    }

    public Set<ModelSet> getSets() {
        return Collections.unmodifiableSet(sets);
    }
    
    public Set<ModelParameter> getParameters() {
        return Collections.unmodifiableSet(params);
    }
    
    public Set<ModelConstraint> getConstraints() {
        return Collections.unmodifiableSet(constraints);
    }
    
    public Set<ModelPreference> getPreferences() {
        return Collections.unmodifiableSet(preferences);
    }
    
    public Set<ModelVariable> getVariables() {
        return Collections.unmodifiableSet(variables);
    }
    
    // Getters for individual elements by identifier
    public ModelSet getSet(String identifier) {
        return sets.stream()
            .filter(set -> set.getIdentifier().equals(identifier))
            .findFirst()
            .orElse(null);
    }
    
    public ModelParameter getParameter(String identifier) {
        return params.stream()
            .filter(param -> param.getIdentifier().equals(identifier))
            .findFirst()
            .orElse(null);
    }
    
    public ModelConstraint getConstraint(String identifier) {
        return constraints.stream()
            .filter(constraint -> constraint.getIdentifier().equals(identifier))
            .findFirst()
            .orElse(null);
    }
    
    public ModelPreference getPreference(String identifier) {
        return preferences.stream()
            .filter(preference -> preference.getIdentifier().equals(identifier))
            .findFirst()
            .orElse(null);
    }
    
    public ModelVariable getVariable(String identifier) {
        return variables.stream()
            .filter(variable -> variable.getIdentifier().equals(identifier))
            .findFirst()
            .orElse(null);
    }
    
}