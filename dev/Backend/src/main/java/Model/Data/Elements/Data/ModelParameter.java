package Model.Data.Elements.Data;

import Model.Data.Types.ModelType;

public class ModelParameter extends DataElement {
    private String data;
    private final boolean auxiliary;
    public ModelParameter (String name, ModelType type, String data) {
        super(name, type);
        this.data = data;
        this.auxiliary =false;
    }
    public ModelParameter (String name, ModelType type, String data, boolean auxiliary) {
        super(name, type);
        this.data = data;
        this.auxiliary = auxiliary;
    }

    public boolean isAuxiliary() {
        return auxiliary;
    }

    public String getData () {
        return data;
    }

    public void setData (String data) {
        this.data = data;
    }
}
