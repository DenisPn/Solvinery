package Model.Parsing;

import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Types.ModelPrimitives;
import Model.Data.Types.ModelType;
import Model.Data.Types.Tuple;
import Model.Model;
import parser.FormulationBaseVisitor;
import parser.FormulationParser;

import java.util.LinkedList;
import java.util.List;

public class TypeVisitor extends FormulationBaseVisitor<Void> {
    private final Model model;
    private ModelType type = ModelPrimitives.UNKNOWN;
    private List<ModelSet> basicSets;
    private List<ModelParameter> basicParams;


    public TypeVisitor (Model model) {
        this.model = model;
        basicSets = new LinkedList<>();
        basicParams = new LinkedList<>();
    }

    // Main visitor methods for type analysis
    @Override
    public Void visitSetExprBin (FormulationParser.SetExprBinContext ctx) {
        // Handle binary set operations (*, +, \, -)
        TypeVisitor leftVisitor = new TypeVisitor(model);
        TypeVisitor rightVisitor = new TypeVisitor(model);

        // Check if the left set is already identified
        ModelSet leftSet = model.getSet(ctx.setExpr(0).getText());
        if (leftSet != null) {
            basicSets.add(leftSet);
            type = leftSet.getDataType(); // Inherit the type
        } else {
            leftVisitor.visit(ctx.setExpr(0));
            basicSets.addAll(leftVisitor.getBasicSets());
            basicParams.addAll(leftVisitor.getBasicParams());
        }

        // Check if the right set is already identified
        ModelSet rightSet = model.getSet(ctx.setExpr(1).getText());
        if (rightSet != null) {
            basicSets.add(rightSet);
            type = rightSet.getDataType(); // Inherit the type
        } else {
            rightVisitor.visit(ctx.setExpr(1));
            basicSets.addAll(rightVisitor.getBasicSets());
            basicParams.addAll(rightVisitor.getBasicParams());
        }

        // Handle type combination based on the operator
        if (ctx.op.getText().equals("*") || ctx.op.getText().equals("cross")) {
            // For Cartesian product, combine types into a tuple
            type = new Tuple();
            if (leftSet != null) {
                ((Tuple) type).append(leftSet.getDataType());
            } else if (leftVisitor.getType() instanceof Tuple) {
                ((Tuple) type).append((Tuple) leftVisitor.getType());
            } else {
                ((Tuple) type).append(leftVisitor.getType());
            }

            if (rightSet != null) {
                ((Tuple) type).append(rightSet.getDataType());
            } else if (rightVisitor.getType() instanceof Tuple) {
                ((Tuple) type).append((Tuple) rightVisitor.getType());
            } else {
                ((Tuple) type).append(rightVisitor.getType());
            }
        } else {
            // For union, difference, etc., types must match
            if (leftSet != null) {
                type = leftSet.getDataType();
            } else if (rightSet != null) {
                type = rightSet.getDataType();
            } else {
                type = leftVisitor.getType();
            }
        }

        return null;
    }

    @Override
    public Void visitVariable (FormulationParser.VariableContext ctx) {
        visit(ctx.sqRef());
        return null;
    }

    @Override
    public Void visitConstraint (FormulationParser.ConstraintContext ctx) {

        for (FormulationParser.ForallContext ctxFA : ctx.forall()) {
            TypeVisitor v = new TypeVisitor(model);
            v.visit(ctxFA);
            basicParams.addAll(v.basicParams);
            basicSets.addAll(v.basicSets);
        }
        TypeVisitor v = new TypeVisitor(model);
        v.visit(ctx.comparison());
        basicParams.addAll(v.basicParams);
        basicSets.addAll(v.basicSets);

        return null;
    }

    @Override
    public Void visitComparisonIfExpr (FormulationParser.ComparisonIfExprContext ctx) {
        this.visit(ctx.ifExpr());
        return null;
    }

    @Override
    public Void visitObjective(FormulationParser.ObjectiveContext ctx) {
        this.visit(ctx.nExpr());
        return null;
    }

