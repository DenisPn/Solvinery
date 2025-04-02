package Model.Deprecated.Parsing;

import Model.*;
import parser.FormulationBaseVisitor;
import parser.FormulationParser;

import java.util.ArrayList;
import java.util.List;

public class CollectorVisitor extends FormulationBaseVisitor<Void> {

    private final Model model;

    public CollectorVisitor (Model model) {
        this.model = model;

    }

    public Void visitParamDecl (FormulationParser.ParamDeclContext ctx) {
        String paramName = extractName(ctx.sqRef().getText());
        TypeVisitor typer = new TypeVisitor(model);
        typer.visit(ctx.expr());
        ModelParameter param = new ModelParameter(paramName,
                typer.getType(),
                typer.getBasicSets(),
                typer.getBasicParams());
            param.setValue(ctx.expr().getText());
        model.getParamsMap().put(paramName, param);
        return super.visitParamDecl(ctx);
    }

    @Override
    public Void visitSetDecl (FormulationParser.SetDeclContext ctx) {
        String setName = extractName(ctx.sqRef().getText());
        
        model.getSetsMap().put(setName, new ModelSet(setName, ModelPrimitives.UNKNOWN));
        return super.visitSetDecl(ctx);
    }

    @Override
    public Void visitSetDefExpr (FormulationParser.SetDefExprContext ctx) {
        String setName = extractName(ctx.sqRef().getText());

        TypeVisitor typer = new TypeVisitor(model);
        typer.visit(ctx.setExpr());
        ModelSet set = new ModelSet(setName, typer.getType(), typer.getBasicSets(), typer.getBasicParams());
        List<String> elements = parseSetElements(ctx.setExpr());
        if(elements!=null) {
            set.setElements(elements);
            model.getSetsMap().put(setName, set);
        }
        return super.visitSetDefExpr(ctx);
    }

    @Override
    public Void visitConstraint (FormulationParser.ConstraintContext ctx) {
        String constName = extractName(ctx.name.getText());
        TypeVisitor visitor = new TypeVisitor(model);
        visitor.visit(ctx);
        model.getConstraintsMap().put(constName, new ModelConstraint(constName, visitor.getBasicSets(), visitor.getBasicParams()));
        return super.visitConstraint(ctx);
    }

    @Override
    public Void visitObjective (FormulationParser.ObjectiveContext ctx) {
        List<FormulationParser.UExprContext> components = model.findComponentContexts(ctx.nExpr());

        for (FormulationParser.UExprContext expressionComponent : components) {
            String name = expressionComponent.getText();
            // Create a parse tree for the specific component
            //ParseTree componentParseTree = parseComponentExpression(expressionComponent);
            TypeVisitor visitor = new TypeVisitor(model);
            visitor.visit(expressionComponent);

            model.getPreferencesMap().put(expressionComponent.getText(),
                    new ModelPreference(expressionComponent.getText(),
                            visitor.getBasicSets(),
                            visitor.getBasicParams())
            );
        }

        return super.visitObjective(ctx);
    }

    public Void visitVariable (FormulationParser.VariableContext ctx) {
        String varName = extractName(ctx.sqRef().getText());
        TypeVisitor visitor = new TypeVisitor(model);
        visitor.visit(ctx);
        boolean isComplex = true;
        if (ctx.sqRef() instanceof FormulationParser.SqRefCsvContext) {
            isComplex = ((FormulationParser.SqRefCsvContext) (ctx.sqRef())).csv() == null ? false : true;
        }
        model.getVariablesMap().put(varName, new ModelVariable(varName, visitor.getBasicSets(), visitor.getBasicParams(), isComplex));
        return super.visitVariable(ctx);
    }

    private String extractName (String sqRef) {
        // Handle indexed sets by taking the base name
        int bracketIndex = sqRef.indexOf('[');
        return bracketIndex == -1 ? sqRef : sqRef.substring(0, bracketIndex);
    }
    //Refactor below, kept this old one for posterity.
    private List<String> parseSetElementsOld (FormulationParser.SetExprContext ctx) {
        List<String> elements = new ArrayList<>();

        if (ctx instanceof FormulationParser.SetExprStackContext) {
            FormulationParser.SetExprStackContext stackCtx = (FormulationParser.SetExprStackContext) ctx;
            if (stackCtx.setDesc() != null) {
                // Handle explicit set descriptions by looking at the definition in the parse tree
                if (stackCtx.setDesc() instanceof FormulationParser.SetDescStackContext) {
                    FormulationParser.SetDescStackContext descCtx = (FormulationParser.SetDescStackContext) stackCtx.setDesc();
                    if (descCtx.csv() != null) {
                        String csvText = descCtx.csv().getText();

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
                        if (currentElement.length() > 0) {
                            elements.add(currentElement.toString().trim());
                        }
                    } else { // If no CSV, the set is not explicitly defined with elements.
                        elements = null;
                    }
                } else { // If not a SetDescStackContext, the set definition does not describe explicit elements.
                    elements = null;
                }
            } else
                // If setDesc() is null, this is likely a function of other sets, rather than explicit elements.
                elements = null;
        }

        return elements;
    }
    private List<String> parseSetElements (FormulationParser.SetExprContext ctx) {
        if (ctx instanceof FormulationParser.SetExprStackContext stackCtx) {
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
