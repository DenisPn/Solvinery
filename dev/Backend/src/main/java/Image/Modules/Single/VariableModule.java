package Image.Modules.Single;

import Model.Data.Elements.Variable;

public class VariableModule {

    private final Variable variable;

    private String alias;



    public VariableModule(Variable variable,String alias) {
        this.variable = variable;
        this.alias = alias;
    }
    public VariableModule(Variable variable) {
        this.variable = variable;
        this.alias = variable.getName();
    }

    public Variable getVariable () {
        return variable;
    }

    public String getAlias () {
        return alias;
    }

    public void setAlias (String alias) {
        this.alias = alias;
    }
}
