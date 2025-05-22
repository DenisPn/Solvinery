package Model.Parsing;

import Exceptions.InternalErrors.ModelExceptions.InvalidModelStateException;
import Model.Data.Elements.Element;
import Model.Data.Elements.Operational.Preference;
import Model.Model;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.Interval;
import parser.FormulationBaseVisitor;
import parser.FormulationLexer;
import parser.FormulationParser;

import java.util.*;

public class ModifierVisitor extends FormulationBaseVisitor<Void> {
    private final Model model;
   // private final CommonTokenStream tokens;
   /* private final String targetIdentifier; // For single-target operations
    private final Set<String> targetValues; // For single-target operations
    private Set<String> targetFunctionalities; // For multi-target operations*/
  //  private final Action act;
    private final String originalSource;
    private boolean modified = false;
    private final StringBuilder modifiedSource;
    private int shift = 0;
  /*  public enum Action {
        APPEND,
        DELETE,
        SET,
        COMMENT_OUT,
        UNCOMMENT
    }*/

    // Original constructor for backward compatibility
    public ModifierVisitor(Model model, String originalSource){
        this.model = model;
       // this.tokens = tokens;
        this.originalSource = originalSource;
        this.modifiedSource = new StringBuilder(originalSource);
    }
    @Deprecated
    public ModifierVisitor (Model model, CommonTokenStream tokens, String targetIdentifier, String value,/* Action act,*/ String originalSource) {
        this.model = model;
       // this.tokens = tokens;
        /*this.targetIdentifier = targetIdentifier;
        this.targetValues = new HashSet<>();
        this.targetValues.add(value);*/
   //     this.act = act;
        this.originalSource = originalSource;
        this.modifiedSource = new StringBuilder(originalSource);
    }
    @Deprecated
    public ModifierVisitor (Model model, CommonTokenStream tokens, String targetIdentifier, String[] values, /*Action act,*/ String originalSource) {
        this.model = model;
     //   this.tokens = tokens;
        /*this.targetIdentifier = targetIdentifier;
        this.targetValues = new HashSet<>(Arrays.asList(values));*/
      //  this.act = act;
        this.originalSource = originalSource;
        this.modifiedSource = new StringBuilder(originalSource);
    }

   /* // Method to set target functionalities for commenting out
    public void setTargetFunctionalities (Set<String> functionalities) {
        this.targetFunctionalities = functionalities;
    }*/

    private void modifyParamContent (FormulationParser.ExprContext ctx, String value) {
        // Get the original text with its formatting
        int startIndex = ctx.start.getStartIndex();
        int stopIndex = ctx.stop.getStopIndex();
        String originalLine = originalSource.substring(startIndex, stopIndex + 1);


        String modifiedLine = originalLine.replace(ctx.getText(),value);

        if (!originalLine.equals(modifiedLine)) {
            modifiedSource.replace(startIndex+shift, stopIndex + 1 + shift, value);
            modified = true;
            shift += modifiedLine.length() - originalLine.length();

        }
    }

