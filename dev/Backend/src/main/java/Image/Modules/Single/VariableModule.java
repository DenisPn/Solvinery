package Image.Modules.Single;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Objects;

public class VariableModule {

    @NonNull
    private String alias;
    @NonNull
    private final List<String> typeStructure;
    @NonNull
    private final String variableName;
    @NonNull
    private final String objectiveValueAlias;

    public VariableModule(@NonNull String variableName, @NonNull List<String> typeStructure,@Nullable String alias,String objectiveValueAlias) {
        this.variableName = variableName;
        this.typeStructure = typeStructure;
        this.alias = Objects.requireNonNullElse(alias,variableName);
        this.objectiveValueAlias = Objects.requireNonNullElse(objectiveValueAlias,"Objective Value");
    }

    @NonNull
    public List<String> getTypeStructure() {
        return typeStructure;
    }

    @NonNull
    public String getName() {
        return variableName;
    }

    @NonNull
    public String getAlias () {
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

    @NonNull
    public String getObjectiveValueAlias() {
        return objectiveValueAlias;
    }

    /**
     * Sets alias to be the same as the variable name.
     */
    public void removeAlias () {
        this.alias = variableName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VariableModule that = (VariableModule) o;
        return Objects.equals(alias, that.alias) && Objects.equals(typeStructure, that.typeStructure) && Objects.equals(variableName, that.variableName) && Objects.equals(objectiveValueAlias, that.objectiveValueAlias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias, typeStructure, variableName, objectiveValueAlias);
    }
}
