package Image.Modules.Single;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

public class VariableModule {

    @NonNull
    private String alias;
    @NonNull
    private final List<String> typeStructure;
    @NonNull
    private final String variableName;

    public VariableModule(@NonNull String variableName, @NonNull List<String> typeStructure,@Nullable String alias) {
        this.variableName = variableName;
        this.typeStructure = typeStructure;
        if(alias==null) this.alias=variableName;
        else this.alias = alias;
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

    /**
     * Sets alias to be the same as the variable name.
     */
    public void removeAlias () {
        this.alias = variableName;
    }
}