    private void modifySetContent (FormulationParser.SetDefExprContext ctx,
                                   List<String> values
                                   /*FormulationParser.SetExprStackContext stackCtx*/) {
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
        String modifiedLine = modifySetLine(originalLine, values);
        if (!originalLine.equals(modifiedLine)) {
            modifiedSource.replace(startIndex + shift, stopIndex + 1 + shift, indentation + modifiedLine);
            modified = true;
            shift += modifiedLine.length() - originalLine.length();
        }
    }
    @Deprecated
    //not in use, no idea if works
    private void commentOutParameter (FormulationParser.ParamDeclContext ctx) {
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
    @Deprecated //don't need this?
    private void commentOutSet (FormulationParser.SetDefExprContext ctx) {
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
    public Void visitParamDecl (FormulationParser.ParamDeclContext ctx) {
        String paramName = extractName(ctx.sqRef().getText());
        if (model.isModified(paramName, Element.ElementType.MODEL_PARAMETER)) {
                modifyParamContent(ctx.expr(),model.getParameterFromAll(paramName).getData());
        }
        return super.visitParamDecl(ctx);
    }

    @Override
    public Void visitSetDefExpr (FormulationParser.SetDefExprContext ctx) {
        String setName = extractName(ctx.sqRef().getText());
        if (model.isModified(setName, Element.ElementType.MODEL_SET)) {
            if (ctx.setExpr() instanceof FormulationParser.SetExprStackContext stackCtx) {
                if (stackCtx.setDesc() instanceof FormulationParser.SetDescStackContext) {
                    modifySetContent(ctx,model.getSet(setName).getData());
                }
            }
        }
        return super.visitSetDefExpr(ctx);
    }

    @Override
    public Void visitConstraint (FormulationParser.ConstraintContext ctx) {
        String constraintName = extractName(ctx.name.getText());
        if (model.isModified(constraintName, Element.ElementType.CONSTRAINT)) {
                commentOutConstraint(ctx);
        }
        return super.visitConstraint(ctx);
    }

    @Override
    public Void visitObjective(FormulationParser.ObjectiveContext ctx) {
        List<FormulationParser.UExprContext> components = model.findComponentContexts(ctx.nExpr());
        for (FormulationParser.UExprContext subCtx : components) {
            String preferenceName = subCtx.start.getInputStream()
                    .getText(new Interval(subCtx.start.getStartIndex(),
                            subCtx.stop.getStopIndex()));
           // String preferenceName = subCtx.getText();
            if (!model.hasPreference(preferenceName)) {
                Preference preference = model.getPreferences().stream()
                        .filter(pref -> pref.getName().contains(preferenceName))
                        .findFirst()
                        .orElse(null);
                Objects.requireNonNull(preference, "Invalid Preference call in model:\n" + preferenceName);
                replacePreference(subCtx,preference.getName());
            }
        }
        return super.visitObjective(ctx);
    }

    private String modifySetLine (String line, List<String> values) {
        // Find the set content between braces
        int openBrace = line.indexOf('{');
        int closeBrace = line.lastIndexOf('}');
        String separator= ",";
        if (openBrace != -1 && closeBrace != -1 && closeBrace > openBrace) {
            String beforeBraces = line.substring(0, openBrace + 1);
            String afterBraces = line.substring(closeBrace);
            String content = "";
           /* if (this.act == Action.APPEND || this.act == Action.DELETE)
                content = line.substring(openBrace + 1, closeBrace).trim();*/
            for (String val : values) {
              //  if (isAppend) {
                    // Add value
                    content = content.isEmpty() ? val : content + ", " + val;
                /*} else {
                    // Remove value
                    content = Arrays.stream(content.split(","))
                            .map(String::trim)
                            .filter(s -> !s.equals(val))
                            .collect(Collectors.joining(", "));
                }*/
            }
            return beforeBraces + content + afterBraces;
        }
        else {
            throw new InvalidModelStateException("Invalid or unmodifiable set: " + line + ". Modifiable sets must contain braces {} to indicate set contents.");
        }
    }

    private void commentOutConstraint (FormulationParser.ConstraintContext ctx) {
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
                commentedOut.append(initialIndent).append("# ").append(line.substring(0, line.length() - 1)).append("\n");
            } else {
                commentedOut.append(initialIndent).append("# ").append(line);
            }
        }

        modifiedSource.replace(startIndex + shift, stopIndex + 1 + shift, commentedOut.toString());
        shift += commentedOut.length() - fullStatement.length();
        modified = true;
    }
    private void replacePreference(FormulationParser.UExprContext ctx, String newPreference) {
        int startIndex = ctx.start.getStartIndex();
        int stopIndex = ctx.stop.getStopIndex();
        String existingPreference= ctx.getText();
        modifiedSource.replace(startIndex + shift, stopIndex + 1 + shift, newPreference);
        shift += newPreference.length() - existingPreference.length();

    }
    @Deprecated
    private void commentOutPreference (FormulationParser.UExprContext ctx) {
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
    @Deprecated //should not be used, preference scalars set through params
    private void zeroOutPreference (FormulationParser.UExprContext ctx) {
        int startIndex = ctx.start.getStartIndex();
        int stopIndex = ctx.stop.getStopIndex();
        String originalLine = originalSource.substring(startIndex, stopIndex + 1);

        modifiedSource.replace(startIndex, stopIndex + 1,
                "((" + originalLine + ")*0)");
        modified = true;
    }


    private String extractName (String sqRef) {
        int bracketIndex = sqRef.indexOf('[');
        return bracketIndex == -1 ? sqRef : sqRef.substring(0, bracketIndex);
    }

    public boolean isModified () {
        return modified;
    }

    public String getModifiedSource () {
        return modifiedSource.toString();
    }

    /**
     * Removes all whitespace characters from a string.
     * @param str The string to be modified.
     * @return The modified string with all whitespace characters removed.
     */
    private static String removeWhitespaces(String str){
        return str.replaceAll("\\s+","");
    }
}
