package Image.Modules.Single;

import Model.Data.Elements.Data.ModelParameter;
import jakarta.annotation.Nullable;
import org.springframework.lang.NonNull;

public class ParameterModule {

    @NonNull
    private final ModelParameter parameter;
    @NonNull
    private  String alias;

    public ParameterModule (@NonNull ModelParameter param, @Nullable String alias) {
        parameter = param;
        if(alias==null) this.alias=param.getName();
        else this.alias = alias;
    }

    @SuppressWarnings("unused")
    public ParameterModule (@NonNull ModelParameter param) {
        parameter = param;
        alias = param.getName();
    }
    @NonNull
    public  String getAlias() {
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

    public void setAlias (@NonNull String alias) {
        this.alias = alias;
    }
}
