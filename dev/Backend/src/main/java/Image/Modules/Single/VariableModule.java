package Image.Modules.Single;

import Model.Data.Elements.Variable;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;

import java.util.List;

public class VariableModule {
    @NonNull
    private final Variable variable;
    @NonNull
    private String alias;
    @NonNull
    private final List<String> typeStructureAlias;


    public VariableModule(@NotNull Variable variable,@Nullable String alias,@Nullable List<String> typeStructureAlias) {
        this.variable = variable;
        if(alias==null) this.alias=variable.getName();
        else this.alias = alias;
        if(typeStructureAlias ==null)
            this.typeStructureAlias =variable.getTypeStructure();
        else if(variable.getTypeStructure().size()!= typeStructureAlias.size())
            throw new IllegalArgumentException("Type alias and variable type length do not match");
        else this.typeStructureAlias = typeStructureAlias;
    }
    @Deprecated
    public VariableModule(@NotNull Variable variable,@Nullable String alias) {
        this.variable = variable;
        if(alias==null) this.alias=variable.getName();
        else this.alias = alias;
        this.typeStructureAlias =variable.getTypeStructure();

    }
    public VariableModule(@NotNull Variable variable) {
        this.variable = variable;
        this.alias = variable.getName();
        this.typeStructureAlias =variable.getTypeStructure();

    }

    @NonNull
    public List<String> getTypeStructureAlias() {
        return typeStructureAlias;
    }
    @NonNull
    public List<String> getOriginalTypeStructure() {
        return variable.getTypeStructure();
    }

    @NonNull
    public String getOriginalName() {
        return variable.getName();
    }
    @Deprecated(forRemoval = true)
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
