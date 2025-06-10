package Image.Modules.Single;

import Model.Data.Elements.Data.ModelParameter;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public class ParameterModule {

    @NotNull
    private final ModelParameter parameter;
    @NotNull
    private  String alias;

    public ParameterModule (@NotNull ModelParameter param, @Nullable String alias) {
        parameter = param;
        if(alias==null) this.alias=param.getName();
        else this.alias = alias;
    }

    @SuppressWarnings("unused")
    public ParameterModule (@NotNull ModelParameter param) {
        parameter = param;
        alias = param.getName();
    }
    public @NotNull ModelParameter getParameter () {
        return parameter;
    }
    public @NotNull String getAlias() {
        return alias;
    }

    public void setAlias (@NotNull String alias) {
        this.alias = alias;
    }
}
