package Image.Modules.Single;

import Model.Data.Elements.Data.ModelParameter;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;

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
    public @NotNull String getName () {
        return alias;
    }
    @NonNull
    public String getOriginalName(){
        return parameter.getName();
    }
    @NonNull
    public String getTypeString(){
        return parameter.getDataType().toString();
    }
    @NonNull
    public String getData(){
        return parameter.getData();
    }
    @Deprecated(forRemoval = true)
    public @NotNull ModelParameter getParameter () {
        return parameter;
    }
    @Deprecated(forRemoval = true)
    public @NotNull String getAlias() {
        return alias;
    }

    public void setAlias (@NotNull String alias) {
        this.alias = alias;
    }
}
