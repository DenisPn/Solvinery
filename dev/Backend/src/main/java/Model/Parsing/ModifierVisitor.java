package Model.Parsing;

import Exceptions.InternalErrors.ModelExceptions.InvalidModelStateException;
import Model.Data.Elements.Element;
import Model.Data.Elements.Operational.Preference;
import Model.Model;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.Interval;
import org.springframework.lang.NonNull;
import parser.FormulationBaseVisitor;
import parser.FormulationParser;

import java.util.List;
import java.util.Objects;

public class ModifierVisitor extends FormulationBaseVisitor<Void> {
    @NonNull
    private final Model model;
    @NonNull
    private final String originalSource;
    private boolean modified = false;
    @NonNull
    private final TokenStreamRewriter rewriter;

    // Original constructor for backward compatibility
    public ModifierVisitor(@NonNull Model model, @NonNull String originalSource){
        this.model = model;
        this.rewriter = new TokenStreamRewriter(model.getTokens());
        this.originalSource = originalSource;
    }


    private void modifyParamContent (@NonNull FormulationParser.ExprContext ctx, String value) {
        rewriter.replace(ctx.start,ctx.stop,value);
    }

    private void modifySetContent (@NonNull FormulationParser.SetDefExprContext ctx,
                                   @NonNull List<String> values) {
        int startIndex = ctx.start.getStartIndex();
        int stopIndex = ctx.stop.getStopIndex();
        String originalLine = originalSource.substring(startIndex, stopIndex + 1);
        String modifiedLine = modifySetLine(originalLine, values);
        rewriter.replace(ctx.start,ctx.stop,modifiedLine);
    }

    @Override
    public Void visitParamDecl (@NonNull FormulationParser.ParamDeclContext ctx) {
        String paramName = extractName(ctx.sqRef().getText());
        if (model.isModified(paramName, Element.ElementType.MODEL_PARAMETER)) {
                modifyParamContent(ctx.expr(),model.getParameterFromAll(paramName).getData());
        }
        return super.visitParamDecl(ctx);
    }

    @Override
    public Void visitSetDefExpr (@NonNull FormulationParser.SetDefExprContext ctx) {
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
    public Void visitConstraint (@NonNull FormulationParser.ConstraintContext ctx) {
        String constraintName = extractName(ctx.name.getText());
        if (model.isModified(constraintName, Element.ElementType.CONSTRAINT)) {
                commentOutConstraint(ctx);
        }
        return super.visitConstraint(ctx);
    }

    @Override
    public Void visitObjective(@NonNull FormulationParser.ObjectiveContext ctx) {
        List<FormulationParser.UExprContext> components = model.findComponentContexts(ctx.nExpr());
        for (FormulationParser.UExprContext subCtx : components) {
            String preferenceName = subCtx.start.getInputStream()
                    .getText(new Interval(subCtx.start.getStartIndex(),
                            subCtx.stop.getStopIndex()));
            if (!model.hasPreference(preferenceName)) {
                Preference preference = model.getModifiedPreferences().stream()
                        .filter(pref -> pref.getName().contains(preferenceName))
                        .findFirst()
                        .orElse(null); //the new preference is: `(preferenceName) * someScalar`, stream finds a pref that contains preferenceName
                Objects.requireNonNull(preference, "Invalid Preference call in model:\n" + preferenceName);
                replacePreference(subCtx,preference.getName());
            }
        }
        return super.visitObjective(ctx);
    }

    @NonNull
    private String modifySetLine (@NonNull String line, @NonNull List<String> values) {
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

    private void commentOutConstraint (@NonNull FormulationParser.ConstraintContext ctx) {
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
                commentedOut.append(initialIndent).append("# ").append(line, 0, line.length() - 1).append("\n");
            } else {
                commentedOut.append(initialIndent).append("# ").append(line);
            }
        }

        rewriter.replace(ctx.start, ctx.stop, commentedOut.toString());
        modified = true;
    }
    private void replacePreference(@NonNull FormulationParser.UExprContext ctx, String newPreference) {
        rewriter.replace(ctx.start, ctx.stop, newPreference);
    }


    @NonNull
    private String extractName (@NonNull String sqRef) {
        int bracketIndex = sqRef.indexOf('[');
        return bracketIndex == -1 ? sqRef : sqRef.substring(0, bracketIndex);
    }

    public boolean isModified () {
        return modified;
    }

    public String getModifiedSource () {
        return rewriter.getText();
    }

    /**
     * Removes all whitespace characters from a string.
     * @param str The string to be modified.
     * @return The modified string with all whitespace characters removed.
     */
    @NonNull
    private static String removeWhitespaces(@NonNull String str){
        return str.replaceAll("\\s+","");
    }
}
