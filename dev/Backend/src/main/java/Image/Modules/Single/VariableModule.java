package Image.Modules.Single;

import Model.Data.Elements.Variable;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public class VariableModule {
    @NotNull
    private final Variable variable;
    @NotNull
    private String alias;



    public VariableModule(@NotNull Variable variable,@Nullable String alias) {
        this.variable = variable;
        if(alias==null) this.alias=variable.getName();
        else this.alias = alias;
    }
    public VariableModule(@NotNull Variable variable) {
        this.variable = variable;
        this.alias = variable.getName();
    }

    public @NotNull Variable getVariable () {
        return variable;
    }

    public @NotNull String getAlias () {
        return alias;
    }

    /**
     * Sets the alias for the variable. If the provided alias is null or empty,
     * the alias will be reset to the variable's original name.
     *
     * @param alias The new alias for the variable. If null or empty, the alias will be reset.
     */
    public void setAlias (@Nullable String alias) {
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