    @Override
    public Void visitForall (FormulationParser.ForallContext ctx) {
        this.visit(ctx.condition());
        return null;
    }

    @Override
    public Void visitLongRedExpr (FormulationParser.LongRedExprContext ctx) {
        this.visit(ctx.condition());
        this.visit(ctx.nExpr());
        return null;
    }

    @Override
    public Void visitShortRedExpr (FormulationParser.ShortRedExprContext ctx) {
        this.visit(ctx.index());
        return null;
    }

    @Override
    public Void visitRegIfExpr (FormulationParser.RegIfExprContext ctx) {
        visit(ctx.boolExpr());
        visit(ctx.thenExpr);
        if (ctx.elseExpr != null)
            visit(ctx.elseExpr);

        return null;
    }

    @Override
    public Void visitVarIfExpr (FormulationParser.VarIfExprContext ctx) {
        visit(ctx.boolExpr());
        visit(ctx.thenExpr);
        if (ctx.elseExpr != null)
            visit(ctx.elseExpr);

        return null;
    }
    @Deprecated
    @Override
    //dependencies are not longer used, avoid deletion for now since that will take changing parser def (.g4 file)
    public Void visitSetDescStack (FormulationParser.SetDescStackContext ctx) {

        if (ctx.condition() != null) {
            TypeVisitor elementVisitor = new TypeVisitor(model);
            elementVisitor.visit(ctx.condition());
            /*ModelSet set = new ModelSet("anonymous_set", elementVisitor.type);
            basicSets.add(set);*/
            type = elementVisitor.getType();
        } else if (ctx.csv() != null) {
            // Handle explicit set elements
            TypeVisitor elementVisitor = new TypeVisitor(model);
            elementVisitor.visit(ctx.csv().expr(0));
            /*ModelSet set = new ModelSet("anonymous_set", elementVisitor.type, elementVisitor.basicSets, elementVisitor.basicParams);
            // Add this as a basic set since it's explicitly defined
            basicSets.add(set);*/
            type = elementVisitor.getType();
        } else if (ctx.range() != null) {
            /*ModelSet set = new ModelSet("anonymous_set", ModelPrimitives.INT);
            basicSets.add(set);*/
            type = ModelPrimitives.INT;
            TypeVisitor visitor = new TypeVisitor(model);
            visitor.visit(ctx.range());
         }

        return null;
    }

    @Override
    public Void visitRange (FormulationParser.RangeContext ctx) {

        if (model.getParamsMap().get(ctx.lhs.getText()) != null) {
            basicParams.add(model.getParamsMap().get(ctx.lhs.getText()));
        }
        if (model.getParamsMap().get(ctx.rhs.getText()) != null) {
            basicParams.add(model.getParamsMap().get(ctx.rhs.getText()));
        }
        if (ctx.step != null && model.getParamsMap().get(ctx.step.getText()) != null) {
            basicParams.add(model.getParamsMap().get(ctx.step.getText()));
        }
        type = ModelPrimitives.INT;
        return null;
    }


    @Override
    public Void visitSqRefCsv (FormulationParser.SqRefCsvContext ctx) {
        if (ctx.ID().getText() != null && model.getSet(ctx.ID().getText()) != null) {
            basicSets.add(model.getSet(ctx.ID().getText()));
        } else if (ctx.ID().getText() != null && model.getParameter(ctx.ID().getText()) != null) {
            basicParams.add(model.getParameter(ctx.ID().getText()));
        }

        if (ctx.csv() != null && model.getSet(ctx.csv().getText()) != null) {
            basicSets.add(model.getSet(ctx.csv().getText()));
        } else if (ctx.csv() != null && model.getParameter(ctx.csv().getText()) != null) {
            basicParams.add(model.getParameter(ctx.csv().getText()));
        } else if (ctx.csv() != null) {
            visit(ctx.csv());
        }
        return null;
    }

