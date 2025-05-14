package Model.Parsing;

import Exceptions.InternalErrors.ModelExceptions.InvalidModelStateException;
import Model.Model;
import org.antlr.v4.runtime.CommonTokenStream;
import parser.FormulationBaseVisitor;
import parser.FormulationParser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModifierVisitor extends FormulationBaseVisitor<Void> {
    private final Model model;
    private final CommonTokenStream tokens;
    private final String targetIdentifier; // For single-target operations
    private final Set<String> targetValues; // For single-target operations
    private Set<String> targetFunctionalities; // For multi-target operations
    private final Action act;
    private final String originalSource;
    private boolean modified = false;
    private final StringBuilder modifiedSource;

    public enum Action {
        APPEND,
        DELETE,
        SET,
        COMMENT_OUT,
        UNCOMMENT
    }

    // Original constructor for backward compatibility
    public ModifierVisitor (Model model, CommonTokenStream tokens, String targetIdentifier, String value, Action act, String originalSource) {
        this.model = model;
        this.tokens = tokens;
        this.targetIdentifier = targetIdentifier;
        this.targetValues = new HashSet<>();
        this.targetValues.add(value);
        this.act = act;
        this.originalSource = originalSource;
        this.modifiedSource = new StringBuilder(originalSource);
    }

    public ModifierVisitor (Model model, CommonTokenStream tokens, String targetIdentifier, String[] values, Action act, String originalSource) {
        this.model = model;
        this.tokens = tokens;
        this.targetIdentifier = targetIdentifier;
        this.targetValues = new HashSet<>(Arrays.asList(values));
        this.act = act;
        this.originalSource = originalSource;
        this.modifiedSource = new StringBuilder(originalSource);
    }

    // Method to set target functionalities for commenting out
    public void setTargetFunctionalities (Set<String> functionalities) {
        this.targetFunctionalities = functionalities;
    }

    private void modifyParamContent (FormulationParser.ExprContext ctx) {
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
        // targetValues is expected to have at exactly one element
        String modifiedLine = originalLine.replaceFirst(ctx.getText(),
                targetValues.stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new InvalidModelStateException("Target value is null in modifyParamContent method.\n" +
                                "This should never happen and is a bug.")));

        if (!originalLine.equals(modifiedLine)) {
            modifiedSource.replace(startIndex, stopIndex + 1, modifiedLine);
            modified = true;
        }
    }

    private void modifySetContent (FormulationParser.SetDefExprContext ctx,
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
        if (act == Action.APPEND)
            modifiedLine = modifySetLine(originalLine, targetValues, true);
        else if (act == Action.DELETE)
            modifiedLine = modifySetLine(originalLine, targetValues, false);
        else if (act == Action.SET)
            modifiedLine = modifySetLine(originalLine, targetValues, true);
        else
            System.out.println("ERROR - shouldnt reach this line (Model.java - modifySetContent(...))");

        if (!originalLine.equals(modifiedLine)) {
            modifiedSource.replace(startIndex, stopIndex + 1, indentation + modifiedLine);
            modified = true;
        }
    }

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
        if (paramName.equals(targetIdentifier)) {
            if (act == Action.SET)
                modifyParamContent(ctx.expr());
            else if (act == Action.COMMENT_OUT)
                commentOutParameter(ctx);
        }
        return super.visitParamDecl(ctx);
    }

    @Override
    public Void visitSetDefExpr (FormulationParser.SetDefExprContext ctx) {
        String setName = extractName(ctx.sqRef().getText());
        if (setName.equals(targetIdentifier)) {
            if (act == Action.COMMENT_OUT)
                commentOutSet(ctx);
            else if (ctx.setExpr() instanceof FormulationParser.SetExprStackContext stackCtx) {
                if (stackCtx.setDesc() instanceof FormulationParser.SetDescStackContext) {
                    modifySetContent(ctx, stackCtx);
                }
            }
        }
        return super.visitSetDefExpr(ctx);
    }

    @Override
    public Void visitConstraint (FormulationParser.ConstraintContext ctx) {
        String constraintName = extractName(ctx.name.getText());
        if ((targetFunctionalities != null && targetFunctionalities.contains(constraintName)) ||
                (constraintName.equals(targetIdentifier))) {
            if (act == Action.COMMENT_OUT)
                commentOutConstraint(ctx);
        }
        return super.visitConstraint(ctx);
    }

    @Override
    public Void visitPreference (FormulationParser.ObjectiveContext ctx) {
        List<FormulationParser.UExprContext> components = model.findComponentContexts(ctx.nExpr());
        for (FormulationParser.UExprContext subCtx : components) {

            String objectiveName = subCtx.getText();
            if ((targetFunctionalities != null && targetFunctionalities.contains(objectiveName)) ||
                    (objectiveName.equals(targetIdentifier))) {
                if (act == Action.COMMENT_OUT)
                    zeroOutPreference(subCtx);
            }
        }
        return super.visitPreference(ctx);
    }

    // ... keep all existing helper methods (modifyParamContent, commentOutParameter, etc.) ...

    private String modifySetLine (String line, Set<String> values, boolean isAppend) {
        // Find the set content between braces
        int openBrace = line.indexOf('{');
        int closeBrace = line.lastIndexOf('}');
        String separator= ",";
        if (openBrace != -1 && closeBrace != -1) {
            String beforeBraces = line.substring(0, openBrace + 1);
            String afterBraces = line.substring(closeBrace);
            String content = ""; // if Action.SET, then leave content empty string
            if (this.act == Action.APPEND || this.act == Action.DELETE)
                content = line.substring(openBrace + 1, closeBrace).trim();
            for (String val : values) {
                if (isAppend) {
                    // Add value
                    content = content.isEmpty() ? val : content + ", " + val;
                } else {
                    // Remove value
                    content = Arrays.stream(content.split(","))
                            .map(String::trim)
                            .filter(s -> !s.equals(val))
                            .collect(Collectors.joining(", "));
                }
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

        modifiedSource.replace(startIndex, stopIndex + 1, commentedOut.toString());
        modified = true;
    }
//TODO: As planned after Plan A split, preferences wont by removed- but multiplied by a scalar of 0
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
