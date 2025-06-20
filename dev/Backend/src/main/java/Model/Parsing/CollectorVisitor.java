package Model.Parsing;

import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Variable;
import Model.Data.Types.ModelPrimitives;
import Model.Model;
import org.antlr.v4.runtime.misc.Interval;
import org.springframework.lang.NonNull;
import parser.FormulationBaseVisitor;
import parser.FormulationParser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CollectorVisitor extends FormulationBaseVisitor<Void> {
    @NonNull
    private final Model model;

    public CollectorVisitor (@NonNull Model model) {
        this.model = model;

    }

    public Void visitParamDecl (@NonNull FormulationParser.ParamDeclContext ctx) {
        String paramName = extractName(ctx.sqRef().getText());
        TypeVisitor typer = new TypeVisitor(model);
        typer.visit(ctx.expr());
        ModelParameter param = new ModelParameter(paramName,
                typer.getType(),ctx.expr().getText());
        model.getParamsMap().put(paramName, param);
        return super.visitParamDecl(ctx);
    }

    @Override
    public Void visitSetDecl (@NonNull FormulationParser.SetDeclContext ctx) {
        String setName = extractName(ctx.sqRef().getText());
        model.getSetsMap().put(setName, new ModelSet(setName, ModelPrimitives.UNKNOWN,new LinkedList<>()));
        return super.visitSetDecl(ctx);
    }

    @Override
    public Void visitSetDefExpr (@NonNull FormulationParser.SetDefExprContext ctx) {
        String setName = extractName(ctx.sqRef().getText());

        TypeVisitor typer = new TypeVisitor(model);
        typer.visit(ctx.setExpr());
        String expr= ctx.setExpr().getText();
        List<String> elements = parseSetElements(ctx.setExpr());
        if(elements != null) {
            //compute if absent is same as putIfAbsent, but creates a new set if key is absent
            model.getSetsMap().computeIfAbsent(setName,
                    ignored -> new ModelSet(setName, typer.getType(),elements));
        }
        else {
            model.getSetsMap().computeIfAbsent(setName,
                    ignored -> new ModelSet(setName, typer.getType(),false));
        }
        return super.visitSetDefExpr(ctx);
    }

    @Override
    public Void visitConstraint (@NonNull FormulationParser.ConstraintContext ctx) {
        String constName = extractName(ctx.name.getText());
        TypeVisitor visitor = new TypeVisitor(model);
        visitor.visit(ctx);
        model.getConstraintsMap().put(constName, new Constraint(constName));
        return super.visitConstraint(ctx);
    }

    @Override
    public Void visitObjective(@NonNull FormulationParser.ObjectiveContext ctx) {
        List<FormulationParser.UExprContext> components = model.findComponentContexts(ctx.nExpr());

        for (FormulationParser.UExprContext expressionComponent : components) {
       //     String body = expressionComponent.getText();
            String body = expressionComponent.start.getInputStream()
                    .getText(new Interval(expressionComponent.start.getStartIndex(),
                            expressionComponent.stop.getStopIndex()));

            // Create a parse tree for the specific component
            //ParseTree componentParseTree = parseComponentExpression(expressionComponent);
            TypeVisitor visitor = new TypeVisitor(model);
            visitor.visit(expressionComponent);

            model.getUneditedPreferences().add(body);
        }

        return super.visitObjective(ctx);
    }

    public Void visitVariable (@NonNull FormulationParser.VariableContext ctx) {
        String varName = extractName(ctx.sqRef().getText());
        TypeVisitor visitor = new TypeVisitor(model);
        visitor.visit(ctx);
        List<String> types = new LinkedList<>();
        //List<String> basicSets = new LinkedList<>();
        for(ModelSet set : visitor.getBasicSets()) {
         types.addAll(set.getDataType().typeList());
         //basicSets.add(set.getName());
        }
        model.getVariablesMap().put(varName, new Variable(varName,types));
        return super.visitVariable(ctx);
    }

    @NonNull
    private String extractName (@NonNull String sqRef) {
        // Handle indexed sets by taking the base name
        int bracketIndex = sqRef.indexOf('[');
        return bracketIndex == -1 ? sqRef : sqRef.substring(0, bracketIndex);
    }

    private List<String> parseSetElements (FormulationParser.SetExprContext ctx) {

        if (ctx instanceof FormulationParser.SetExprStackContext stackCtx) {
            if (stackCtx.setDesc() instanceof FormulationParser.SetDescEmptyContext) {
                return new ArrayList<>();
            }
            if (stackCtx.setDesc() instanceof FormulationParser.SetDescStackContext descCtx) {
                if (descCtx.csv() != null) {
                    String csvText = descCtx.csv().getText();
                    List<String> elements = new ArrayList<>();

                    // Explicit elements are defined using a comma-separated value (CSV) structure.
                    // Split the CSV text by commas, but ignore commas within angle brackets
                    StringBuilder currentElement = new StringBuilder();
                    boolean inAngleBrackets = false;

                    for (int i = 0; i < csvText.length(); i++) {
                        char c = csvText.charAt(i);

                        if (c == '<') {
                            inAngleBrackets = true;
                            currentElement.append(c);
                        } else if (c == '>') {
                            inAngleBrackets = false;
                            currentElement.append(c);
                        } else if (c == ',' && !inAngleBrackets) {
                            // When encountering a comma outside of angle brackets, add the current element to the list
                            elements.add(currentElement.toString().trim());
                            currentElement.setLength(0); // Reset the current element
                        } else {
                            currentElement.append(c);
                        }
                    }
                    // Add the last element if it exists
                    if (!currentElement.isEmpty()) {
                        elements.add(currentElement.toString().trim());
                    }
                    return elements; // Return the list if explicit elements are present
                }
            }
        }
        return null; // Return null if no explicit elements are defined
    }
}
