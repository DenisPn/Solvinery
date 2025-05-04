package Image.Modules.Single;

import Model.Data.Elements.Data.ModelParameter;

public class ParameterModule {
    private final ModelParameter parameter;
    private  String alias;

    public ParameterModule (ModelParameter param, String alias) {
        parameter = param;
        this.alias = alias;
    }

    public ParameterModule (ModelParameter param) {
        parameter = param;
        alias = param.getName();
    }
    public ModelParameter getParameter () {
        return parameter;
    }
    public String getAlias() {
        return alias;
    }

    public void setAlias (String alias) {
        this.alias = alias;
    }
}
