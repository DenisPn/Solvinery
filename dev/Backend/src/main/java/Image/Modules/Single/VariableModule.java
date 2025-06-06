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

    /**
     * Sets the alias for the variable. If the provided alias is null or empty,
     * the alias will be reset to the variable's original name.
     *
     * @param alias The new alias for the variable. If null or empty, the alias will be reset.
     */
    public void setAlias (String alias) {
        if(alias == null || alias.isEmpty())
            removeAlias();
        else this.alias = alias;
    }

    /**
     * Sets alias to be the same as the variable name.
     */
    public void removeAlias () {
        this.alias = variable.getName();
    }
}