    //Planning to remove this if it's not too much effort, kept for posterity
    @Deprecated
    @Override
    public Void visitProjFunc (FormulationParser.ProjFuncContext ctx) {
       /* if(false) {
            TypeVisitor visitor = new TypeVisitor(model);
            visitor.visit(ctx.setExpr());
            List<Integer> pointersToSetComp = new LinkedList<>();
            String structureTuple = ctx.tuple().csv().getText();
            for (String tctx : structureTuple.split(",")) {
                pointersToSetComp.add(Integer.parseInt(tctx));
            }

            int count = 0;
            *//*for (ModelSet s : visitor.basicSets) {
                count += s.getStructure().length;
            }*//*
            *//*ModelInput.StructureBlock[] totalStructure = new ModelInput.StructureBlock[count];
            count = 0;
            for (ModelSet s : visitor.basicSets) {
                int i = 1;
                for (ModelInput.StructureBlock sb : s.getStructure()) {
                    totalStructure[count] = new ModelInput.StructureBlock(s, sb == null && s.identifier.equals("anonymous_set") ? i : sb.position);
                    count++;
                    i++;
                }
            }*//*

            *//*ModelInput.StructureBlock[] resultingStructure = new ModelInput.StructureBlock[pointersToSetComp.size()];
            count = 0;
            for (Integer p : pointersToSetComp) {
                resultingStructure[count++] = totalStructure[p - 1];
            }*//*
            ModelSet newSet = new ModelSet("anonymous_set", visitor.getBasicSets(), visitor.getBasicParams(), resultingStructure);
            basicSets.add(newSet);
            if (type == null || type == ModelPrimitives.UNKNOWN)
                type = newSet.getType();
            else if (type instanceof Tuple)
                ((Tuple) type).append(newSet.getType());

            return null;
        }*/
        return null;
    }

    @Override
    public Void visitStrExprToken (FormulationParser.StrExprTokenContext ctx) {
        handleBasicType(ModelPrimitives.TEXT);
        return null;
    }

    @Override
    public Void visitBasicExprToken (FormulationParser.BasicExprTokenContext ctx) {
        if (ctx.FLOAT() != null) {
            handleBasicType(ModelPrimitives.FLOAT);
        } else if (ctx.INT() != null) {
            handleBasicType(ModelPrimitives.INT);
        } else if (ctx.INFINITY() != null) {
            handleBasicType(ModelPrimitives.INFINITY);
        }
        return null;
    }

    @Override
    public Void visitCondition (FormulationParser.ConditionContext ctx) {
        this.visit(ctx.setExpr());
        if (ctx.boolExpr() != null)
            this.visit(ctx.boolExpr());
        return null;
    }

    @Override
    public Void visitComparisonStrExpr (FormulationParser.ComparisonStrExprContext ctx) {
        this.visit(ctx.lhs);
        this.visit(ctx.rhs);
        return null;
    }

    @Override
    public Void visitBoolExprBin (FormulationParser.BoolExprBinContext ctx) {
        this.visit(ctx.boolExpr(0));
        this.visit(ctx.boolExpr(1));
        return null;
    }

    @Override
    public Void visitTuple (FormulationParser.TupleContext ctx) {
        Tuple tupleType = new Tuple();

        // Visit each element in the tuple
        if (ctx.csv() != null) {
            for (FormulationParser.ExprContext ec : ctx.csv().expr()) {
                TypeVisitor elementVisitor = new TypeVisitor(model);
                elementVisitor.visit(ec);
                if (elementVisitor.getType() instanceof Tuple) {
                    tupleType.append((Tuple) elementVisitor.getType());
                } else {
                    tupleType.append(elementVisitor.getType());
                }
            }
        }

        type = tupleType;
        return null;
    }

    private void handleBasicType (ModelType newType) {
        if (type == ModelPrimitives.UNKNOWN) {
            type = newType;
        } else if (type instanceof Tuple) {
            ((Tuple) type).append(newType);
        }
    }

    // Getter methods
    public ModelType getType () {
        return type;
    }

    public List<ModelSet> getBasicSets () {
        return basicSets;
    }

    public List<ModelParameter> getBasicParams () {
        return basicParams;
    }
}
