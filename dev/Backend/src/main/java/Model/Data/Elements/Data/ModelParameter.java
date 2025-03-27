package Model.Data.Elements.Data;

import Model.ModelType;

public class ModelParameter extends DataElement {
    private String data;
    public ModelParameter (String name, ModelType type, String data) {
        super(name, type);
        this.data = data;
    }

    public String getData () {
        return data;
    }

}
